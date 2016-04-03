package com.goldadorn.main.assist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.daprlabs.cardstack.SwipeDeck;

/**
 * Created by Kiran BH on 03/04/16.
 */
public class SwipeCardView extends SwipeDeck {

    public SwipeCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    float startX;
    float startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                float currentY = event.getY();

                float diffX = Math.abs(currentX - startX);
                float diffY = Math.abs(currentY - startY);

                // The purpose of the +100 is to make sure that we have a big enough
                // difference between the 2 directions, eg. it's not diagonal
                if (diffY > diffX + 100) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
        }

        return true;
    }
}
