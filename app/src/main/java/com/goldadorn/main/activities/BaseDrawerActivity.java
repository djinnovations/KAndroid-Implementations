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
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.User;
import com.goldadorn.main.utils.TypefaceHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

        setupMenu();
    }

    @OnClick({ R.id.nav_home,R.id.nav_shop_by, R.id.nav_timeline,R.id.nav_feed, R.id.nav_showcase,R.id.nav_collections,R.id.nav_cart,R.id.nav_wishlist,R.id.nav_share,R.id.nav_share_facebook,R.id.nav_rate_us,R.id.nav_contact_us,
            R.id.nav_my_profile,
            R.id.nav_order_tracking,
            R.id.nav_about_us,
            R.id.nav_faqs,
            R.id.nav_shipping_and_return,
            R.id.nav_privacy_policy,
            R.id.nav_terms_conditions,
            R.id.nav_settings,
            R.id.nav_logout
    })
    public void menuButtonClick(View view) {
        int id = view.getId();
        menuAction(id);
    }







    @OnClick({ R.id.labelHome,R.id.labelshopBy,R.id.labelTimeLine,R.id.labelShowcase,R.id.labelCollection,R.id.labelCart,R.id.labelWishlist })
    public void menuLabelClick(View view) {
        int id = view.getId();
        if(id == R.id.labelHome)
            id =R.id.nav_home;
        else if(id == R.id.labelTimeLine)
            id =R.id.nav_timeline;
        else if(id == R.id.labelFeed)
            id =R.id.nav_feed;
        else if(id == R.id.labelShowcase)
            id =R.id.nav_showcase;
        else if(id == R.id.labelCollection)
            id =R.id.nav_collections;
        else if(id == R.id.labelCart)
            id =R.id.nav_cart;
        else if(id == R.id.labelWishlist)
            id =R.id.nav_wishlist;
        menuAction(id);
    }

    private void menuAction(int id) {
        NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(id);
        if(navigationDataObject !=null)
            action(navigationDataObject);

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }


    @Bind({ R.id.nav_share,R.id.nav_share_facebook,R.id.nav_rate_us,R.id.nav_contact_us,R.id.labelHome,R.id.labelFeed,R.id.labelTimeLine,R.id.labelShowcase,R.id.labelCollection,R.id.labelCart,R.id.labelWishlist,R.id.labelshopBy,
            R.id.nav_my_profile,
            R.id.nav_order_tracking,
            R.id.nav_about_us,
            R.id.nav_faqs,
            R.id.nav_shipping_and_return,
            R.id.nav_privacy_policy,
            R.id.nav_terms_conditions,
            R.id.nav_settings,
            R.id.nav_logout})
    List<View> views;
    public void setupMenu()
    {
        View[] viewList = new View[views.size()];
        for (int i = 0; i < views.size(); i++) {
            viewList[i]=views.get(i);
        }
        TypefaceHelper.setFont(viewList);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void bindViews() {

    }

    @Override
    public void onStart() {
        super.onStart();
        UserInfoCache.getInstance(this).start();
    }

    @Override
    public void onStop() {
        super.onStop();
        UserInfoCache.getInstance(this).stop();
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
                        navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_edit_profile);
                if(navigationDataObject !=null) {
                    action(navigationDataObject);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
        TypefaceHelper.setFont(userName);
        User user =getApp().getUser();
        if(user!=null) {
            userName.setText(user.getName());
            ImageView userImage = (ImageView) headerLayout.findViewById(R.id.userImage);
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationDataObject navigationDataObject =
                            (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_settings);
                    if (navigationDataObject != null) {
                        action(navigationDataObject);
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            });
            Picasso.with(this).load(user.getImageUrl()).placeholder(R.drawable
                    .vector_image_place_holder_profile_dark).into(userImage);
        }

    }

}
