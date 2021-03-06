package com.goldadorn.main.dj.uiutils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.utils.Constants;

/**
 * Created by DJphy on 29-05-2016.
 */
public class BadgeDrawable extends Drawable {

    private float mTextSize;
    private Paint mBadgePaint;
    private Paint mBadgePaint1;
    private Paint mTextPaint;
    private Rect mTxtRect = new Rect();

    private String mCount = "";
    private boolean mWillDraw = false;


    public BadgeDrawable(Context context) {

        Log.d("dj", "constructor pt1 BadgeDrawable");
        mTextSize = Constants.CURRENT_API_LEVEL >=21 ? 17 : 16;
        //mTextSize = /*context.getResources().getDimension(R.dimen.badge_text_size)*/ 20;
        mBadgePaint = new Paint();
        mBadgePaint.setColor(ResourceReader.getInstance(context).getColorFromResource(R.color.colorPrimary));
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);
        mBadgePaint1 = new Paint();
        mBadgePaint1.setColor(Color.parseColor("#FFFFFF"));//white
        mBadgePaint1.setAntiAlias(true);
        mBadgePaint1.setStyle(Paint.Style.FILL);


        Log.d("dj", "selected text size: "+mTextSize);
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTypeface(Typeface.SANS_SERIF);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.setCallback(callback);
    }

    @Override
    public void draw(Canvas canvas) {

        Log.d("dj", "onDraw BadgeDrawable");
        if (!mWillDraw) {
            return;
        }

        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;
        // Position the badge in the top-right quadrant of the icon.

  /*Using Math.max rather than Math.min */
        float radius = ((Math.max(width, height) / 2)) / 2;
        float centerX = (width - radius - 1) +10;
        float centerY = radius -5;
        if(mCount.length() <= 2){
            // Draw badge circle.
            /*canvas.drawCircle(centerX, centerY, radius+9, mBadgePaint1);
            canvas.drawCircle(centerX, centerY, radius+7, mBadgePaint);*/

            canvas.drawCircle(centerX, centerY, radius+7, mBadgePaint1);
            canvas.drawCircle(centerX, centerY, radius+8, mBadgePaint);
        }
        else{
            /*canvas.drawCircle(centerX, centerY, radius+10, mBadgePaint1);
            canvas.drawCircle(centerX, centerY, radius+8, mBadgePaint);*/

            canvas.drawCircle(centerX, centerY, radius+10, mBadgePaint1);
            canvas.drawCircle(centerX, centerY, radius+8, mBadgePaint);
        }
        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
        float textHeight = mTxtRect.bottom - mTxtRect.top;
        float textY = centerY + (textHeight / 2f);
        /*if(mCount.length() > 2)
        canvas.drawText("99+", centerX, textY, mTextPaint);
        else*/
        canvas.drawText(mCount, centerX, textY, mTextPaint);
    }

    /*
     Sets the count (i.e notifications) to display.
      */
    public void setCount(String count) {
        Log.d(Constants.TAG, "setCount count val- BadgeDrawable: "+count);
        mCount = count;
        // Only draw a badge if there are notifications.
        mWillDraw = !count.equalsIgnoreCase("0");
        invalidateSelf();
    }


    private Drawable.Callback callback = new Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            Log.d(Constants.TAG, "invalidateDrawable - BadgeDrawable: ");
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {

        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {

        }
    };

    @Override
    public void setAlpha(int alpha) {
        // do nothing
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // do nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
