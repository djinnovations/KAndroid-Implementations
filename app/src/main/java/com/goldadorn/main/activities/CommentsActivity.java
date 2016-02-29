package com.goldadorn.main.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.goldadorn.main.R;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.modules.socialFeeds.CommentsView;
import com.goldadorn.main.utils.IDUtils;
import com.kimeeo.library.fragments.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class CommentsActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        String title = getIntent().getExtras().getString("TITLE");
        String id = getIntent().getExtras().getString("POST_ID");
        boolean isPost = getIntent().getExtras().getBoolean("POST");
        setTitle(title + "");


        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(),title, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, CommentsView.class);
        navigationObject.setParam(id);
        BaseFragment mActivePage = BaseFragment.newWebViewInstance(navigationObject);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
