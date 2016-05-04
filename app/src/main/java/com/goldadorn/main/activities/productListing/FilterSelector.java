package com.goldadorn.main.activities.productListing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.model.FilterPrice;
import com.goldadorn.main.model.IIDInterface;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.listDataView.viewHelper.RecyclerViewHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BhavinPadhiyar on 21/04/16.
 */
public class FilterSelector extends BaseActivity {
    private Button addRange;

    public ViewGroup getLayoutParent() {
        return layoutParent;
    }

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    @Bind(R.id.applyFilters)
    Button applyFilters;
    RangeBar rangebar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector_holder_filter);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Apply Filter(s)");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_icon_cross_white);

        RecyclerView designersRecyclerView = (RecyclerView) findViewById(R.id.designersRecyclerView);
        new DesignerHelper(this,designersRecyclerView,filterItemClick);

        RecyclerView collectionsRecyclerView = (RecyclerView) findViewById(R.id.collectionsRecyclerView);
        new CollectionHelper(this,collectionsRecyclerView,filterItemClick);

        RecyclerView productTypeRecyclerView = (RecyclerView) findViewById(R.id.productTypeRecyclerView);
        new TypeHelper(this,productTypeRecyclerView,filterItemClick);

        RecyclerView priceRecyclerView= (RecyclerView) findViewById(R.id.priceRecyclerView);
        new PriceHelper(this,priceRecyclerView,filterItemClick);

        RecyclerView selectorRecyclerView= (RecyclerView) findViewById(R.id.selectorRecyclerView);

        final ArrayList<Parcelable> filters = getIntent().getParcelableArrayListExtra("filters");
        selectorHelper= new SelectorHelper(this,selectorRecyclerView);
        if(filters!=null && filters.size()!=0)
            selectorHelper.addAll(filters);


        rangebar = (RangeBar) findViewById(R.id.rangebar);

        addRange = (Button) findViewById(R.id.addRange);
        addRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterPrice price=new FilterPrice();
                price.setMin((rangebar.getLeftIndex()*50)+"");
                price.setMax((rangebar.getRightIndex()*50)+"");
                price.setMaxVal("INR "+price.getMax());
                price.setMinVal("INR "+price.getMin());
                price.setId(price.getMin()+price.getMax());
                selectorHelper.add(price);
            }
        });
        rangebar.setFormatter(new IRangeBarFormatter() {
            @Override
            public String format(String s) {
                if(Integer.parseInt(s)>=1000) {
                    int value =(Integer.parseInt(s)* 50)/1000;
                    return value  + "k+";
                }
                else {
                    int value =(Integer.parseInt(s)* 50)/1000;
                    return value  + "k";
                }
            }
        });


    }
    SelectorHelper selectorHelper;
    RecyclerViewHelper.OnItemClick filterItemClick = new RecyclerViewHelper.OnItemClick(){
        @Override
        public void onItemClick(Object o) {
            if(o instanceof IIDInterface)
                selectorHelper.add((IIDInterface)o);
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

        intent.putParcelableArrayListExtra("filters",parcelableList);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
