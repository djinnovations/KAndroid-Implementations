package com.goldadorn.main.dj.modules.search;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.dj.model.CustomizationStepResponse;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.google.gson.Gson;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 21-10-2016.
 */
public class SearchActivity extends BaseActivity{

    private static final String TAG = "SearchActivity";
    @Bind(R.id.container)
    FrameLayout container;
    SearchTabFragment tabFragment;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.llRoot)
    LinearLayout llRoot;
    @Bind(R.id.etSearch)
    EditText etSearch;
    @Bind(R.id.progressBar)
    View progressBar;
    @Bind(R.id.ivGlass)
    ImageView ivGlass;

    private void setDrawableForMagGlass() {
        int color = ResourceReader.getInstance(Application.getInstance()).getColorFromResource(R.color.colorPrimary);
        ivGlass.setImageDrawable(new IconicsDrawable(Application.getInstance())
                .icon(CommunityMaterial.Icon.cmd_magnify)
                .color(color)
                .sizeDp(20));
    }

    View.OnClickListener searchClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String txt = etSearch.getText().toString().trim();
            if (txt.length() < 3) {
                Toast.makeText(getApplicationContext(), "Enter Minimum three chars to begin a Search", Toast.LENGTH_SHORT).show();
                return;
            }
            performSearch();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar.setVisibility(View.GONE);
        ivGlass.setOnClickListener(searchClick);
        setDrawableForMagGlass();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabFragment = new SearchTabFragment();
        getSupportFragmentManager().beginTransaction().replace(container.getId(), tabFragment).commit();
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String txt = v.getText().toString().trim();
                    if (txt.length() < 3) {
                        Toast.makeText(getApplicationContext(), "Enter Minimum three chars to begin a Search", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }


    public void gotoDesigner(){
        NavigationDataObject dataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_showcase);
        action(dataObject);
    }


    public void gotoCollection(int desId, int collId){
        RandomUtils.launchCollectionScreen(this, desId, collId);
    }


    public void onPageChange(int page){
        tabFragment.gotoPage(page);
    }

    private void performSearch() {
        /*if (tabFragment != null)
            getSupportFragmentManager().beginTransaction().remove(tabFragment).commit();*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            llRoot.setBackground(ResourceReader.getInstance(getApplicationContext()).getDrawableFromResId(R.drawable.square_black_filling));
        }else llRoot.setBackgroundDrawable(ResourceReader.getInstance(getApplicationContext()).getDrawableFromResId(R.drawable.square_black_filling));
        tabFragment.getDesignerSearchFragment().callLoad();
        tabFragment.getCollectionSearchFragment().callLoad();
        tabFragment.getProductSearchFragment().callLoad();
        queryForSocialCommSearch();
        //SearchTabFragment.adapter.notifyDataSetChanged();
    }


    public String getTag(){
        String tag = etSearch.getText().toString().trim();
        if (tag.equalsIgnoreCase("ring") || tag.equalsIgnoreCase("rings")){
            tag = " "+tag;
        }
        return tag;
    }


    public static final int SEARCH_ECOMM_CALL = IDUtils.generateViewId();
    public static final int SEARCH_SOCIAL_CALL = IDUtils.generateViewId();

    //"designer":"1","tag":"go","designerOffset":"0"
    //"tag": "bang", "prodOffset": 0, "prod" :1, "collOffset": 0, "coll" :1
    public void queryForSocialCommSearch(){
        progressBar.setVisibility(View.VISIBLE);
        WindowUtils.justPlainOverLay = true;
        showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("tag", getTag());
        paramsMap.put("prodOffset", "0");
        paramsMap.put("prod", "1");
        paramsMap.put("collOffset", "0");
        paramsMap.put("coll", "1");
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(SEARCH_ECOMM_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        getAQuery().ajax(ApiKeys.getSearchAPI(false), new HashMap<>(paramsMap), String.class, ajaxCallback);

        paramsMap.clear();
        paramsMap.put("designer", "1");
        paramsMap.put("tag", getTag());
        paramsMap.put("designerOffset", "0");
        ExtendedAjaxCallback ajaxCallback1 = getAjaxCallback(SEARCH_SOCIAL_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        getAQuery().ajax(ApiKeys.getSearchAPI(true), paramsMap, String.class, ajaxCallback1);
    }


    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        //
        Log.d(TAG, "url queried- "+TAG + ": " + url);
        Log.d(TAG, "response- "+TAG + ": " + json);
        WindowUtils.justPlainOverLay = false;
        dismissOverLay();
        progressBar.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            llRoot.setBackground(null);
        }else llRoot.setBackgroundDrawable(null);


        if (id == SEARCH_ECOMM_CALL){
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    container, this);
            if (success){
                SearchDataObject dataObject = new Gson().fromJson((String) json, SearchDataObject.class);
                if (dataObject != null){
                    //EverythingSearchFragment fragment = tabFragment.getFragment();
                    dataObject.onLoadColl();
                    dataObject.onLoadProducts();
                    tabFragment.getEverythingSearchFragment().updateEComm(dataObject);
                }
            }
        }
        else if (id == SEARCH_SOCIAL_CALL){
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    container, this);
            if (success){
                SearchDataObject dataObject = new Gson().fromJson((String) json, SearchDataObject.class);
                if (dataObject != null){
                    //EverythingSearchFragment fragment = tabFragment.getFragment();
                    dataObject.onLoadDes();
                    tabFragment.getEverythingSearchFragment().updateSocial(dataObject);
                }
            }
        }
        else super.serverCallEnds(id, url, json, status);
    }
}
