package com.matthijs.rtpstreamingclient;

/**
 * Created by Matthijs Overboom on 31-5-16.
 */
public class RTPpacket{

    //size of the RTP header:
    static int HEADER_SIZE = 12;

    //Fields that compose the RTP header
    public int Version;
    public int Padding;
    public int Extension;
    public int CC;
    public int Marker;
    public int PayloadType;
    public int SequenceNumber;
    public int TimeStamp;
    public int Ssrc;

    //Bitstream of the RTP header
    public byte[] header;

    //size of the RTP payload
    public int payload_size;
    //Bitstream of the RTP payload
    public byte[] payload;

    /**
     * Constructor
     *
     * @param packet byte[] received bytes from server
     * @param packet_size content length
     */
    public RTPpacket(byte[] packet, int packet_size)
    {
        //fill default fields:
        Version = 2;
        Padding = 0;
        Extension = 0;
        CC = 0;
        Marker = 0;
        Ssrc = 0;

        //check if total packet size is lower than the header size
        if (packet_size >= HEADER_SIZE)
        {
            //get the header bitsream:
            header = new byte[HEADER_SIZE];
            for (int i=0; i < HEADER_SIZE; i++)
                header[i] = packet[i];

            //get the payload bitstream:
            payload_size = packet_size - HEADER_SIZE;
            payload = new byte[payload_size];
            for (int i=HEADER_SIZE; i < packet_size; i++)
                payload[i-HEADER_SIZE] = packet[i];

            //interpret the changing fields of the header:
            PayloadType = header[1] & 127;
            SequenceNumber = unsigned_int(header[3]) + 256*unsigned_int(header[2]);
            TimeStamp = unsigned_int(header[7]) + 256*unsigned_int(header[6]) + 65536*unsigned_int(header[5]) + 16777216*unsigned_int(header[4]);
        }
    }

    /**
     * Returns the payload's size
     * and sets the payload itself to the byte[] given as argument
     *
     * @param data byte[] to set the payload to
     * @return payload size
     */
    public int getpayload(byte[] data) {

        for (int i = 0; i < payload_size; i++)
            data[i] = payload[i];

        return (payload_size);
    }

    /**
     * Returns the payload length
     *
     * @return int payload length
     */
    public int getpayload_length() {
        return (payload_size);
    }

    /**
     * Return the full length of the RTPpacket
     * payload size + header size
     *
     * @return int full packet size
     */
    public int getlength() {
        return (payload_size + HEADER_SIZE);
    }

    /**
     * Returns the packet size
     * and sets the packet itself to the byte[] given as argument
     *
     * @param packet byte[] to set packet to
     * @return int packet size
     */
    public int getpacket(byte[] packet) {
        // construct the packet = header + payload
        for (int i = 0; i < HEADER_SIZE; i++)
            packet[i] = header[i];
        for (int i = 0; i < payload_size; i++)
            packet[i + HEADER_SIZE] = payload[i];

        // return total size of the packet
        return (payload_size + HEADER_SIZE);
    }

    /**
     * Returns the timestamp on which the packet was created
     * at the server
     *
     * @return int timestamp
     */
    public int gettimestamp() {
        return (TimeStamp);
    }

    /**
     * Returns the packet's sequence number
     *
     * @return int sequence number
     */
    public int getsequencenumber() {
        return (SequenceNumber);
    }

    /**
     * Returns the payload type
     * (in this project we use type 26, jpeg.
     *
     * @return int payload type
     */
    public int getpayloadtype() {
        return (PayloadType);
    }

    /**
     * Prints the header to the console without SSRC
     * SSRC is not actively used in this project
     */
    public void printheader() {

        for (int i = 0; i < (HEADER_SIZE - 4); i++) {
            for (int j = 7; j >= 0; j--)
                if (((1 << j) & header[i]) != 0)
                    System.out.print("1");
                else
                    System.out.print("0");
            System.out.print(" ");
        }

        System.out.println();
    }

    // return the unsigned value of 8-bit integer nb
    static int unsigned_int(int nb) {
        if (nb >= 0)
            return (nb);
        else
            return (256 + nb);
    }

}
