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
public class FlatIconsFont implements ITypeface {
    private static final String TTF_FILE = "flaticon.ttf";

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
        return "fla";
    }

    @Override
    public String getFontName() {
        return "flaticon";
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
        fla_addition10('\ue000'),
        fla_appointment('\ue001'),
        fla_arrow487('\ue002'),
        fla_arrowhead7('\ue003'),
        fla_black403('\ue004'),
        fla_bookmark49('\ue005'),
        fla_concert1('\ue006'),
        fla_configuration21('\ue007'),
        fla_drawer4('\ue008'),
        fla_earth16('\ue009'),
        fla_emoticon87('\ue00a'),
        fla_event8('\ue00b'),
        fla_facebook55('\ue00c'),
        fla_google_plus1('\ue00d'),
        fla_handshake1('\ue00e'),
        fla_heart295('\ue00f'),
        fla_label36('\ue010'),
        fla_link23('\ue011'),
        fla_magnifying_glass32('\ue012'),
        fla_next15('\ue013'),
        fla_orientation9('\ue014'),
        fla_passion('\ue015'),
        fla_pin71('\ue016'),
        fla_placeholder8('\ue017'),
        fla_previous11('\ue018'),
        fla_rss24('\ue019'),
        fla_star218('\ue01a'),
        fla_telephone5('\ue01b'),
        fla_thumbs27('\ue01c'),
        fla_thumbs28('\ue01d'),
        fla_twitter1('\ue01e'),
        fla_wall_clock('\ue01f');


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
                typeface = new FlatIconsFont();
            }
            return typeface;
        }
    }
}
