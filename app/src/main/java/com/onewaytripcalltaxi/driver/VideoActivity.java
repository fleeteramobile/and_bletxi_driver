package com.onewaytripcalltaxi.driver;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoActivity extends MainActivity{

    VideoView videoView;
    TextView okbtn;
    @Override
    public int setLayout() {
        return R.layout.video_dialog;
    }

    @Override
    public void Initialize() {

        videoView =findViewById(R.id.vdVw);
         okbtn = findViewById(R.id.okbtn);


      //  MediaController mediaController= new MediaController(this);
       // mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.taxiexample);
       // videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(VideoActivity.this, UserLoginAct.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();

                Intent intent = new Intent(VideoActivity.this, UserLoginAct.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


    }
}
