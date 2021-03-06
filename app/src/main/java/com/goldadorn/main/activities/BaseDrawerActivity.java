package com.goldadorn.main.activities;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.cart.MyOrdersActivity;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.dj.model.UserSession;
import com.goldadorn.main.dj.modules.search.SearchActivity;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.User;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Vijith Menon on 06.03.16.
 */
public class BaseDrawerActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        DefaultProjectDataManager.NotificationCountChangeListener {

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

    @OnClick({R.id.nav_home,
            R.id.nav_timeline, R.id.nav_feed,
            R.id.nav_showcase, R.id.nav_collections,
            R.id.nav_cart, R.id.nav_wishlist,
            R.id.nav_share, R.id.nav_share_facebook,
            R.id.nav_rate_us, R.id.nav_contact_us, R.id.nav_rate_us_new,
            R.id.nav_my_profile,
            R.id.nav_order_tracking,
            R.id.nav_about_us,
            R.id.nav_faqs,
            R.id.nav_shipping_and_return,
            R.id.nav_privacy_policy,
            R.id.nav_terms_conditions,
            R.id.nav_settings,
            R.id.nav_logout, R.id.nav_people, R.id.nav_my_likes,
            R.id.nav_size_guide, R.id.nav_buy_back_policy,
            R.id.nav_dia_qual_guide, R.id.nav_my_orders,
            R.id.rlGoldHolder, R.id.rlDiamondHolder, R.id.nav_my_redemption
    })
    public void menuButtonClick(View view) {
        int id = view.getId();
        menuAction(id);
    }


    @OnClick({R.id.labelHome, R.id.labelTimeLine,
            R.id.labelShowcase, R.id.labelCollection, R.id.labelCart, R.id.labelWishlist, R.id.labelPeople})
    public void menuLabelClick(View view) {
        int id = view.getId();
        if (id == R.id.labelHome)
            id = R.id.nav_home;
        else if (id == R.id.labelTimeLine)
            id = R.id.nav_timeline;
        else if (id == R.id.labelFeed)
            id = R.id.nav_feed;
        else if (id == R.id.labelShowcase)
            id = R.id.nav_showcase;
        else if (id == R.id.labelCollection)
            id = R.id.nav_collections;
        else if (id == R.id.labelCart)
            id = R.id.nav_cart;
        else if (id == R.id.labelWishlist)
            id = R.id.nav_wishlist;
        else if (id == R.id.labelPeople)
            id = R.id.nav_people;
        menuAction(id);
    }


    private final int SHARE_NOTIFY_CALL = IDUtils.generateViewId();
    protected void notifyShareOnfb(String postId){
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(SHARE_NOTIFY_CALL);
        ajaxCallback.method(AQuery.METHOD_GET);
        getAQuery().ajax(ApiKeys.getShareNotifierAPI(postId), String.class, ajaxCallback);
    }


    public void menuAction(int id) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_rate_us_new) {
            RandomUtils.performAppRateTask();
            /*if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);*/
            return;
        }
        if (id == R.id.nav_home) {
            if (isMainActivity)
                return;
            Intent intent = new Intent(this, MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//not preferred - DJphy
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(intent);
                finishAffinity();//preferred - DJphy
            } else {//ICS only
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//not preferred - DJphy
                startActivity(intent);
            }
           /* if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);*/
            return;
        }

        if (id == R.id.nav_my_orders){
            Intent intent = new Intent(this, MyOrdersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            return;
        }

        NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(id);
        if (navigationDataObject != null) {
            action(navigationDataObject);
        }

        /*if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);*/
    }

    private boolean isMainActivity;
    protected void setIsMainActivity(boolean isMainActivity){
        this.isMainActivity = isMainActivity;
    }

    @Bind(R.id.tvGoldCoinCnt)
    TextView tvGoldCoinCnt;
    @Bind(R.id.tvDiamondCnt)
    TextView tvDiamondCnt;

    @Bind({R.id.tvGoldCoinCnt, R.id.tvDiamondCnt, R.id.labelGold, R.id.labelDiamond,
            R.id.nav_share, R.id.nav_share_facebook,
            R.id.nav_rate_us, R.id.nav_contact_us, R.id.nav_rate_us_new,
            R.id.labelHome, R.id.labelFeed,
            R.id.labelTimeLine, R.id.labelShowcase,
            R.id.labelCollection, R.id.labelCart,
            R.id.labelWishlist,
            R.id.labelPeople,
            R.id.nav_my_profile,
            R.id.nav_order_tracking,
            R.id.nav_about_us,
            R.id.nav_faqs,
            R.id.nav_shipping_and_return,
            R.id.nav_privacy_policy,
            R.id.nav_terms_conditions,
            R.id.nav_settings,R.id.nav_size_guide,R.id.nav_my_likes,
            R.id.nav_buy_back_policy, R.id.nav_dia_qual_guide,
            R.id.nav_logout, R.id.nav_my_orders, R.id.nav_my_redemption})
    List<View> views;

    public void setupMenu() {
        View[] viewList = new View[views.size()];
        for (int i = 0; i < views.size(); i++) {
            viewList[i] = views.get(i);
        }
        TypefaceHelper.setFont(viewList);
    }

    @Bind(R.id.pBarGold)
    ProgressView pBarGold;
    @Bind(R.id.pBarDiamond)
    ProgressView pBarDiamond;

    private final int GET_POINTS_CALL = IDUtils.generateViewId();
    protected void setPoints(){
        isPointsUpdateInprogress = true;
        updatePointsUi(false);
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(GET_POINTS_CALL);
        ajaxCallback.method(AQuery.METHOD_GET);
        getAQuery().ajax(ApiKeys.getRedemptionCntAPI(), String.class, ajaxCallback);
    }

    private final String TAG = "BaseDrawerActivity";

    protected void updatePointsUi(boolean isDone){
        if (isDone) {
            pBarGold.setVisibility(View.GONE);
            pBarDiamond.setVisibility(View.GONE);
            tvGoldCoinCnt.setVisibility(View.VISIBLE);
            tvDiamondCnt.setVisibility(View.VISIBLE);
            return;
        }
        pBarGold.setVisibility(View.VISIBLE);
        pBarDiamond.setVisibility(View.VISIBLE);
        tvGoldCoinCnt.setVisibility(View.GONE);
        tvDiamondCnt.setVisibility(View.GONE);
    }

    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        Log.d(TAG, "url queried- "+TAG+": " + url);
        Log.d(TAG, "response- "+TAG+": " + json);
        dismissOverLay();
        if (id == GET_POINTS_CALL){
            isPointsUpdateInprogress = false;
            updatePointsUi(true);
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    drawerLayout, this);
            int gcnt = 0;
            int dcnt = 0;
            JSONObject jsonObject1 = null;
            if (success){
                try {
                    JSONObject jsonObject = new JSONObject(json.toString());
                    jsonObject1 = jsonObject.getJSONObject("points");
                    gcnt = jsonObject1.getInt("gold");
                    dcnt = jsonObject1.getInt("diamond");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tvGoldCoinCnt.setText(String.valueOf(gcnt));
                tvDiamondCnt.setText(String.valueOf(dcnt));

                if (isMainActivity){
                    UserSession.getInstance().setCounts(gcnt, dcnt);
                    try {
                        UserSession.getInstance().setVoucherData(jsonObject1.getJSONArray("coupons"));
                        UserSession.getInstance().setAvailVoucher(jsonObject1.getJSONArray("need"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        else super.serverCallEnds(id, url, json, status);
    }

    static LayerDrawable icon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //setPoints();
        if (!isMainActivity){
            UserSession session = UserSession.getInstance();
            tvGoldCoinCnt.setText(session.getGoldCoinCount());
            tvDiamondCnt.setText(session.getDiamondCnt());
        }
        Log.d(Constants.TAG, "onCreateOptionsMenu: BaseDrawerActivity");
        /*getMenuInflater().inflate(R.menu.main, menu);

        MenuItem itemCart = menu.findItem(R.id.nav_my_notifications);
        icon = (LayerDrawable) itemCart.getIcon();*/
        setMenuCustom(menu);

        return true;
    }


    private void setMenuCustom(Menu menu) {
        if (menu != null) {
            menu.clear();
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.nav_my_notifications);

        if (item != null) {
            MenuItemCompat.setActionView(item, R.layout.notification_badge);

            TextView textView = (TextView) item.getActionView()
                    .findViewById(R.id.tvNotifyCount);
            item.getActionView().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(item);
                }
            });

            String unreadCount = RandomUtils.getUnreadCount();
            if (TextUtils.isEmpty(/*mCount*/unreadCount)) {
                if (textView != null) {
                    textView.setVisibility(View.INVISIBLE);
                }
            } else if (/*mCount*/unreadCount.equalsIgnoreCase("0")) {
                if (textView != null) {
                    textView.setVisibility(View.INVISIBLE);
                }
            } else {
                if (textView != null) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(/*mCount*/unreadCount);
                }
            }
        }
    }


    private String mCount = "";

    @Override
    public void onNotificationCountChanged(String count) {
        boolean shouldDraw = mCount.equalsIgnoreCase(count);
        Log.d("djdrawer", "onNotificationCountChanged.. should not draw?: " + shouldDraw);
        if (shouldDraw)
            return;
        mCount = count;
        this.supportInvalidateOptionsMenu();
    }


    /*public static void displayUnreadData(final Context context, final String count) {
        if (count == null)
            return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setBadgeCount(context, icon, count);

            }
        }, 100);
    }

  *//*  private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem, null);
        view.setBackgroundResource(backgroundImageId);
        TextView textView = (TextView) view.findViewById(R.id.count);

        if (count == 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }*//*


    private static Button btnBadge;
    private static TextView tvBadge;
    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        float xPosFortv = 57.3f;
        DisplayProperties mProp = DisplayProperties.getInstance(context, 1);
        if (Constants.CURRENT_API_LEVEL < Build.VERSION_CODES.LOLLIPOP){
            Log.d("djbadge", "version pre-5");
            if (btnBadge == null) {
                btnBadge = new Button(context);
                int xCord = (int) (56.5 * mProp.getXPixelsPerCell());
                int yCord = (int) (2 * mProp.getYPixelsPerCell());
                int width = (int) (2.4 * mProp.getXPixelsPerCell());
                int height = (int) (2.4 * mProp.getYPixelsPerCell());

                if (MainActivity.getRlMain() == null )
                    return;
                mSetLayoutParams(MainActivity.getRlMain(), btnBadge,
                        xCord, yCord, width, height);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btnBadge.setBackground(ResourceReader.getInstance(context).getDrawableFromResId(R.drawable.circle_bg));
                }else
                    btnBadge.setBackgroundDrawable(ResourceReader.getInstance(context).getDrawableFromResId(R.drawable.circle_bg));

                tvBadge = new TextView(context);
                xCord = (int) (xPosFortv * mProp.getXPixelsPerCell());
                yCord = (int) (2.2 * mProp.getYPixelsPerCell());
                mSetLayoutParams(MainActivity.getRlMain(), tvBadge, xCord, yCord,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvBadge.setTextSize(8);
                tvBadge.setTypeface(null, Typeface.BOLD);
                tvBadge.setTextColor(Color.WHITE);
            }
            RelativeLayout.LayoutParams tvParams = (RelativeLayout.LayoutParams) tvBadge.getLayoutParams();
            if (count.length() >= 2){
                tvParams.leftMargin = (int) ((xPosFortv - 0.3f) * mProp.getXPixelsPerCell());
            }
            else {tvParams.leftMargin = (int) (xPosFortv  * mProp.getXPixelsPerCell());
            }
            tvBadge.setText(count);
            btnBadge.setEnabled(false);
            Log.d("djbadge", "badge placed");
            return;
        }

        BadgeDrawable badge; // Reuse drawable if possible
        icon.mutate();
        *//*badge = new BadgeDrawable(context);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);*//*

        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge); //getting the layer 2
        if (reuse != null && reuse instanceof BadgeDrawable) {
            Log.d("dj", "reuse not null");
            badge = (BadgeDrawable) reuse;
        } else {
            Log.d("dj", "reuse is null");
            badge = new BadgeDrawable(context);
            icon.setDrawableByLayerId(R.id.ic_badge, badge);
        }
        Rect bounds = badge.copyBounds();
        Log.d("dj", "badge right: "+bounds.right);
        Log.d("dj", "badge left: "+bounds.left);
        Log.d("dj", "badge top: "+bounds.top);
        Log.d("dj", "badge bottom: "+bounds.bottom);
        *//*if (bounds.right == 0 && bounds.left == 0 && bounds.top == 0 && bounds.bottom == 0)
            badge.setBounds(0, 0, 50, 50);*//*
        badge.setCount(count);
       // icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }



    private static void mSetLayoutParams(RelativeLayout parent, View child, int xCord, int yCord, int width, int height){

        RelativeLayout.LayoutParams childParams = new RelativeLayout.LayoutParams(
                width, height);

        if(xCord < 0)
            xCord = 0;
        if(yCord < 0)
            yCord = 0;
        childParams.leftMargin = xCord;
        childParams.topMargin = yCord;

        child.setLayoutParams(childParams);
        parent.addView(child);
    }*/


    protected void bindViews() {

    }

    boolean isFirstTime = true;

    @Override
    public void onStart() {
        super.onStart();
        Log.d("djmain", "onstart-basedrawer");
        UserInfoCache.getInstance(this).start();
        if (!isFirstTime) {
            isFirstTime = false;
            onNotificationCountChanged(RandomUtils.getUnreadCount());
        } else isFirstTime = false;
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
        if (id == R.id.nav_my_search) {
            startActivity(new Intent(this, SearchActivity.class));
            //Toast.makeText(getApplication(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
            return true;
        }
        boolean returnVal = false;
        NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(id);
        if (navigationDataObject != null)
            returnVal = action(navigationDataObject);

        if (returnVal)
            return returnVal;
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean returnVal = false;
        NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(id);
        if (navigationDataObject != null)
            returnVal = action(navigationDataObject);
        drawerLayout.closeDrawer(GravityCompat.START);
        return returnVal;
    }

    protected boolean isPointsUpdateInprogress = false;

    private void setupHeader() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isPointsUpdateInprogress)
                    setPoints();
            }
        };
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        vNavigation.setNavigationItemSelectedListener(this);

        View headerLayout = vNavigation.getHeaderView(0);

        TextView userName = (TextView) headerLayout.findViewById(R.id.userName);
        /*Button editProfile = (Button) headerLayout.findViewById(R.id.editProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDataObject
                        navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_my_profile);
                if (navigationDataObject != null) {
                    action(navigationDataObject);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });*/
        TypefaceHelper.setFont(userName);
        User user = getApp().getUser();
        if (user != null) {
            userName.setText(user.getName());
            ImageView userImage = (ImageView) headerLayout.findViewById(R.id.userImage);
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_my_profile);
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
