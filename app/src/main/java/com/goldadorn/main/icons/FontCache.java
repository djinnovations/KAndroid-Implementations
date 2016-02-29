package com.goldadorn.main.icons;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * Created by bhavinpadhiyar on 11/24/15.
 */
public class FontCache {
    private static Hashtable<String, Typeface> fontCache = new Hashtable<>();

    public static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname);
            } catch (Exception e) {
                return null;
            }
            fontCache.put(fontname, typeface);
        }

        return typeface;
    }
    public static void setTypeface(String fontname, TextView context) {
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/" + fontname);
            } catch (Exception e) {

            }
            if(typeface!=null)
                fontCache.put(fontname, typeface);
        }
        if(typeface!=null)
            context.setTypeface(typeface);
    }
}
