package com.goldadorn.main.modules.home;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.dj.gesture.SwipeHelper;
import com.goldadorn.main.dj.support.AppTourGuideHelper;
import com.goldadorn.main.dj.uiutils.DisplayProperties;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.modules.people.FindPeopleFragment;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.StaticDataManger;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.BaseHorizontalFragmentViewPager;
import com.kimeeo.library.model.IFragmentData;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomePage extends BaseHorizontalFragmentViewPager {
    View disableApp;

    protected void garbageCollectorCall() {
        super.garbageCollectorCall();
        disableApp = null;
        socialFeedFragmentpage = null;
    }

    public void updateComments() {
        if (socialFeedFragmentpage != null)
            socialFeedFragmentpage.updateComments();
    }

    public boolean allowedBack() {
        if (socialFeedFragmentpage != null) {
            return socialFeedFragmentpage.allowedBack();
        } else
            return super.allowedBack();
    }

    public SocialFeedFragment socialFeedFragmentpage;
    public FindPeopleFragment findPeopleFragment;

    public void onItemCreated(Fragment page) {
        if (page instanceof SocialFeedFragment) {
            socialFeedFragmentpage = ((SocialFeedFragment) page);
            ((SocialFeedFragment) page).addDisableColver(disableApp);
        }
        if (page instanceof FindPeopleFragment) {
            Log.d("djhome", "onItemCreated - setting people fragment");
            findPeopleFragment = (FindPeopleFragment) page;
        }
    }

    protected View createRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment_page_view, container, false);
        disableApp = rootView.findViewById(R.id.disableApp);
        return rootView;
    }

    protected Application getApp() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        return baseActivity.getApp();
    }

    RecyclerTabLayout indicatorMain;

    protected View createIndicator(View rootView) {
        View indicator = rootView.findViewById(R.id.indicator);
        if (getActivity() instanceof MainActivity && ((MainActivity) getActivity()).getPageIndicator() != null) {
            if (indicator != null)
                indicator.setVisibility(View.GONE);
            return indicatorMain = (RecyclerTabLayout) ((MainActivity) getActivity()).getPageIndicator();
        }
        return indicator;
    }

    protected DataManager createDataManager() {
        DataManager dataManger = new StaticDataManger(getActivity());
        NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_feed);
        dataManger.add(navigationDataObject);
        navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_people);
        dataManger.add(navigationDataObject);
        return dataManger;
    }

    public Fragment getItemFragment(int position, Object navigationObject) {
        if (navigationObject instanceof IFragmentData) {
            BaseFragment activePage = BaseFragment.newInstance((IFragmentData) navigationObject);
            return activePage;
        }
        return
                null;
    }

    @Override
    public String getItemTitle(int position, Object navigationObject) {
        if (navigationObject instanceof IFragmentData) {
            return ((IFragmentData) navigationObject).getName();
        }
        return super.getItemTitle(position, navigationObject);
    }

    protected RecyclerTabLayout.Adapter<?> getRecyclerViewTabProvider(ViewPager viewPager) {
        return new TabIndicatorRecyclerViewAdapter(viewPager, getDataManager());
    }

    private void gotoData(Object data) {
        if (getDataManager() != null && data != null) {
            for (int i = 0; i < getDataManager().size(); i++) {
                if (getDataManager().get(i) == data) {
                    /*if (i == 1)
                        setUpTourGuideForPeople();*/
                    gotoItem(i, false);
                    break;
                }
            }
        }
    }

    /*private AppTourGuideHelper mTourHelper;
    private void setUpTourGuideForPeople() {
        Log.d("djhomePage", "onClickPeopleTab");
        *//*resRdr = ResourceReader.getInstance(getApplicationContext());
        coachMarkMgr = DjphyPreferenceManager.getInstance(getApplicationContext());*//*
        mTourHelper = AppTourGuideHelper.getInstance(Application.getInstance());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                *//*if (!coachMarkMgr.isHomeScreenTourDone())
                    testTourGuide();*//*
                mTourHelper.displayPeopleScreenTour(getActivity(), ((MainActivity) getActivity()).getCenterView());
            }
        }, 800);
    }*/

    //public static ViewPager viewPager;

    public class TabIndicatorRecyclerViewAdapter extends com.kimeeo.library.listDataView.viewPager.TabIndicatorRecyclerViewAdapter {

        public TabIndicatorRecyclerViewAdapter(ViewPager viewPager, DataManager dataManager) {
            super(viewPager, dataManager);
            //HomePage.viewPager = viewPager;
        }

        protected ViewHolder getViewHolder(View view, int var2) {
            return new MyViewHolder(view);
        }

        @Override
        protected View getView(ViewGroup parent, int var2) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom_tab_view, parent, false);
            //return LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_two_tab_indicators, parent, false);
        }

        public int getItemCount() {
            return getDataManager().size();
        }


        public class MyViewHolder extends ViewHolder {

            @Bind(R.id.textView)
            public TextView textView;
            @Bind(R.id.selected)
            public View selected;

            @Bind(R.id.tabParent)
            RelativeLayout tabParent;
            /*@Bind(R.id.shadow_left_bottom_square)
            View shadow_left_bottom_square;
            @Bind(R.id.textView)
            TextView textView;
            @Bind(R.id.shadow_pillar_left)
            View shadow_pillar_left;
            @Bind(R.id.shadow_selected_bottom)
            View shadow_selected_bottom;
            @Bind(R.id.shadow_pillar_right)
            View shadow_pillar_right;
            @Bind(R.id.right_pillar)
            View right_pillar;
            @Bind(R.id.shadow_right_bottom_square)
            View shadow_right_bottom_square;
            @Bind(R.id.layoutTab)
            LinearLayout layoutTab;*/


            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        gotoData(data);
                    }
                });
                setTabWidth();
                /*DisplayProperties properties = DisplayProperties
                        .getInstance(getContext(), DisplayProperties.ORIENTATION_PORTRAIT);
                tabParent.getLayoutParams().width = (int) (17 * properties.getXPixelsPerCell());*/
                //textView.setTextSize((float) (1.8 * properties.getXPixelsPerCell()));
                TypefaceHelper.setFont(itemView.getResources().getString(R.string.font_name_text_secondary), textView);
            }

            private void setTabWidth() {
                ViewTreeObserver vto = indicatorMain.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        try {
                            int indicatorWidth = indicatorMain.getWidth();
                            Log.d("dj", " indicator width: " + indicatorWidth);
                            tabParent.getLayoutParams().width = indicatorWidth / 2;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                indicatorMain.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } else
                                indicatorMain.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            NavigationDataObject data;

            public void updatedSelectedItem(Object o) {
                data = (NavigationDataObject) o;
                textView.setText(data.getName());
                textView.setTextColor(textView.getResources().getColor(R.color.colorPrimary));
                selected.setVisibility(View.VISIBLE);
               /* shadow_left_bottom_square.setVisibility(View.INVISIBLE);
                shadow_pillar_left.setVisibility(View.VISIBLE);
                shadow_selected_bottom.setVisibility(View.INVISIBLE);
                shadow_pillar_right.setVisibility(View.VISIBLE);
                shadow_right_bottom_square.setVisibility(View.VISIBLE);
                right_pillar.setVisibility(View.INVISIBLE);*/
            }

            public void updatedNormalItem(Object o) {
                data = (NavigationDataObject) o;
                textView.setText(data.getName());
                textView.setTextColor(textView.getResources().getColor(R.color.colorPrimaryAlpha));
                selected.setVisibility(View.INVISIBLE);
                /*shadow_left_bottom_square.setVisibility(View.INVISIBLE);
                shadow_pillar_left.setVisibility(View.INVISIBLE);
                shadow_selected_bottom.setVisibility(View.INVISIBLE);
                shadow_pillar_right.setVisibility(View.INVISIBLE);
                shadow_right_bottom_square.setVisibility(View.INVISIBLE);
                right_pillar.setVisibility(View.INVISIBLE);*/
            }
        }
    }
}
