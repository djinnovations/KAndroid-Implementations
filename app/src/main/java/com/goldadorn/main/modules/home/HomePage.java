package com.goldadorn.main.modules.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.activities.productListing.FilterSelector;
import com.goldadorn.main.activities.productListing.ProductsFragment;
import com.goldadorn.main.activities.productListing.SelectorHelper;
import com.goldadorn.main.dj.fragments.FilterSelectorFragment;
import com.goldadorn.main.model.FilterProductListing;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.modules.people.FindPeopleFragment;
import com.goldadorn.main.modules.socialFeeds.FABScrollBehavior;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.OnCallService;
import com.kimeeo.library.listDataView.dataManagers.StaticDataManger;
import com.kimeeo.library.listDataView.viewPager.BaseViewPager;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.BaseFragmentViewPagerAdapter;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.BaseHorizontalFragmentViewPager;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.DefaultViewFragmentPagerAdapter;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.IFragmentProvider;
import com.kimeeo.library.model.IFragmentData;
import com.nineoldandroids.animation.Animator;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomePage extends BaseHorizontalFragmentViewPager {

    @Bind(R.id.disableApp)
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

    public static final int FILTER_APPLY = 2;

    public SocialFeedFragment socialFeedFragmentpage;
    public FindPeopleFragment findPeopleFragment;

    public ProductsFragment mActivePage;

    @Bind(R.id.applyFilters)
    Button applyFilters;

    @OnClick(R.id.applyFilters)
    void applyFilters() {
        /*Intent intent = new Intent(getActivity(), FilterSelector.class);
        if (filters != null)
            intent.putParcelableArrayListExtra("filters", filters);*/
        filterPanel.setVisibility(View.GONE);

        FragmentManager fragmentManager = getFragmentManager();
        if (mActivePage != null)
            fragmentManager.beginTransaction().remove(mActivePage).commit();
        if (mFilterSelectorFrag != null)
            fragmentManager.beginTransaction().remove(mFilterSelectorFrag).commit();
        NavigationDataObject navigationObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_filter_selector);
        if (filters != null)
            navigationObject.setParam(filters);
        mFilterSelectorFrag = FilterSelectorFragment.newInstance(navigationObject);
        getFilterPanel().setVisibility(View.GONE);
        setLayoutBehaviour(null);
        getDataManager().set(1, navigationObject);
        selectFrag = SELECTOR_FRAG;
        getFragmentAdapter().notifyDataSetChanged();
        //getActivity().startActivityForResult(intent, FILTER_APPLY);
    }


    public void onFilterResult(Intent data) {
        final ArrayList<Parcelable> filters = data.getParcelableArrayListExtra("filters");
        filterPanel.setVisibility(View.VISIBLE);
        setLayoutBehaviour(new FABScrollBehavior(getActivity(), null));
        Handler h = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                refreshView(filters, sort);
            }
        };
        h.postDelayed(r, 200);
    }


    @Bind(R.id.sortBestSelling)
    Button sortBestSelling;

    @OnClick(R.id.sortBestSelling)
    void onSortBestSelling() {
        updateSort(sortBestSelling);
    }

    @Bind(R.id.sortPrice)
    Button sortPrice;

    @OnClick(R.id.sortPrice)
    void onSortPrice() {
        updateSort(sortPrice);
    }

    @Bind(R.id.sortNew)
    Button sortNew;

    @Bind(R.id.filterPanel)
    View filterPanel;


    @OnClick(R.id.sortNew)
    void onSortNew() {
        updateSort(sortNew);
    }

    @OnClick(R.id.closeFilter)
    void onCloseFilter() {
        YoYo.with(Techniques.FadeOutDown).duration(300).withListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                filterPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).playOn(filterPanel);
    }


    public void setLayoutBehaviour(CoordinatorLayout.Behavior behaviour){
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) filterPanel.getLayoutParams();
        params.setBehavior(behaviour);
        filterPanel.setLayoutParams(params);
        filterPanel.requestLayout();
    }

    public View getFilterPanel() {
        return filterPanel;
    }

    public void setFilterPanel(View filterPanel) {
        this.filterPanel = filterPanel;
    }

    Button lastSorted;

    private void updateSort(Button btn) {
        if (lastSorted != btn) {
            lastSorted.setSelected(false);
            lastSorted.setAlpha(Float.parseFloat(".3"));
            btn.setSelected(true);
            btn.setAlpha(Float.parseFloat("1"));
            lastSorted = btn;
            if (btn == sortBestSelling)
                sort = SORT_SOLD_UNITS;
            else if (btn == sortNew)
                sort = SORT_DATE_ADDED;
            else if (btn == sortPrice)
                sort = SORT_PROD_DEFAULT_PRICE;
            refreshView(filters, sort);
        }
    }

    //View container;
    public FilterSelectorFragment mFilterSelectorFrag;
    public void onItemCreated(Fragment page) {
        if (page instanceof SocialFeedFragment) {
            socialFeedFragmentpage = ((SocialFeedFragment) page);
            ((SocialFeedFragment) page).addDisableColver(disableApp);
        }
        if (page instanceof FindPeopleFragment) {
            Log.d("djhome", "onItemCreated - setting people fragment");
            findPeopleFragment = (FindPeopleFragment) page;
        }
        if (page instanceof ProductsFragment) {
            Log.d("djhome", "onItemCreated - productFrag");
            mActivePage = (ProductsFragment) page;
        }
        if (page instanceof FilterSelectorFragment){
            Log.d("djhome", "onItemCreated - productFrag");
            mFilterSelectorFrag = (FilterSelectorFragment) page;
        }
    }

    @Bind(R.id.recyclerView)
    RecyclerView selectorRecyclerView;

    protected View createRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment_page_view, container, false);
        ButterKnife.bind(this, rootView);
        //disableApp = rootView.findViewById(R.id.disableApp);
        sortBestSelling.setSelected(true);
        sortBestSelling.setAlpha(/*Float.parseFloat("1")*/1);
        sortNew.setAlpha(/*Float.parseFloat(".3")*/0.3f);
        sortPrice.setAlpha(/*Float.parseFloat(".3")*/0.3f);
        filterPanel.setVisibility(View.GONE);
        setLayoutBehaviour(null);
        lastSorted = sortBestSelling;
        selectFrag = SELECTOR_FRAG;
        selectorHelper = new SelectorHelper((BaseActivity) getActivity(), selectorRecyclerView);
        selectorHelper.onRemoveListner(onRemoveListner);
        return rootView;
    }

    SelectorHelper.OnRemoveListner onRemoveListner = new SelectorHelper.OnRemoveListner() {
        @Override
        public void remove(Object o) {
            for (Parcelable filter : filters) {
                if (o instanceof Parcelable && (Parcelable) o == filter) {
                    filters.remove((Parcelable) o);
                    refreshView(filters, sort);
                    break;
                }
            }
        }
    };

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
        //navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_people);
        navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_filter_selector);
        /*ProductsFragment productsFragment*/mFilterSelectorFrag = FilterSelectorFragment.newInstance(navigationDataObject);
        //mActivePage.setFilters(filters, sort);
        dataManger.add(navigationDataObject);
        return dataManger;
    }

    ArrayList<Parcelable> filters;
    public String sort = SORT_SOLD_UNITS;
    public static final String SORT_SOLD_UNITS = "soldUnits";
    public static final String SORT_PROD_DEFAULT_PRICE = "prodDefaultPrice";
    public static final String SORT_DATE_ADDED = "dateAdded";
    private SelectorHelper selectorHelper;


    private void refreshView(ArrayList<Parcelable> filters, String sort) {
        if (filters != null) {
            this.filters = filters;
            selectorHelper.removeAll();
            selectorHelper.addAll(filters);
           /* if (filters.size() == 0)
                setTitle("Our Products");
            else
                setTitle("Your Selections");*/
        } /*else
            setTitle("Our Products");*/

        /*previouslySelected.clear();
        tosendbackList.clear();*/
        //getFilterPanel().setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getFragmentManager();
        if (mActivePage != null)
            fragmentManager.beginTransaction().remove(mActivePage).commit();
        if (mFilterSelectorFrag != null)
            fragmentManager.beginTransaction().remove(mFilterSelectorFrag).commit();
        /*if (selectFrag == SELECTOR_FRAG)
        fragmentManager.beginTransaction().remove(mActivePage).commit();
        else if (selectFrag == RESULT_FRAG)
            fragmentManager.beginTransaction().remove(mFilterSelectorFrag).commit();*/
        NavigationDataObject navigationObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_filter_result);
        mActivePage = (ProductsFragment) BaseFragment.newInstance(navigationObject);
        mActivePage.setFilters(filters, sort);
        getDataManager().set(1, navigationObject);
        selectFrag = RESULT_FRAG;
        getFragmentAdapter().notifyDataSetChanged();
        //_layTitle.bringToFront();
    }

    int selectFrag;
    final int RESULT_FRAG = 222;
    final int SELECTOR_FRAG = 111;

    @Override
    protected BaseFragmentViewPagerAdapter createViewPagerFragmentAdapter(FragmentManager fragmentManager, DataManager dataManager, ViewPager viewPager) {
        return new TempPagerAdapter(fragmentManager, dataManager, viewPager, null, HomePage.this, HomePage.this);//super.createViewPagerFragmentAdapter(fragmentManager, dataManager, viewPager);
    }

    private class TempPagerAdapter extends DefaultViewFragmentPagerAdapter {

        public TempPagerAdapter(FragmentManager fragmentManager, DataManager dataManager, ViewPager viewPager, IFragmentProvider fragmentProvider, OnCallService onCallService, OnItemCreated onItemCreated) {
            super(fragmentManager, dataManager, viewPager, fragmentProvider, onCallService, onItemCreated);
        }

        @Override
        public int getItemPosition(Object object) {
            //return super.getItemPosition(object);
            if (!(object instanceof SocialFeedFragment))
                return POSITION_NONE;
            return POSITION_UNCHANGED;
        }

        @Override
        public Fragment getItemFragment(int position, Object navigationObject) {
            if (position == 1){
                if (selectFrag == SELECTOR_FRAG) {
                    return mFilterSelectorFrag;
                }
                else if (selectFrag == RESULT_FRAG){
                    return mActivePage;
                }
                return mFilterSelectorFrag;
            }
            else if (position == 0){
                return BaseFragment.newInstance((IFragmentData) navigationObject);
            }
            return null;
        }

    }

    /*private void refreshView(ArrayList<Parcelable> filters, String sort) {
        if (filters != null) {
            this.filters = filters;
            selectorHelper.removeAll();
            selectorHelper.addAll(filters);
            *//*if (filters.size() == 0)
                setTitle("Our Products");
            else
                setTitle("Your Selections");*//*
        } *//*else
            setTitle("Our Products");*//*

        *//*previouslySelected.clear();
        tosendbackList.clear();*//*
        *//*FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(), "",
                NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, ProductsFragment.class);
        mActivePage = (ProductsFragment) BaseFragment.newInstance(navigationObject);
        mActivePage.setFilters(filters, sort);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();*//*
        //_layTitle.bringToFront();
    }*/

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

    @Override
    protected void onPageChange(Object itemPosition, int position) {
        super.onPageChange(itemPosition, position);
        if (position == 0) {
            getFilterPanel().setVisibility(View.GONE);
            setLayoutBehaviour(null);
        }
        else if (position == 1) {
            if (selectFrag == RESULT_FRAG) {
                getFilterPanel().setVisibility(View.VISIBLE);
                setLayoutBehaviour(new FABScrollBehavior(getActivity(), null));
            }else {
                getFilterPanel().setVisibility(View.GONE);
                setLayoutBehaviour(null);
            }
        }
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
                if (data.getIdInt() == R.id.nav_feed){
                    getFilterPanel().setVisibility(View.GONE);
                    setLayoutBehaviour(null);
                }
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
