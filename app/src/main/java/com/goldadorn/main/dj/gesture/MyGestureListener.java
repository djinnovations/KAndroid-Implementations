package com.goldadorn.main.dj.gesture;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.uiutils.DisplayProperties;

/**
 * Created by User on 07-06-2016.
 */
public abstract class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private final float spacingFromEdge = 10 * DisplayProperties.getInstance(Application.getInstance(), 1)
            .getXPixelsPerCell();

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        Log.d("djgest", "onFling");
        //boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        if (e1.getX() < spacingFromEdge)
                            return false;
                        onSwipeLeftToRight();
                    } else {
                        //onSwipeRightToLeft();
                    }
                    return true;
                }
                //result = true;
                return false;
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    //onSwipeTopToBottom();
                } else {
                    //onSwipeBottomToTop();
                }
                //result = false;
                return true;
            }
            //result = true;
            return false;

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }


    public abstract void onSwipeRightToLeft();

    public abstract void onSwipeLeftToRight();

    public abstract void onSwipeBottomToTop();

    public abstract void onSwipeTopToBottom();

}
