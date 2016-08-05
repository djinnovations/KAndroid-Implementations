package com.goldadorn.main.dj.uiutils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by User on 29-06-2016.
 */
public class UiRandomUtils {

    public static final String DIAMOND_URL = "http://www.pngall.com/wp-content/uploads/2016/04/Diamond-PNG-Picture.png";

    public static void startAnim(View view, int animResID) throws Exception {
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), animResID);
        view.startAnimation(anim);
    }

    public static void setPaddingLeftRight(TextView textView){
        DisplayProperties dispProp = Application.getInstance().getDisplayPropInstance();
        textView.setPadding((int) (5*dispProp.getXPixelsPerCell()), 0,
                (int) (5*dispProp.getXPixelsPerCell()), (int) (5*dispProp.getYPixelsPerCell()));
    }

    public static void underLineTv(TextView textview, int start, int end) {
        String udata = "";
        try {
            udata=/*"Underlined Text"*/ textview.getText().toString().trim();
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), start, end, 0);
            textview.setText(content);
        } catch (Exception e) {
            e.printStackTrace();
            textview.setText(udata);
        }
        //textview.setPaintFlags(textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public static void drawableFromUrl(final ImageView ivImage, final String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                final Bitmap bitmap;
                InputStream input = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.connect();
                    input = connection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmap = BitmapFactory.decodeStream(input);
                Application.getInstance().getUIHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        ivImage.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }


    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
