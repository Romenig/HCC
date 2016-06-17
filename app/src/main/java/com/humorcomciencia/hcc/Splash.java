package com.humorcomciencia.hcc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.commonsware.cwac.layouts.AspectLockedFrameLayout;
import com.mklimek.frameviedoview.FrameVideoView;
import com.mklimek.frameviedoview.FrameVideoViewListener;

/**
 * Created by rdasilvar on 14/06/2016.
 */
public class Splash extends Activity{

    FrameVideoView frameVideoView;
    AspectLockedFrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        frameVideoView = (FrameVideoView) findViewById(R.id.frame_video_view);
        frameVideoView.setup(Uri.parse("android.resource://com.humorcomciencia.hcc/" + R.raw.intro4));
        frameVideoView.setFrameVideoViewListener(new FrameVideoViewListener() {
            @Override
            public void mediaPlayerPrepared(final MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        finish();


                        ConnectivityManager cm =
                                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();

                        if(isConnected){
                            Intent i = new Intent(Splash.this, HCC.class);
                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(Splash.this, NoInternet.class);
                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                            startActivity(i);
                        }
                    }
                });

            }
            @Override
            public void mediaPlayerPrepareFailed(MediaPlayer mediaPlayer, String error) {

            }
        });




    }
}
