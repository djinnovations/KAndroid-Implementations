package com.goldadorn.main.activities.showcase;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Vijith Menon on 11/3/16.
 */
public class SocialFragment extends Fragment {
    private final static String TAG = SocialFragment.class.getSimpleName();
    private final static boolean DEBUG = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = new View(inflater.getContext());
        v.setBackgroundColor(Color.RED);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2000));
        return v;
    }

}
