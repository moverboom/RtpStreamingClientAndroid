package com.matthijs.rtpstreamingclient;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity implements VideoStream.VideoScreen
{
    Button buttonSetup;
    Button buttonPlayPause;
    Button buttonTear;
    ImageView frame;
    VideoStream videoStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        buttonSetup = (Button)findViewById(R.id.btnSetup);
        buttonPlayPause = (Button)findViewById(R.id.btnPlayPause);
        buttonTear = (Button)findViewById(R.id.btnTear);
        frame = (ImageView)findViewById(R.id.frame);

        videoStream = new VideoStream();
        videoStream.setVideoScreen(this);

        buttonSetup.setOnClickListener(new setupListener());
        buttonPlayPause.setOnClickListener(new playPauseListener());
        buttonTear.setOnClickListener(new tearListener());
    }

    @Override
    public void drawFrame(Bitmap bitmap) {
        Log.d("RTP", "Drew frame");
        frame.setImageBitmap(bitmap);
    }

    @Override
    public void enableSetupTeardownButtons() {
        buttonSetup.setEnabled(true);
        buttonTear.setEnabled(true);
    }

    @Override
    public void enablePlayButtonDisableSetup() {
        buttonSetup.setEnabled(false);
        buttonPlayPause.setEnabled(true);
    }

    @Override
    public void finishVideoActivity() {
        finish();
    }

    private class setupListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            videoStream.setup();
        }
    }

    private class playPauseListener implements View.OnClickListener {
        boolean isPlaying;
        @Override
        public void onClick(View view) {
            if(!isPlaying) {
                videoStream.play();
                buttonPlayPause.setText("Pause");
                isPlaying = true;
            } else {
                videoStream.pause();
                buttonPlayPause.setText("Play");
                isPlaying = false;
            }
        }
    }

    private class tearListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            videoStream.teardown();
        }
    }
}