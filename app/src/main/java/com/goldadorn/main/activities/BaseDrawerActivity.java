package com.goldadorn.main.activities;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.NavigationDataObject;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijith Menon on 06.03.16.
 */
public class BaseDrawerActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Bind(R.id.drawerLayout)
    protected DrawerLayout drawerLayout;
    @Bind(R.id.vNavigation)
    NavigationView vNavigation;
    Toolbar toolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        ButterKnife.bind(this);
        setupToolbar();
        bindViews();
        setupHeader();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void bindViews() {

    }

    protected void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean returnVal=false;
        NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(id);
        if(navigationDataObject !=null)
            returnVal = action(navigationDataObject);

        if(returnVal)
            return returnVal;
        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean returnVal=false;
        NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(id);
        if(navigationDataObject !=null)
            returnVal = action(navigationDataObject);
        drawerLayout.closeDrawer(GravityCompat.START);
        return returnVal;
    }

    private void setupHeader() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();


        vNavigation.setNavigationItemSelectedListener(this);

        View headerLayout = vNavigation.getHeaderView(0);

        TextView userName= (TextView)headerLayout.findViewById(R.id.userName);
        Button editProfile= (Button)headerLayout.findViewById(R.id.editProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDataObject
                        navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_settings);
                if(navigationDataObject !=null) {
                    action(navigationDataObject);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
        userName.setText(getApp().getUser().getName());
        ImageView userImage = (ImageView)headerLayout.findViewById(R.id.userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_settings);
                if(navigationDataObject !=null) {
                    action(navigationDataObject);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
        Picasso.with(this)
               .load(getApp().getUser().getImageUrl())
               .placeholder(R.drawable.vector_image_place_holder_profile)
               .into(userImage);

    }

}
