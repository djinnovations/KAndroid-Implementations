package com.goldadorn.main.activities;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.goldadorn.main.R;
import com.goldadorn.main.intro.IntroBase;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.kimeeo.library.actions.Action;

/**
 * Created by bhavinpadhiyar on 2/20/16.
 */
public class Intro extends IntroBase
{
    private SharedPreferences sharedPreferences;

    public void init(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences(AppSharedPreferences.LoginInfo.NAME, Context.MODE_PRIVATE);

        addSlide(new Slide1());
        addSlide(new Slide2());
        addSlide(new Slide3());
        addSlide(new Slide4());
        addSlide(new Slide5());
        //addSlide(AppIntroFragment.newInstance("Hello", "Hello", R.drawable.vector_image_logo_final, R.color.darkColor));


        setBarColor(getResources().getColor(R.color.colorPrimary));
        setSeparatorColor(Color.parseColor("#002196F3"));
        showSkipButton(false);
    }

    public void onSkipPressed() {
        doneWithIntro();

    }

    @Override
    public void onDonePressed() {
        doneWithIntro();
    }

    private void doneWithIntro() {
        sharedPreferences.edit().putBoolean(AppSharedPreferences.LoginInfo.IS_INTRO_SEEN, true).commit();
        new Action(this).launchActivity(AppStartActivity.class, true);
    }

    @Override
    public void onSlideChanged() {

    }

    @Override
    public void onNextPressed() {

    }
    public static class Slide1 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.intro_screen_1, container, false);
            return v;
        }
    }
    public static class Slide2 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.intro_screen_2, container, false);
            return v;
        }
    }
    public static class Slide3 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.intro_screen_3, container, false);
            return v;
        }
    }
    public static class Slide4 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.intro_screen_4, container, false);
            return v;
        }
    }
    public static class Slide5 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.intro_screen_5, container, false);
            return v;
        }
    }
}
