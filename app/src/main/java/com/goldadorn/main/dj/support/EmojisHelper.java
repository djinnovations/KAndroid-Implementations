package com.goldadorn.main.dj.support;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;

import com.goldadorn.main.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 23-06-2016.
 */
public class EmojisHelper {

    private static String TAG = "djemoticon";
    private static HashMap<String, Integer> mapOfEmojis;

    static{
        mapOfEmojis = new HashMap<>();
        mapOfEmojis.put(":)", R.drawable.emoji_blush_smile);
        mapOfEmojis.put(":-)", R.drawable.emoji_blush_smile);
        mapOfEmojis.put(":D", R.drawable.emoji_teeth);
        mapOfEmojis.put(":O", R.drawable.emoji_ooh_exp);
        mapOfEmojis.put(":o", R.drawable.emoji_ooh_exp);
        mapOfEmojis.put("@@", R.drawable.emoji_two_eye_love);
        mapOfEmojis.put(":*", R.drawable.emoji_kiss);
    }

    /*public static void with(EditText et) {
        et.addTextChangedListener(emoticonWatcher);
    }*/


    /*public static TextWatcher emoticonWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d(TAG, "beforeTextChanged - CharSequence: " + s);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "onTextChanged - CharSequence: " + s);
            //Log.d(TAG, "onTextChanged - afterbind " + s);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };*/


    public static Spannable getSpannedText(Context context, String text) {
        Log.d(TAG, "getSpannedText");
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        int index;
        for (index = 0; index < builder.length(); index++) {
            for (Map.Entry<String, Integer> entry : mapOfEmojis.entrySet()) {
                int length = entry.getKey().length();
                if (index + length > builder.length())
                    continue;
                if (builder.subSequence(index, index + length).toString().equals(entry.getKey())) {
                    Log.d(TAG, "getSpannedText - contains emoji");
                    builder.setSpan(new ImageSpan(context, entry.getValue()), index, index + length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index += length - 1;
                    break;
                }
            }
        }
        return builder;
    }
}
