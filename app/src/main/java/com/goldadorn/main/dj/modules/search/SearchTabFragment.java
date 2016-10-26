package com.goldadorn.main.dj.modules.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.utils.TypefaceHelper;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 21-10-2016.
 */
public class SearchTabFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_search, container, false);
    }

    @Bind(R.id.viewpagertab)
    SmartTabLayout viewpagertab;
    @Bind(R.id.viewpager)
    ViewPager viewpager;

    FragmentPagerItemAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        adapter = new FragmentPagerItemAdapter(
                getActivity().getSupportFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("Everything", EverythingSearchFragment.class)
                .add("Designers", DesignerSearchFragment.class)
                .add("Collections", CollectionSearchFragment.class)
                .add("Products", ProductSearchFragment.class)
                .create());

        viewpager.setOffscreenPageLimit(4);
        viewpager.setAdapter(adapter);
        viewpagertab.setViewPager(viewpager);
    }

    public EverythingSearchFragment getFragment(){
        return (EverythingSearchFragment) adapter.getItem(0);
    }


}
