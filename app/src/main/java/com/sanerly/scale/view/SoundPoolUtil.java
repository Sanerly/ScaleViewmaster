package com.sanerly.scale.view;

import android.content.Context;
import android.media.SoundPool;
import android.os.Build;

/**
 * @Author: Sanerly
 * @CreateDate: 2019/7/23 9:52
 * @Description: 类描述
 */
public class SoundPoolUtil {

    private static SoundPool instance;

    public SoundPoolUtil() {
    }

    public static SoundPool getInstance() {
        if (null == instance) {
            if (Build.VERSION.SDK_INT >= 21) {
                instance = (new SoundPool.Builder()).setMaxStreams(3).build();
            } else {
                instance = new SoundPool(3, 1, 1);
            }
        }

        return instance;
    }

    public static void play(Context context, int resId) {
        SoundPool soundPool = getInstance();
        final int mMusic = soundPool.load(context, resId, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(mMusic, 1.0F, 1.0F, 0, 0, 1.0F);
            }
        });
    }
}
