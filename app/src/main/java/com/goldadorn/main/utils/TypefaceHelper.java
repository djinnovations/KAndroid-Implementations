package com.goldadorn.main.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.goldadorn.main.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BhavinPadhiyar on 31/03/16.
 */
public class TypefaceHelper {
    private static Map<String,Typeface> map = new HashMap<>();
    public static void setFont(EditText text,String fontName) {
        Typeface typeface = getTypeFace(text.getContext(),fontName);
        text.setTypeface(typeface);
    }
    public static void setFont(TextView text,String fontName) {
        Typeface typeface = getTypeFace(text.getContext(),fontName);
        text.setTypeface(typeface);
    }
    public static void setFont(TextInputLayout text,String fontName) {
        Typeface typeface = getTypeFace(text.getContext(),fontName);
        text.setTypeface(typeface);
    }
    public static void setFont(Button text,String fontName) {
        Typeface typeface = getTypeFace(text.getContext(),fontName);
        text.setTypeface(typeface);
    }
    public static void setFont(RadioButton text,String fontName) {
        Typeface typeface = getTypeFace(text.getContext(),fontName);
        text.setTypeface(typeface);
    }




    public static void setFont(EditText text) {
        Typeface typeface = getTypeFace(text.getContext(),text.getResources().getString(R.string.font_name_edit_text));
        text.setTypeface(typeface);
    }
    public static void setFont(TextView text) {
        Typeface typeface = getTypeFace(text.getContext(),text.getResources().getString(R.string.font_name_text_normal));
        text.setTypeface(typeface);

    }
    public static void setFont(TextInputLayout text) {
        Typeface typeface = getTypeFace(text.getContext(),text.getResources().getString(R.string.font_name_text_normal));
        text.setTypeface(typeface);
    }

    public static void setFont(RadioButton text) {
        Typeface typeface = getTypeFace(text.getContext(),text.getResources().getString(R.string.font_name_text_normal));
        text.setTypeface(typeface);
    }

    public static void setFont(Button text) {
        Typeface typeface = getTypeFace(text.getContext(),text.getResources().getString(R.string.font_name_text_normal));
        text.setTypeface(typeface);
    }

    public static void setFont(View... views) {
        View view;

        for (int i = 0; i < views.length; i++) {
            view = views[i];
            if(view instanceof TextView)
                setFont((TextView)view);
            else if(view instanceof EditText)
                setFont((EditText)view);
            else if(view instanceof Button)
                setFont((Button)view);
            else if(view instanceof TextInputLayout)
                setFont((TextInputLayout)view);
            else if(view instanceof RadioButton)
                setFont((RadioButton)view);



        }
    }

    public static void setFont(String fontName,View... views) {
        View view;

        for (int i = 0; i < views.length; i++) {
            view = views[i];
            if(view instanceof TextView)
                setFont((TextView)view,fontName);
            else if(view instanceof EditText)
                setFont((EditText)view,fontName);
            else if(view instanceof Button)
                setFont((Button)view,fontName);
            else if(view instanceof TextInputLayout)
                setFont((TextInputLayout)view,fontName);
            else if(view instanceof RadioButton)
                setFont((RadioButton)view,fontName);
        }
    }


    private static Typeface getTypeFace(Context context,String fontName) {
        Typeface typeface = map.get(fontName);
        if(typeface==null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
            map.put(fontName,typeface);
        }
        return typeface;
    }

}
