package com.deepmirror.host;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class BeepManager {
    public static final String TAG = BeepManager.class.getSimpleName();
    private MediaPlayer mp;
    private final String filename;
    private Context mContext;

    public BeepManager(Context context) {
        mContext = context;
        filename = "android.resource://" + context.getPackageName() + "/raw/pixiedust";
    }

    public synchronized void beep() {
        if (mp != null && mp.isPlaying()) return;
        reset();
        mp = new MediaPlayer();
        try {
            mp.setDataSource(mContext, Uri.parse(filename));
            mp.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build());
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void release() {
        mContext = null;
        reset();
    }

    private void reset(){
        if (mp != null) {
            mp.reset();
            mp.release();
        }
        mp=null;
    }

}
