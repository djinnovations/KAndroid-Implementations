package com.goldadorn.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.facebook.appevents.AppEventsConstants;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.post.PostBestOfActivity;
import com.goldadorn.main.activities.post.PostNormalActivity;
import com.goldadorn.main.activities.post.PostPollActivity;
import com.goldadorn.main.dj.support.AppTourGuideHelper;
import com.goldadorn.main.dj.support.SocialLoginUtil;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.eventBusEvents.SocialPost;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.home.HomePage;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.fragments.BaseFragment;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class MainActivity extends BaseDrawerActivity  {
    private BaseFragment activePage;
    private NavigationDataObject activePageData;
    private List<NavigationDataObject> history = new ArrayList<>();
    @Bind(R.id.disableApp)
    View disableApp;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    @Bind(R.id.indicator)
    View indicator;

    @Bind(R.id.transView)
    View transView;
    @Bind(R.id.transViewSearch)
    View transViewSearch;
    @Bind(R.id.transViewNotify)
    View transViewNotify;
    @Bind(R.id.transViewPeople)
    View transViewPeople;
    @Bind(R.id.transViewPost)
    View transViewPost;

//    private ResourceReader resRdr;
//    private MyPreferenceManager coachMarkMgr;
//
//    private final String msgWelcome = "You have landed on the social feed\n"
//            +"where you can see all the social\nactivity happening in the app";
//    private final String msgSearch = "for designers, products\ncollections, trends and more";
//    private final String msgNotification = "check here";
//    private final String msgPeople = "See the user community at GoldAdorn";
//    private final String msgPost = "Create a Post";
    private AppTourGuideHelper mTourHelper;



    public View getPageIndicator()
    {
        return indicator;
    }

    private boolean uploadInProgress;
    private WeakReference<SocialFeedFragment> socialPostHost;
    private boolean backEntry;

    public View getDisableApp()
    {
        //return null;
        return disableApp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_home);
        if(navigationDataObject !=null)
            action(navigationDataObject);

        logEventsAnalytics(AppEventsConstants.EVENT_NAME_ACHIEVED_LEVEL);

        tourThisScreen();
    }



    private void tourThisScreen() {

        /*resRdr = ResourceReader.getInstance(getApplicationContext());
        coachMarkMgr = MyPreferenceManager.getInstance(getApplicationContext());*/
        mTourHelper = AppTourGuideHelper.getInstance(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /*if (!coachMarkMgr.isHomeScreenTourDone())
                    testTourGuide();*/
                mTourHelper.displayHomeTour(MainActivity.this, new View[]{ transView, transViewPeople, transViewSearch,
                        transViewNotify, transViewPost});
            }
        }, 2000);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
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
                    SocialLoginUtil.getInstance(getBaseApplication()).performFbLogout();
                    SocialLoginUtil.getInstance(getBaseApplication()).performGoogleLogout();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1500);
                }
            });
            ColoredSnackbar.alert(snackbar).show();
        }
        else {
            super.onBackPressed();
        }
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
                Log.d(Constants.TAG_APP_EVENT, "AppEventLog: Create_post_initiation");
                logEventsAnalytics(GAAnalyticsEventNames.CREATE_POST_INITIATION);
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

                        if(data.getExtras().get("links")!=null) {
                            String[] links = (String[]) data.getExtras().get("links");

                            String link;
                            int count = 1;
                            for (int i = 0; i < links.length; i++) {
                                link = links[i];
                                if (link!=null && link.equals("")==false) {
                                    reqEntity.addPart("link" + count, new StringBody(link+""));
                                    count++;
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

                Log.d(Constants.TAG_APP_EVENT, "AppEventLog: CREATE_POST_SUCCESS");
                logEventsAnalytics(GAAnalyticsEventNames.CREATE_POST_SUCCESS);
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
        if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW))
        {
            Action action =new Action(this);
            Boolean isAdded=false;

            if(activePageData!=null && activePageData.getIdInt()== navigationDataObject.getIdInt()) {
                isAdded = true;

            }

            if(!isAdded) {
                BaseFragment view = BaseFragment.newInstance(navigationDataObject);
                if (view != null) {
                    //setTitle(navigationDataObject.getTitle());
                    setTitle("");
                    FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container, view);
                    ft.commit();
                    activePage = view;
                    activePageData = navigationDataObject;
                    if(backEntry==false)
                        history.add(activePageData);
                    backEntry=false;
                    return true;
                }
            }
        }
        return super.action(navigationDataObject);
    }

    protected void onResume() {
        super.onResume();
        if(activePage instanceof HomePage)
        {
            ((HomePage)activePage).updateComments();
        }
    }

}
