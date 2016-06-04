package com.matthijs.rtpstreamingclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Matthijs Overboom on 23-5-16.
 */
public class VideoStream {
    DatagramPacket rcvdp; //UDP packet received from the server
    DatagramSocket RTPsocket; //socket to be used to send and receive UDP packets
    static int RTP_RCV_PORT = 25000; //port where the client will receive the RTP packets

    Timer timer; //timer used to receive data from the UDP socket
    byte[] buf; //buffer used to store data received from the server

    //rtsp states
    final static int INIT = 0;
    final static int READY = 1;
    final static int PLAYING = 2;
    static int state; //RTSP state == INIT or READY or PLAYING
    Socket RTSPsocket; //socket used to send/receive RTSP messages
    //input and output stream filters
    static BufferedReader RTSPBufferedReader;
    static BufferedWriter RTSPBufferedWriter;
    static String VideoFileName; //video file to request to the server
    int RTSPSeqNb = 0; //Sequence number of RTSP messages within the session
    int RTSPid = 0; //ID of the RTSP session (given by the RTSP Server)

    final static String CRLF = "\r\n";

    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video

    private VideoScreen videoScreen;
    private Handler handler;
    private Video video;

    public VideoStream(Video video) {
        this.video = video;
        buf = new byte[64000];
        handler = new Handler();
        new Thread() {
            public void run() {
                Log.d("RTP", "Running initialize thread");
                initialize();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        videoScreen.enableSetupButton();
                    }
                });
            }
        }.start();
    }

    private void initialize() {
        try {
            //SETUP RTSP connection
            InetAddress ServerIPAddr = video.getIp();
            //RTSP port
            int RTSP_server_port = video.getPort();
            //get video filename to request:
            VideoFileName = video.getName();

            //Establish a TCP connection with the server to exchange RTSP messages
            RTSPsocket = new Socket(ServerIPAddr, RTSP_server_port);

            //Set input and output stream filters:
            RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPsocket.getInputStream()));
            RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPsocket.getOutputStream()));
            //init RTSP state:
            state = INIT;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        if (state == INIT)
        {
            new Thread() {
                @Override
                public void run() {
                    try {
                        RTPsocket = new DatagramSocket(RTP_RCV_PORT);
                        RTPsocket.setSoTimeout(5);
                        Log.d("RTP", "Created new RTPSocket: " + + RTPsocket.getLocalPort());
                    } catch (IOException ioE) {
                        System.out.println(ioE.getMessage());
                        ioE.printStackTrace();
                    }

                    //init RTSP sequence number
                    RTSPSeqNb = 1;

                    //Send SETUP message to the server
                    send_RTSP_request("SETUP");

                    //Wait for the response
                    //Changed test to == 200 instead of != 200 to make sure we get a OK response
                    if (parse_server_response() == 200) {
                        //change RTSP state and print new state
                        state = READY;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                videoScreen.enablePlayButtonDisableSetup();
                            }
                        });
                        System.out.println("New RTSP state: " + state);
                    } else
                    {
                        System.out.println("Invalid Server Response");
                    }
                }
            }.start();
        }
    }

    public void play() {
        if (state == READY) {
            new Thread() {
                public void run() {
                    System.out.println("Play Button pressed !");
                    RTSPSeqNb++;

                    //Send PLAY message to the server
                    send_RTSP_request("PLAY");

                    //Wait for the response
                    if (parse_server_response() == 200) {
                        //change RTSP state and print out new state
                        state = PLAYING;
                        Log.d("RTP", "New RTSP state: " + state);

                        //Setup and start timer
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                final Bitmap receivedBitmap = receivePacktet();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        videoScreen.drawFrame(receivedBitmap);
                                    }
                                });
                            }
                        }, 0, 100);
                        state = PLAYING;
                    } else {
                        System.out.println("Invalid Server Response");
                    }
                }//else if state != READY then do nothing
            }.start();
        }
    }

    public void pause() {
        if (state == PLAYING) {

            new Thread() {
                @Override
                public void run() {
                    //increase RTSP sequence number
                    RTSPSeqNb++;

                    //Send PAUSE message to the server
                    send_RTSP_request("PAUSE");

                    //Wait for the response
                    if (parse_server_response() == 200) {
                        //change RTSP state and print out new state
                        //NO PAUSE STATE?
                        state = READY;
                        Log.d("RTP", "New RTSP state: " + state);

                        //stop the timer
                        timer.cancel();
                    } else
                    {
                        System.out.println("Invalid Server Response");
                    }
                }
            }.start();
        }
    }

    public void teardown() {
        RTSPSeqNb++;
        new Thread() {
            @Override
            public void run() {
                send_RTSP_request("TEARDOWN");

                //Wait for the response
                if (parse_server_response() == 200) {
                    state = INIT;
                    System.out.println("New RTSP state: " + state);
                    timer.cancel();
                    closeStreams();
                } else
                {
                    System.out.println("Invalid Server Response");
                }
                videoScreen.finishVideoActivity();
            }
        }.start();
    }

    private void closeStreams() {
        try {
            RTSPBufferedReader.close();
            RTSPBufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RTPsocket.close();
    }

    private Bitmap receivePacktet() {
        //Construct a DatagramPacket to receive data from the UDP socket
        rcvdp = new DatagramPacket(buf, buf.length);
        Bitmap bitmap = null;
        try{
            //receive the DP from the socket:
            RTPsocket.receive(rcvdp);

            //create an RTPpacket object from the DP
            RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

            //print important header fields of the RTP packet received:
            System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+rtp_packet.getpayloadtype());

            //print header bitstream:
            rtp_packet.printheader();

            //get the payload bitstream from the RTPpacket object
            int payload_length = rtp_packet.getpayload_length();
            byte [] payload = new byte[payload_length];
            rtp_packet.getpayload(payload);
            bitmap = BitmapFactory.decodeByteArray(payload, 0, payload_length);
        }
        catch (InterruptedIOException iioe){
            //System.out.println("Nothing to read");
        }
        catch (IOException ioe) {
            System.out.println("Exception caught: "+ioe);
            ioe.printStackTrace();
        }
        return bitmap;
    }

    public void setVideoScreen(VideoScreen videoScreen) {
        this.videoScreen = videoScreen;
    }

    public interface VideoScreen {
        void drawFrame(Bitmap bitmap);
        void enableSetupButton();
        void enablePlayButtonDisableSetup();
        void finishVideoActivity();
    }

    private int parse_server_response()
    {
        int reply_code = 0;

        try{
            //parse status line and extract the reply_code:
            String StatusLine = RTSPBufferedReader.readLine();
            //System.out.println("RTSP Client - Received from Server:");
            System.out.println(StatusLine);

            StringTokenizer tokens = new StringTokenizer(StatusLine);
            tokens.nextToken(); //skip over the RTSP version
            reply_code = Integer.parseInt(tokens.nextToken());

            //if reply code is OK get and print the 2 other lines
            if (reply_code == 200)
            {
                String SeqNumLine = RTSPBufferedReader.readLine();
                System.out.println(SeqNumLine);

                String SessionLine = RTSPBufferedReader.readLine();
                System.out.println(SessionLine);

                //if state == INIT gets the Session Id from the SessionLine
                tokens = new StringTokenizer(SessionLine);
                tokens.nextToken(); //skip over the Session:
                RTSPid = Integer.parseInt(tokens.nextToken());
                System.out.println("RTSPid = " + RTSPid);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception caught: "+ex);
            System.exit(0);
        }

        return(reply_code);
    }

    private void send_RTSP_request(String request_type)
    {
        try{
            System.out.println("Sending RTSP request asking server to " + request_type);
            if(request_type.equals("SETUP")) {
                //Using whitespace a delimiter since StringTokenizer usus this a default
                //But the comment above suggests we put each 'token' on a new line?
                RTSPBufferedWriter.write(request_type + " " + VideoFileName + " RTSP/1.0" + CRLF);
                RTSPBufferedWriter.write("CSeq: " + " " + RTSPSeqNb + CRLF);
                RTSPBufferedWriter.write("Transport: RTP/UDP; client_port= " + RTP_RCV_PORT + CRLF);
            } else if(request_type.equals("PLAY") || request_type.equals("PAUSE") || request_type.equals("TEARDOWN")) {
                RTSPBufferedWriter.write(request_type + " " + VideoFileName + " RTSP/1.0" + CRLF);
                RTSPBufferedWriter.write("CSeq: " + RTSPSeqNb + CRLF);
                RTSPBufferedWriter.write("Session: " + RTSPid + CRLF);
            }
            RTSPBufferedWriter.flush();
        }
        catch(Exception ex)
        {
            System.out.println("Exception caught: "+ex);
            ex.printStackTrace();
        }
    }
}