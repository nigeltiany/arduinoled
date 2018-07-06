package com.tiany.nigel.arduinoled.paid;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.VideoView;

import com.tiany.nigel.arduinoled.R;
import com.tiany.nigel.arduinoled.paid.MainActivity;

/**
 * Created by Nigel on 12/4/2015.
 */
public class Splashscreen extends Activity{
    VideoView VideoView;
    TableRow allthreeleds;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        allthreeleds = (TableRow) findViewById(R.id.allthreeleds);
        final TextView appname = (TextView) findViewById(R.id.appname);
        final ImageView redledimg = (ImageView) findViewById(R.id.redled);
        final ImageView greenledimg = (ImageView) findViewById(R.id.greenled);
        final ImageView blueledimg = (ImageView) findViewById(R.id.blueled);
        final Animation movetoright = AnimationUtils.loadAnimation(getBaseContext(),R.anim.moveleft);
        final Animation movetoleft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.moveright);
        final Animation zoomout = AnimationUtils.loadAnimation(getBaseContext(), R.anim.zoom_out);
        final Animation moveup = AnimationUtils.loadAnimation(getBaseContext(), R.anim.moveup);

        VideoView = (VideoView)findViewById(R.id.appVideoView);
        VideoView.setVisibility(View.GONE);
        VideoInits();
        redledimg.startAnimation(movetoright);
        blueledimg.startAnimation(movetoleft);
        greenledimg.startAnimation(zoomout);
        appname.setAnimation(moveup);
        movetoright.setAnimationListener(
            new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //start fade animation
                    appname.setVisibility(View.GONE);
                    VideoInits();
                    VideoView.start();
                    VideoView.setVisibility(View.VISIBLE);
                    allthreeleds.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            }
        );

        VideoView.setOnCompletionListener(
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finish();
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                }
            }
        );
    }

    public void VideoInits(){
        Uri video = Uri.parse("android.resource://com.tiany.nigel.arduinoled.paid/raw/officialappvideo"); //cant play video fixed.
        VideoView.setVideoURI(video);
        VideoView.requestFocus();
    }
}