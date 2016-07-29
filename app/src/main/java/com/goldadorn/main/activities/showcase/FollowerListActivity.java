package com.goldadorn.main.activities.showcase;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.dj.fragments.FollowViewFragment;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.utils.IDUtils;
import com.kimeeo.library.fragments.BaseFragment;

import butterknife.ButterKnife;

public class FollowerListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        String title = getIntent().getExtras().getString("TITLE");
        String id = getIntent().getExtras().getString("USER_ID");
        setTitle(title + "");


        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(),title,
                NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, FollowViewFragment.class);
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
