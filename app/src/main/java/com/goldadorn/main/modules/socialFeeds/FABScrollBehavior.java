package com.goldadorn.main.modules.socialFeeds;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

/**
 * Created by BhavinPadhiyar on 02/04/16.
 */
public class FABScrollBehavior extends CoordinatorLayout.Behavior {
    public FABScrollBehavior(Context context, AttributeSet attributeSet){
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
    Handler handler;
    Runnable runnablelocal;
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,final View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if(dyConsumed > 0)
        {
            if (runnablelocal != null) {
                handler.removeCallbacks(runnablelocal);
                handler = null;
                runnablelocal=null;
            }

            runnablelocal = new Runnable() {
                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeInUp).duration(300).playOn(child);
                }
            };

            handler = new Handler();
            handler.postDelayed(runnablelocal, 3000);
        }

        if(dyConsumed > 0 && child.getVisibility() == View.VISIBLE)
        {


            YoYo.with(Techniques.FadeOutDown).duration(300).withListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    child.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).playOn(child);



        }
        else if(dyConsumed < 0 && child.getVisibility() == View.GONE)
        {
            child.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeInUp).duration(300).playOn(child);
        }
    }
}
