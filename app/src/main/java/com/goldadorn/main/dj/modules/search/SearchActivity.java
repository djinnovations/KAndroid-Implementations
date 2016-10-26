package com.goldadorn.main.dj.modules.search;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.dj.model.CustomizationStepResponse;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.google.gson.Gson;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 21-10-2016.
 */
public class SearchActivity extends BaseDrawerActivity{

    private static final String TAG = "SearchActivity";
    @Bind(R.id.container)
    FrameLayout container;
    SearchTabFragment tabFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        tabFragment = new SearchTabFragment();
        getSupportFragmentManager().beginTransaction().replace(container.getId(), tabFragment).commit();
        queryForSocialCommSearch();
    }


    public String getTag(){
        return "Gold";
    }


    public static final int SEARCH_ECOMM_CALL = IDUtils.generateViewId();
    public static final int SEARCH_SOCIAL_CALL = IDUtils.generateViewId();

    //"designer":"1","tag":"go","designerOffset":"0"
    //"tag": "bang", "prodOffset": 0, "prod" :1, "collOffset": 0, "coll" :1
    public void queryForSocialCommSearch(){
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

        if (id == SEARCH_ECOMM_CALL){
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    container, this);
            if (success){
                SearchDataObject dataObject = new Gson().fromJson((String) json, SearchDataObject.class);
                if (dataObject != null){
                    //EverythingSearchFragment fragment = tabFragment.getFragment();
                    dataObject.onLoadColl();
                    dataObject.onLoadProducts();
                    EverythingSearchFragment.updateEComm(dataObject);
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
                    EverythingSearchFragment.updateSocial(dataObject);
                }
            }
        }
        else super.serverCallEnds(id, url, json, status);
    }
}
