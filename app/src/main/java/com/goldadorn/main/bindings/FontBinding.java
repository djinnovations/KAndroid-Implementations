package com.goldadorn.main.bindings;

import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.goldadorn.main.utils.TypefaceHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bpa001 on 3/21/16.
 */
public class FontBinding {
    private static final Map<String, Typeface> fontMap = new HashMap<>();

    @BindingAdapter("fontName")
    public static void setFontName(TextView view, @NonNull String fontName) {
        TypefaceHelper.setFont(view,fontName);
    }
}
