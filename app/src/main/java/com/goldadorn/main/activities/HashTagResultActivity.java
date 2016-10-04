package com.goldadorn.main.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.modules.socialFeeds.HashTagEmptyViewHelper;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.EmptyViewHelper;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HashTagResultActivity extends AppCompatActivity {

    @Bind(R.id.rlChild)
    RelativeLayout childLayout;
    static String hashTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_tag_result);

        ButterKnife.bind(this);
        Intent data = getIntent();
        if (data != null){
            hashTag = data.getStringExtra(IntentKeys.HASHTAG_NAME);
        }
        setupToolbarCustom();
        displayFragment();
    }

    public static final String TAG = "HashTagActivity";
    private void setupToolbarCustom() {

        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        setTitle(Html.fromHtml("<font color='#ffffff'>"+"#"+hashTag+"</font>"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private final String TAG_FRAG = "dj.Fragment";
    private void displayFragment() {
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.replace(childLayout.getId(), new HashTagNewFragment(), TAG_FRAG);
        ft.commit();
    }


    public static class HashTagNewFragment extends SocialFeedFragment{

        @Override
        protected boolean allowPostOptions() {
            return false;
        }

        public void onViewCreated(View view) {
            Log.d(TAG, "onViewCreated ");
            super.onViewCreated(view);
            //fpp = getArguments().getParcelable(IntentKeys.FILTER_POST_PARAMS);
            getFloatingActionsMenu().setVisibility(View.GONE);
            getFabBackImage().setVisibility(View.GONE);
            //followPeopleHelper = new FollowPeopleHelper(getActivity(), getApp().getCookies(),postUpdateResult);
        }

        @Override
        public void onResume() {
            super.onResume();
            updateComments();
        }


        @Override
        protected EmptyViewHelper createEmptyViewHelper() {
            EmptyViewHelper helper = new HashTagEmptyViewHelper(this.getActivity(), this.createEmptyView(this.mRootView), this,
                    this.showInternetError(), this.showInternetRetryButton());
            return helper;
        }

        protected DataManager createDataManager() {
            return new HashTagDataManager(getActivity(), this, getApp().getCookies());
        }

        public class HashTagDataManager extends SocialFeedProjectDataManager {
            public HashTagDataManager(Context context, IDataManagerDelegate delegate, List<Cookie> cookies) {
                super(context, delegate, cookies);
            }

            @Override
            public Map<String, Object> getRefreshDataServerCallParams(PageData data) {
                //return super.getRefreshDataServerCallParams(data);
                return null;
            }

        }

        protected void configDataManager(DataManager dataManager) {
            dataManager.setRefreshEnabled(false);
        }

        @Override
        public Map<String, Object> getNextDataParams(PageData data) {
            Map<String, Object> params = new HashMap<>();
            params.put(URLHelper.LIKE_A_POST.OFFSET, offset);
            params.put("tag", hashTag);
            Log.d(TAG, "req params: " + params);
            return params;
        }

        public String getNextDataURL(PageData pageData) {
            Log.d(TAG, "getNextDataURL ");
            isRefreshingData = false;
            return getApp().getUrlHelper().getSocialFeedServiceURL();
        }

    }
}
