package com.goldadorn.main.dj.support;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.utils.ResourceReader;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by COMP on 10-May-16.
 */
public class AppTourGuideHelper {

    private Animation bounceAnim;
    private Animation fadeInAnim;
    private Animation fadeOutAnim;

    private final String msgWelcome = "You have landed on the social feed\n"
            + "where you can see all the social\nactivity happening in the app";
    private final String msgSearch = "for designers, products\ncollections, trends and more";
    private final String msgNotification = "check here";
    private final String msgPeople = "See the user community at GoldAdorn";
    private final String msgPost = "Create a Post";
    private final String msgTimeLine = "You are on a user's timeline\n"
            + "all the recent activities of the \n user can be viewed here";
    private final String msgShowcaseWelcome = "Browse through the most exclusive\n" +
            " Designers, Collections, Products in Jewelry";
    private final String msgSwipeUp = "Swipe up to look through all products of a Designer";
    private final String msgViewProduct = "Directly look through all products of a Designer";

    private ResourceReader resRdr;
    private CoachMarkManager coachMarkMgr;
    private final String overLayBgColor = "#55000000"; //55000000
    private final String toolTipBgColor = "#33E2E4E7";

    private static AppTourGuideHelper ourInstance;

    public static AppTourGuideHelper getInstance(Context appContext) {

        if (ourInstance == null) {
            ourInstance = new AppTourGuideHelper(appContext);
        }
        return ourInstance;
    }

    private AppTourGuideHelper(Context appContext) {

        bounceAnim = new TranslateAnimation(0f, 0f, 200f, 0f);
        bounceAnim.setDuration(1000);
        bounceAnim.setFillAfter(true);
        bounceAnim.setInterpolator(new BounceInterpolator());

        fadeInAnim = new AlphaAnimation(0f, 1f);
        fadeInAnim.setDuration(600);
        fadeInAnim.setFillAfter(true);

        fadeOutAnim = new AlphaAnimation(1f, 0f);
        fadeOutAnim.setDuration(600);
        fadeOutAnim.setFillAfter(true);

        resRdr = ResourceReader.getInstance(appContext);
        coachMarkMgr = CoachMarkManager.getInstance(appContext);
    }


    public void displayHomeTour(Activity homeActivity, View[] viewsInSequence) {

        if (coachMarkMgr.isHomeScreenTourDone())
            return;

        int toolTipBgColor = resRdr.getColorFromResource(R.color.colorPrimaryDark);
        int toolTipTextColor = Color.WHITE;
        Pointer pointer = new Pointer().setColor(resRdr.getColorFromResource(R.color.colorPrimaryDark))
                .setGravity(Gravity.CENTER);
        Pointer pointer1 = new Pointer().setColor(Color.WHITE)
                .setGravity(Gravity.CENTER);

        Overlay overlay = new Overlay()
                .setBackgroundColor(Color.parseColor(overLayBgColor))
                .setStyle(Overlay.Style.Circle)
                .setEnterAnimation(fadeInAnim)
                .setExitAnimation(fadeOutAnim);

        ChainTourGuide tourGuideMain = ChainTourGuide.init(homeActivity)
                .setToolTip(new ToolTip()
                        .setTitle("Welcome To Gold Adorn")
                        .setDescription(msgWelcome)
                        .setTextColor(toolTipTextColor)
                        .setBackgroundColor(toolTipBgColor)
                        .setShadow(true)
                        .setEnterAnimation(bounceAnim)
                        .setGravity(Gravity.CENTER)
                )
                .setOverlay(null)
                // note that there is not Overlay here, so the default one will be used
                .playLater(viewsInSequence[0]);


        ChainTourGuide tourGuidePeople = ChainTourGuide.init(homeActivity)
                .setPointer(pointer)
                .setToolTip(new ToolTip()
                        .setTitle("People")
                        .setTextColor(toolTipTextColor)
                        .setBackgroundColor(toolTipBgColor)
                        .setShadow(true)
                        .setDescription(msgPeople)
                        .setGravity(Gravity.BOTTOM | Gravity.CENTER)
                )
                .setOverlay(overlay)
                .playLater(viewsInSequence[1]);

        ChainTourGuide tourGuideSearch = ChainTourGuide.init(homeActivity)
                .setPointer(pointer)
                .setToolTip(new ToolTip()
                        .setTitle("Search")
                        .setTextColor(toolTipTextColor)
                        .setBackgroundColor(toolTipBgColor)
                        .setShadow(true)
                        .setDescription(msgSearch)
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                )
                .setOverlay(overlay)
                .playLater(viewsInSequence[2]);


        ChainTourGuide tourGuideNotification = ChainTourGuide.init(homeActivity)
                .setPointer(pointer)
                .setToolTip(new ToolTip()
                        .setTitle("Notification")
                        .setTextColor(toolTipTextColor)
                        .setBackgroundColor(toolTipBgColor)
                        .setShadow(true)
                        .setDescription(msgNotification)
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                )
                .setOverlay(overlay)
                .playLater(viewsInSequence[3]);

        ChainTourGuide tourGuidePost = ChainTourGuide.init(homeActivity)
                .setPointer(pointer1)
                .setToolTip(new ToolTip()
                        .setTitle("Post")
                        .setTextColor(toolTipTextColor)
                        .setBackgroundColor(toolTipBgColor)
                        .setShadow(true)
                        .setDescription(msgPost)
                        .setGravity(Gravity.TOP | Gravity.LEFT)
                )
                .setOverlay(overlay)
                .playLater(viewsInSequence[4]);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuideMain, tourGuidePeople, tourGuideSearch,
                        tourGuideNotification, tourGuidePost)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(fadeInAnim)
                        .setExitAnimation(fadeOutAnim)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        ChainTourGuide.init(homeActivity).playInSequence(sequence);
        coachMarkMgr.setHomeScreenTourGuideStatus(true);
    }


    public void displayTimeLineTour(Activity timeLineActivity, View centeredView) {

        if (coachMarkMgr.isTimeLineTourdone())
            return;

        ToolTip toolTip = new ToolTip()
                .setTitle("Time line")
                .setDescription(msgTimeLine)
                .setTextColor(Color.WHITE)
                .setBackgroundColor(resRdr.getColorFromResource(R.color.colorPrimaryDark))
                /*.setTextColor(Color.BLACK)
                .setBackgroundColor(Color.parseColor(toolTipBgColor))*/
                .setShadow(true)
                .setEnterAnimation(fadeInAnim)
                .setGravity(Gravity.BOTTOM | Gravity.CENTER);


        Pointer pointer = new Pointer().setColor(resRdr.getColorFromResource(R.color.colorPrimaryDark))/*.setColor(Color.RED)*/
                .setGravity(Gravity.BOTTOM);

        final TourGuide tg = TourGuide.init(timeLineActivity)
                .setToolTip(toolTip)
                .setOverlay(new Overlay()
                        .setBackgroundColor(Color.parseColor(overLayBgColor)))
                .setPointer(pointer)
                .playOn(centeredView);

        tg.getOverlay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tg.cleanUp();
            }
        });
        coachMarkMgr.setTimeLineTourGuideStatus(true);
    }




    public void displayShowcaseTour(Activity showcaseActivity, View[] viewsInSequence){

        if (coachMarkMgr.isShowcaseTourdone())
            return;

        int toolTipBgColor = resRdr.getColorFromResource(R.color.colorPrimaryDark);
        int toolTipTextColor = Color.WHITE;
        Pointer pointer = new Pointer().setColor(resRdr.getColorFromResource(R.color.colorPrimaryDark))
                .setGravity(Gravity.CENTER);
        Pointer pointer1 = new Pointer().setColor(Color.WHITE)
                .setGravity(Gravity.CENTER);

        Overlay overlay = new Overlay()
                .setBackgroundColor(Color.parseColor(overLayBgColor))
                .setStyle(Overlay.Style.Circle)
                .setEnterAnimation(fadeInAnim)
                .setExitAnimation(fadeOutAnim);


        ChainTourGuide tourGuideMain = ChainTourGuide.init(showcaseActivity)
                .setToolTip(new ToolTip()
                        .setTitle("Welcome To Gold Adorn")
                        .setDescription(msgWelcome)
                        .setTextColor(toolTipTextColor)
                        .setBackgroundColor(toolTipBgColor)
                        .setShadow(true)
                        .setEnterAnimation(bounceAnim)
                        .setGravity(Gravity.CENTER)
                )
                .setOverlay(null)
                // note that there is not Overlay here, so the default one will be used
                .playLater(viewsInSequence[0]);

        ChainTourGuide tourGuideSwipeUp = ChainTourGuide.init(showcaseActivity)
                .setPointer(pointer)
                .setToolTip(new ToolTip()
                        .setTitle("People")
                        .setTextColor(toolTipTextColor)
                        .setBackgroundColor(toolTipBgColor)
                        .setShadow(true)
                        .setDescription(msgPeople)
                        .setGravity(Gravity.BOTTOM | Gravity.CENTER)
                )
                .setOverlay(overlay)
                .playLater(viewsInSequence[1]);

        ChainTourGuide tourGuideProducts = ChainTourGuide.init(showcaseActivity)
                .setPointer(pointer)
                .setToolTip(new ToolTip()
                        .setTitle("Search")
                        .setTextColor(toolTipTextColor)
                        .setBackgroundColor(toolTipBgColor)
                        .setShadow(true)
                        .setDescription(msgSearch)
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                )
                .setOverlay(overlay)
                .playLater(viewsInSequence[2]);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuideMain, tourGuideSwipeUp, tourGuideProducts)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(fadeInAnim)
                        .setExitAnimation(fadeOutAnim)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        ChainTourGuide.init(showcaseActivity).playInSequence(sequence);

    }

}
