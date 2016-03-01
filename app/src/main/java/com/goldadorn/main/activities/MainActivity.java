package com.goldadorn.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.post.PostBestOfActivity;
import com.goldadorn.main.activities.post.PostNormalActivity;
import com.goldadorn.main.activities.post.PostPollActivity;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.eventBusEvents.SocialPost;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.fragments.BaseFragment;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private BaseFragment activePage;
    private NavigationDataObject activePageData;
    @Bind(R.id.disableApp)
    View disableApp;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;
    private boolean uploadInProgress;
    private WeakReference<SocialFeedFragment> socialPostHost;

    public View getDisableApp()
    {
        //return null;
        return disableApp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        TextView userName= (TextView)headerLayout.findViewById(R.id.userName);
        Button editProfile= (Button)headerLayout.findViewById(R.id.editProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_settings);
                if(navigationDataObject !=null) {
                    action(navigationDataObject);
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        userName.setText(getApp().getUser().getUsername());
        ImageView userImage = (ImageView)headerLayout.findViewById(R.id.userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_settings);
                if(navigationDataObject !=null) {
                    action(navigationDataObject);
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        Picasso.with(this)
                .load(getApp().getUser().getUserpic())
                .placeholder(R.drawable.vector_image_place_holder_profile)
                .into(userImage);


        NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_home);
        if(navigationDataObject !=null)
            action(navigationDataObject);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (activePage!= null && activePage.allowedBack()==false)
        {

        }
        else if (activePage!= null && activePage.allowedBack())
        {

            final Snackbar snackbar = Snackbar.make(layoutParent, "Are you sure you want to exit", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Yes", new View.OnClickListener() {
                public void onClick(View v) {
                    snackbar.dismiss();
                    finish();
                }
            });
            ColoredSnackbar.alert(snackbar).show();
        }
        else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return returnVal;
    }
    @Subscribe
    public void onEvent(AppActions data) {
        action(data.navigationDataObject);
    }
    final public static int POST_FEED=1;
    @Subscribe
    public void onEvent(SocialPost data) {

        if(!uploadInProgress)
        {
            Intent intent=null;
            if(data.type== com.goldadorn.main.model.SocialPost.POST_TYPE_NORMAL_POST)
                intent= new Intent(MainActivity.this, PostNormalActivity.class);
            else if(data.type== com.goldadorn.main.model.SocialPost.POST_TYPE_POLL)
                intent= new Intent(MainActivity.this, PostPollActivity.class);
            else if(data.type== com.goldadorn.main.model.SocialPost.POST_TYPE_BEST_OF)
                intent= new Intent(MainActivity.this, PostBestOfActivity.class);
            if(intent!=null)
            {
                socialPostHost = new WeakReference<>(data.host);
                People people = getApp().getPeople();
                intent.putExtra("NAME", people.getUserName());
                intent.putExtra("FOLLOWER_COUNT", people.getFollowerCount());
                intent.putExtra("FOLLOWING_COUNT", people.getFollowingCount());
                intent.putExtra("PROFILE_PIC", people.getProfilePic());
                intent.putExtra("IS_DESIGNER", people.getIsDesigner());
                intent.putExtra("ID", people.getUserId());
                intent.putExtra("IS_SELF",people.isSelf());
                intent.putExtra("backEnabled",true);
                startActivityForResult(intent, POST_FEED);
            }
        }
        else
        {
            final Snackbar snackbar = Snackbar.make(layoutParent, "Upload is in progress", Snackbar.LENGTH_SHORT);
            ColoredSnackbar.info(snackbar).show();
        }

    }
    final private int postCallToken = IDUtils.generateViewId();
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POST_FEED && resultCode == Activity.RESULT_OK) {
                try {
                    //String fileData=data.getStringExtra("fileData");
                    int type=data.getIntExtra("type", -1);
                    if(type!=-1)
                    {
                        String msg=data.getStringExtra("msg");
                        MultipartEntity reqEntity = new MultipartEntity();


                        try
                        {
                            if(data.getExtras().get("files")!=null) {
                                File[] files = (File[]) data.getExtras().get("files");

                                File file;
                                int count = 1;
                                for (int i = 0; i < files.length; i++) {
                                    file = files[i];
                                    if (file!=null && file.exists() && file.canRead()) {
                                        reqEntity.addPart("file" + count, new FileBody(file));
                                        count++;
                                    }
                                }
                            }
                        }catch (Exception e)
                        {
                            if(data.getExtras().get("filesURIs")!=null) {
                                String[] uris = (String[]) data.getExtras().get("filesURIs");

                                File file;
                                int count = 1;
                                for (int i = 0; i < uris.length; i++) {
                                    file = new File(uris[i]);;
                                    if (file!=null && file.exists() && file.canRead()) {
                                        reqEntity.addPart("file" + count, new FileBody(file));
                                        count++;
                                    }
                                }
                            }
                        }




                        if(msg!=null && msg.equals("")==false)
                            reqEntity.addPart("createpost_message", new StringBody(msg));
                        reqEntity.addPart("createpost_type", new StringBody(type+""));
                        Map<String, Object> params = new HashMap<>();
                        params.put(AQuery.POST_ENTITY, reqEntity);


                        String url = getUrlHelper().getCreatePostServiceURL();
                        ExtendedAjaxCallback ajaxCallback =getAjaxCallback(postCallToken);
                        ajaxCallback.setClazz(String.class);
                        ajaxCallback.setParams(params);
                        ajaxCallback.method(AQuery.METHOD_POST);
                        getAQuery().ajax(url, params, String.class, ajaxCallback);
                        uploadInProgress=true;
                        startUploadProgress();
                    }



                }catch (Exception e)
                {
                    System.out.println(e);
                }
        }
    }
    public void serverCallEnds(int id,String url, Object json, AjaxStatus status) {
        if(id== postCallToken)
        {
            uploadInProgress=false;
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, layoutParent, this);
            if(success)
            {
                if(socialPostHost!=null && socialPostHost.get()!=null)
                    socialPostHost.get().postAdded(new com.goldadorn.main.model.SocialPost());

                socialPostHost = null;

                stopUploadProgress(true);

                Toast.makeText(MainActivity.this, "Success fully posted on wall", Toast.LENGTH_SHORT).show();
            }
            else {
                stopUploadProgress(success);
            }

        }
        else
            super.serverCallEnds(id,url, json, status);
    }
    private void startUploadProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }
    private void stopUploadProgress(boolean success) {
        progressBar.setVisibility(View.GONE);
    }
    public boolean action(NavigationDataObject navigationDataObject) {
        Action action =new Action(this);
        if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_LOGOUT)) {
            logout();
            return true;
        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY))
        {
            String url =(String) navigationDataObject.getActionValue();
            if(url!=null && url.equals("")==false)
            {
                Class target = navigationDataObject.getView();
                if(target==null)
                    target = WebActivity.class;
                Map<String, Object> data=new HashMap<>();
                data.put("URL",url);
                data.put("TITLE", navigationDataObject.getName());
                action.launchActivity(target, null, data, false);
                return true;
            }

        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY))
        {
            Class target = navigationDataObject.getView();
            if(target!=null)
            {
                Map<String, Object> data=null;
                if(navigationDataObject.getParam()!=null)
                    data =(Map<String,Object>) navigationDataObject.getParam();
                if(data==null)
                    data = new HashMap<>();
                data.put("TITLE", navigationDataObject.getName());
                action.launchActivity(target, null, data, false);
                return true;
            }

        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_TEXT_SHARE))
        {

            Map<String,String> map =(Map<String,String>) navigationDataObject.getParam();
            action.textShare(map.get(Action.ATTRIBUTE_DATA),map.get(Action.ATTRIBUTE_TITLE));
            return true;
        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_EXTERNAL))
        {
            String url =(String) navigationDataObject.getActionValue();
            if(url!=null && url.equals("")==false)
            {
               action.openURL(url);
               return true;
            }
        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW))
        {
            Boolean isAdded=false;

            if(activePageData!=null && activePageData.getIdInt()== navigationDataObject.getIdInt())
                isAdded=true;

            if(!isAdded) {
                BaseFragment view = BaseFragment.newInstance(navigationDataObject);
                if (view != null) {
                    setTitle(navigationDataObject.getTitle());
                    FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container, view);
                    ft.commit();
                    activePage = view;
                    activePageData = navigationDataObject;
                    return true;
                }
            }
        }
        return false;
    }
}
