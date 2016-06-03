package com.matthijs.rtpstreamingclient;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Matthijs Overboom on 31-5-16.
 */
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

        //Hide actionbar and set fullscreen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
        if(bitmap != null) {
            frame.setImageBitmap(bitmap);
        }
    }

    @Override
    public void enableSetupButton() {
        buttonSetup.setEnabled(true);
    }

    @Override
    public void enablePlayButtonDisableSetup() {
        buttonSetup.setEnabled(false);
        buttonPlayPause.setEnabled(true);
        buttonTear.setEnabled(true);
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