package com.goldadorn.main.dj.uiutils;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.goldadorn.main.activities.Application;

/**
 * Created by User on 29-06-2016.
 */
public class UiRandomUtils {

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
}
