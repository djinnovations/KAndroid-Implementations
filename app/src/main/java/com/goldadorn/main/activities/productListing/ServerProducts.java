package com.goldadorn.main.activities.productListing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.model.FilterProductListing;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.nineoldandroids.animation.Animator;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BhavinPadhiyar on 19/04/16.
 */
public class ServerProducts extends BaseActivity {

    public static final int FILTER_APPLY = 2;
    private SelectorHelper selectorHelper;

    public ViewGroup getLayoutParent() {
        return layoutParent;
    }

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    private ProductsFragment mActivePage;

    @Bind(R.id.applyFilters)
    Button applyFilters;


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

    public boolean getIsPTB() {
        return isCallerPTB;
    }

    private HashSet<FilterProductListing> previouslySelected = new HashSet<>();
    ArrayList<FilterProductListing> tosendbackList;

    @Subscribe
    public void onEvent(FilterProductListing data) {
        if (isCallerPTB) {
            boolean status = previouslySelected.add(data);
            if (!status) {
                previouslySelected.remove(data);
                tosendbackList.remove(data);
            } /*else {*/
            if (canProceed(data)) {
                //// TODO: 13-07-2016  for selection made ui change;
                if (!alreadyExist(data)) {
                    tosendbackList.add(data);
                    //visibleNum.put(tosendbackList.size(), data);
                }
            }
            //}
            showTitle(previouslySelected.size());
        } else {
            ArrayList<FilterProductListing> dataList = new ArrayList<FilterProductListing>();
            dataList.add(0, data);
            processItem(dataList);
        }
    }


    /*private ArrayList<FilterProductListing> editSendBackList(){
        for (FilterProductListing fil)
    }*/

    private boolean alreadyExist(FilterProductListing incoming) {
        for (FilterProductListing obj : tosendbackList) {
            if (incoming.equals(obj))
                return true;
        }
        return false;
    }

    public boolean getCanCheckSelected() {
        return canCheckSelected;
    }

    private boolean canCheckSelected = false;

    private boolean canProceed(FilterProductListing file) {
        if (previouslySelected.size() == 4) {
            canCheckSelected = false;
            previouslySelected.remove(file);
            Toast.makeText(getApplicationContext(), "You can only select a maximum of three products", Toast.LENGTH_SHORT).show();
            return false;
        }
        canCheckSelected = true;
        return true;
    }

    protected void processItem(ArrayList<FilterProductListing> data) {

    }

    View.OnClickListener mSelectionDone = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (previouslySelected.size() < 2) {
                Toast.makeText(getApplicationContext(), "Select at least two products to create the post", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("djpost", "tosendbackList.size = " + tosendbackList.size());
            processItem(/*tosendbackList*/new ArrayList<>(previouslySelected));//// TODO: 13-07-2016  after done is clicked
        }
    };

    public static final String SORT_PROD_DEFAULT_PRICE = "prodDefaultPrice";
    public static final String SORT_DATE_ADDED = "dateAdded";
    public static final String SORT_SOLD_UNITS = "soldUnits";
    public String sort = SORT_SOLD_UNITS;

    private boolean isCallerPTB = false;

    private void showTitle(int count) {
        _layTitle.bringToFront();
        ((TextView) _layTitle.findViewById(R.id.tvSelectedItems)).setText("Selected products: " + count);
        if (_layTitle.getVisibility() == View.GONE) {
            _layTitle.setVisibility(View.VISIBLE);
            _layTitle.setAlpha(0.6f);
            (_layTitle.findViewById(R.id.lldonebtnholder)).setAlpha(1);
            try {
                startAnim(_layTitle, R.anim.anim_dialog_fall_down);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    startAnim(_layTitle, R.anim.anim_dialog_go_up);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            _layTitle.setVisibility(View.GONE);
                            _layTitle.setAlpha(0.3f);
                        }
                    }, 600);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3000);*/

    }


    private void pullBackUpIsAlreadyShown() {
        if (_layTitle.getVisibility() == View.VISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {
                        startAnim(_layTitle, R.anim.anim_dialog_go_up);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                _layTitle.setVisibility(View.GONE);
                                _layTitle.setAlpha(0.6f);
                                (_layTitle.findViewById(R.id.lldonebtnholder)).setAlpha(1);
                            }
                        }, 400);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 200);
        }
    }

    private void startAnim(View view, int animResID) throws Exception {

        Animation anim = AnimationUtils.loadAnimation(getBaseContext(), animResID);
        view.startAnimation(anim);
    }

    /*SparseArray<FilterProductListing> visibleNum;
    public String getSelectedProdCount(FilterProductListing incoming) {
        for (FilterProductListing obj: tosendbackList)
        return String.valueOf(tosendbackList.size());
    }*/

    @Bind(R.id.layTitle)
    View _layTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector_holder2);
        ButterKnife.bind(this);

        isCallerPTB = false;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Our Products");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_icon_cross_brown);

        /*if (getIntent() != null)
            isCallerPTB = getIntent().getBooleanExtra(IntentKeys.CALLER_PTB, false);
        _layTitle.setVisibility(View.GONE);
        _layTitle.setAlpha(0.3f);
        (_layTitle.findViewById(R.id.tvDone)).setOnClickListener(mSelectionDone);
        (_layTitle.findViewById(R.id.tvDone)).setAlpha(1);
        tosendbackList = new ArrayList<>();*/
        prepareSelectionIfNeeded(getIntent());

        sortBestSelling.setSelected(true);
        sortBestSelling.setAlpha(/*Float.parseFloat("1")*/1);
        sortNew.setAlpha(/*Float.parseFloat(".3")*/0.3f);
        sortPrice.setAlpha(/*Float.parseFloat(".3")*/0.3f);
        lastSorted = sortBestSelling;
        refreshView(filters, sort);

        RecyclerView selectorRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selectorHelper = new SelectorHelper(this, selectorRecyclerView);
        selectorHelper.onRemoveListner(onRemoveListner);
        TypefaceHelper.setFont(applyFilters, (_layTitle.findViewById(R.id.tvDone)), (_layTitle.findViewById(R.id.tvSelectedItems)));
    }


    private void prepareSelectionIfNeeded(Intent intent) {
        if (intent != null)
            isCallerPTB = intent.getBooleanExtra(IntentKeys.CALLER_PTB, false);
        _layTitle.setVisibility(View.GONE);
        _layTitle.setAlpha(0.6f);
        (_layTitle.findViewById(R.id.tvDone)).setOnClickListener(mSelectionDone);
        (_layTitle.findViewById(R.id.lldonebtnholder)).setAlpha(1);
        tosendbackList = new ArrayList<>();
        //visibleNum = new SparseArray<>();
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

    private void refreshView(ArrayList<Parcelable> filters, String sort) {
        if (filters != null) {
            this.filters = filters;
            selectorHelper.removeAll();
            selectorHelper.addAll(filters);
            if (filters.size() == 0)
                setTitle("Our Products");
            else
                setTitle("Your Selections");
        } else
            setTitle("Our Products");


        pullBackUpIsAlreadyShown();
        /*previouslySelected.clear();
        tosendbackList.clear();*/
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(), "",
                NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, ProductsFragment.class);
        mActivePage = (ProductsFragment) BaseFragment.newInstance(navigationObject);
        mActivePage.setFilters(filters, sort);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();
        //_layTitle.bringToFront();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.applyFilters)
    void applyFilters() {
        Intent intent = new Intent(this, FilterSelector.class);
        if (filters != null)
            intent.putParcelableArrayListExtra("filters", filters);
        startActivityForResult(intent, FILTER_APPLY);
    }

    ArrayList<Parcelable> filters;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILTER_APPLY && resultCode == Activity.RESULT_OK) {
            final ArrayList<Parcelable> filters = data.getParcelableArrayListExtra("filters");
            Handler h = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    refreshView(filters, sort);
                }
            };
            h.postDelayed(r, 200);

        }
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
