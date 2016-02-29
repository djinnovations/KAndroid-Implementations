package com.goldadorn.main.icons;

import android.content.Context;
import android.graphics.Typeface;

import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.ITypeface;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by mikepenz on 01.11.14.
 */
public class HeartIconFont implements ITypeface {
    private static final String TTF_FILE = "heartIcon.ttf";

    private static Typeface typeface = null;

    private static HashMap<String, Character> mChars;

    @Override
    public IIcon getIcon(String key) {
        return Icon.valueOf(key);
    }

    @Override
    public HashMap<String, Character> getCharacters() {
        if (mChars == null) {
            HashMap<String, Character> aChars = new HashMap<String, Character>();
            for (Icon v : Icon.values()) {
                aChars.put(v.name(), v.character);
            }
            mChars = aChars;
        }

        return mChars;
    }

    @Override
    public String getMappingPrefix() {
        return "hea";
    }

    @Override
    public String getFontName() {
        return "heartIcon";
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public int getIconCount() {
        return mChars.size();
    }

    @Override
    public Collection<String> getIcons() {
        Collection<String> icons = new LinkedList<String>();

        for (Icon value : Icon.values()) {
            icons.add(value.name());
        }

        return icons;
    }


    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public String getUrl() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getLicense() {
        return "";
    }

    @Override
    public String getLicenseUrl() {
        return "";
    }

    @Override
    public Typeface getTypeface(Context context) {
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + TTF_FILE);
            } catch (Exception e) {
                return null;
            }
        }
        return typeface;
    }

    public static enum Icon implements IIcon {
        hea_heart_out_line('\ue901'),
        hea_heart_fill('\ue902'),
        hea_broken_heart_fill('\ue900'),
        hea_broken_heart_out_line('\ue903');




        char character;

        Icon(char character) {
            this.character = character;
        }

        public String getFormattedName() {
            return "{" + name() + "}";
        }

        public char getCharacter() {
            return character;
        }

        public String getName() {
            return name();
        }

        // remember the typeface so we can use it later
        private static ITypeface typeface;

        public ITypeface getTypeface() {
            if (typeface == null) {
                typeface = new HeartIconFont();
            }
            return typeface;
        }
    }
}
