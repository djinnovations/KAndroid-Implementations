package com.goldadorn.main.dj.modules.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.model.IFragmentData;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 21-10-2016.
 */
public class SearchTabFragment extends Fragment{

    private static final String TAG = "SearchTabFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_search, container, false);
    }

    @Bind(R.id.viewpagertab)
    SmartTabLayout viewpagertab;
    @Bind(R.id.viewpager)
    ViewPager viewpager;

    public TempAdapter adapter;
    FragmentPagerItems pagerItems;


    public void gotoPage(int page){
        viewpager.setCurrentItem(page, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        adapter = new TempAdapter(
                getActivity().getSupportFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("Everything", EverythingSearchFragment.class)
                .add("Designers", DesignerSearchFragment.class)
                .add("Collections", CollectionSearchFragment.class)
                .add("Products", ProductSearchFragment.class)
                .create());

        pagerItems = new FragmentPagerItems(getActivity());
        FragmentPagerItem item = FragmentPagerItem.of("Everything", EverythingSearchFragment.class);
        pagerItems.add(0, item);
        item = FragmentPagerItem.of("Designers", DesignerSearchFragment.class);
        pagerItems.add(1, item);
        item = FragmentPagerItem.of("Collections", CollectionSearchFragment.class);
        pagerItems.add(2, item);
        item = FragmentPagerItem.of("Products", ProductSearchFragment.class);
        pagerItems.add(3, item);

        viewpager.setOffscreenPageLimit(4);
        viewpager.setAdapter(adapter);
        viewpagertab.setViewPager(viewpager);
    }

    /*public EverythingSearchFragment getFragment(){
        return (EverythingSearchFragment) adapter.getItem(0);
    }*/


    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
   /* public Fragment getRegisteredFragment(int fragmentPosition) {
        return registeredFragments.get(fragmentPosition);
    }*/

    public class TempAdapter extends FragmentPagerItemAdapter{

        public TempAdapter(FragmentManager fm, FragmentPagerItems pages) {
            super(fm, pages);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }
    }

    public EverythingSearchFragment getEverythingSearchFragment() {
        return (EverythingSearchFragment) registeredFragments.get(0);
    }

    public CollectionSearchFragment getCollectionSearchFragment(){
        return (CollectionSearchFragment) registeredFragments.get(2);
    }

    public DesignerSearchFragment getDesignerSearchFragment() {
        return (DesignerSearchFragment) registeredFragments.get(1);
    }

    public ProductSearchFragment getProductSearchFragment() {
        return (ProductSearchFragment) registeredFragments.get(3);
    }
}
