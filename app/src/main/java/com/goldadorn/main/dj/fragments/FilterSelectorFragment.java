package com.goldadorn.main.dj.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.activities.productListing.CollectionHelper;
import com.goldadorn.main.activities.productListing.DesignerHelper;
import com.goldadorn.main.activities.productListing.PriceHelper;
import com.goldadorn.main.activities.productListing.SelectorHelper;
import com.goldadorn.main.activities.productListing.TypeHelper;
import com.goldadorn.main.model.FilterPrice;
import com.goldadorn.main.model.IIDInterface;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.modules.home.HomePage;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.listDataView.viewHelper.RecyclerViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 15-10-2016.
 */
public class FilterSelectorFragment extends Fragment{

    public static FilterSelectorFragment newInstance(NavigationDataObject navigationDataObject){
        FilterSelectorFragment fragment = new FilterSelectorFragment();
        Bundle bundle =new Bundle();
        ArrayList<Parcelable> filters = (ArrayList<Parcelable>) navigationDataObject.getParam();
        bundle.putParcelableArrayList("filters", filters);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_image_selector_filter, container, false);
    }


    @Bind(R.id.designersRecyclerView)
    RecyclerView designersRecyclerView;

    @Bind(R.id.collectionsRecyclerView)
    RecyclerView collectionsRecyclerView;

    @Bind(R.id.productTypeRecyclerView)
    RecyclerView productTypeRecyclerView;

    @Bind(R.id.priceRecyclerView)
    RecyclerView priceRecyclerView;

    @Bind(R.id.selectorRecyclerView)
    RecyclerView selectorRecyclerView;

    @Bind(R.id.addRange)
    Button addRange;

    @Bind(R.id.rangebar)
    RangeBar rangebar;
    @Bind(R.id.applyFilters)
    Button applyFilters;

    SelectorHelper selectorHelper;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        new DesignerHelper((BaseActivity) getActivity(),designersRecyclerView,filterItemClick);
        new CollectionHelper((BaseActivity) getActivity(),collectionsRecyclerView,filterItemClick);
        new TypeHelper((BaseActivity) getActivity(),productTypeRecyclerView,filterItemClick);
        new PriceHelper((BaseActivity) getActivity(),priceRecyclerView,filterItemClick);
        Bundle bundle = getArguments();
        final ArrayList<Parcelable> filters;
        TypefaceHelper.setFont(applyFilters, addRange);
        selectorHelper = new SelectorHelper((BaseActivity) getActivity(), selectorRecyclerView);
        if (bundle != null) {
            filters = bundle.getParcelableArrayList("filters");
            if (filters != null && filters.size() != 0)
                selectorHelper.addAll(filters);
        }

        addRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterPrice price=new FilterPrice();
                price.setMin((rangebar.getLeftIndex()*50)+"");
                price.setMax((rangebar.getRightIndex()*50)+"");
                price.setMaxVal("INR "+price.getMax());
                price.setMinVal("INR "+price.getMin());
                price.setId(price.getMin()+price.getMax());
                selectorHelper.removePrice();
                selectorHelper.add(price);
            }
        });
        rangebar.setFormatter(new IRangeBarFormatter() {
            @Override
            public String format(String s) {

                if(Integer.parseInt(s)>=1000) {
                    double value =(Double.parseDouble(s)* 50)/1000;
                    String valueString = value  +"";
                    valueString = valueString.substring(0,3);
                    valueString = Double.parseDouble(valueString)+"";
                    valueString += "k+";
                    return valueString;
                }
                else {
                    double value =(Double.parseDouble(s)* 50)/1000;
                    String valueString = value  + "";
                    valueString = valueString.substring(0,3);
                    valueString = Double.parseDouble(valueString)+"";
                    valueString += "k";
                    return valueString;
                }
            }
        });

    }

    RecyclerViewHelper.OnItemClick filterItemClick = new RecyclerViewHelper.OnItemClick(){
        @Override
        public void onItemClick(Object o) {
            if(o instanceof IIDInterface) {
                if(o instanceof FilterPrice)
                    selectorHelper.removePrice();
                selectorHelper.add((IIDInterface) o);
            }
        }
    };


    @OnClick(R.id.applyFilters)void applyFilters()
    {
        Intent intent = new Intent();
        List<Object> list = selectorHelper.list();
        ArrayList<Parcelable> parcelableList = new ArrayList<>();
        if(list!=null && list.size()!=0)
        {
            for (Object o : list) {
                if(o instanceof Parcelable)
                    parcelableList.add((Parcelable)o);
            }
        }

        intent.putParcelableArrayListExtra("filters", parcelableList);
        //getActivity().setResult(Activity.RESULT_OK, intent);
        //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        ((MainActivity) getActivity()).onResultCustom(HomePage.FILTER_APPLY, Activity.RESULT_OK, intent);
    }
}
