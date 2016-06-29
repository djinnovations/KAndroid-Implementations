package com.goldadorn.main.dj.uiutils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by User on 29-06-2016.
 */
public class UiRandomUtils {

    public static void startAnim(View view, int animResID) throws Exception {
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), animResID);
        view.startAnimation(anim);
    }
}
