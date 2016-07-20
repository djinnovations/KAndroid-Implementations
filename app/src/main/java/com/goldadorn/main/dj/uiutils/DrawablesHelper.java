package com.goldadorn.main.dj.uiutils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.goldadorn.main.R;

/**
 * Created by User on 20-07-2016.
 */
public class DrawablesHelper {

    private ResourceReader resourceReader;
    private static DrawablesHelper ourInstance;
    public static DrawablesHelper getInstance(Context appContext){
        if (ourInstance == null)
            ourInstance = new DrawablesHelper(appContext);
        return ourInstance;
    }

    private DrawablesHelper(Context context){
        resourceReader = ResourceReader.getInstance(context);
    }


    public Drawable setColorFilterForDrawable(int drawableResId, int colorResId){
        Drawable drawable;
        drawable = resourceReader.getDrawableFromResId(drawableResId);
        drawable.setColorFilter(resourceReader.getColorFromResource(colorResId),
                PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    public Drawable setBoundsForDrawable(Drawable newDrawable, Drawable oldDrawable){
        Rect oldRect = oldDrawable.getBounds();
        newDrawable.setBounds(oldRect);
        return newDrawable;
    }

    public View setPaddingForDrawable(View viewToSetPaddingForDrawable, int padding){
        //Rect rect = new Rect(padding, padding, padding, padding);
        if(viewToSetPaddingForDrawable instanceof TextView)
            ((TextView) viewToSetPaddingForDrawable).setCompoundDrawablePadding(padding);
        return viewToSetPaddingForDrawable;
    }

}
