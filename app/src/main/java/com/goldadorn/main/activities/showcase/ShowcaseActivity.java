package com.goldadorn.main.activities.showcase;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.db.DbHelper;
import com.goldadorn.main.dj.fragments.FilterTimelineFragment;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.db.Tables.Users;
import com.goldadorn.main.dj.model.BookAppointmentDataObj;
import com.goldadorn.main.dj.model.FilterPostParams;
import com.goldadorn.main.dj.support.AppTourGuideHelper;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.TimelineResponse;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijith Menon on 6/3/16.
 */
public class ShowcaseActivity extends BaseDrawerActivity implements CollectionsFragment.UpdateLikes, ProductsFragment.UpdateProductCount {
    private final static int UISTATE_COLLECTION = 0;
    private final static int UISTATE_PRODUCT = 1;
    private final static int UISTATE_SOCIAL = 2;
    private final static String TAG = ShowcaseActivity.class.getSimpleName();
    private final static boolean DEBUG = true;

    private int mUIState = UISTATE_COLLECTION;


    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.coordinatorlayout)
    CoordinatorLayout mCoordinatorLayout;


    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.tabs)
    View mTabLayout;

    @Bind(R.id.previous)
    ImageView mPrevious;
    @Bind(R.id.next)
    ImageView mNext;
    @Bind(R.id.container_designer_overlay)
    LinearLayout mBrandButtonsLayout;
    @Bind(R.id.top_layout)
    View topLayout;

    @Bind(R.id.progress_frame)
    View mProgressFrame;

    @Bind(R.id.frame)
    FrameLayout mFrame;
    @Bind(R.id.frame_scroll_dummy)
    FrameLayout mFrameScrollDummy;
    @Bind(R.id.frame_no_scroll_dummy)
    FrameLayout mFrameNoScrollDummy;

    @Bind(R.id.view_pager_dummy)
    View mPagerDummy;

    @Bind(R.id.overlay)
    View mOverlay;


    private Context mContext;
    private final ShowCaseCallback mShowCaseCallback = new ShowCaseCallback();
    private ShowcasePagerAdapter mShowCaseAdapter;
    private OverlayViewHolder mOverlayVH;
    private TabViewHolder mTabViewHolder;
    private User mUser;
    private List<UserChangeListener> mUserChangeListeners = new ArrayList<>(4);

    private int mStartHeight, mCollapsedHeight;
    private int mVerticalOffset = 0;
    private int mCurrentPosition = 0;
    private Handler mHandler = new Handler();
    public static boolean isCollectionLike = false;





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BAA && resultCode == RESULT_OK) {
            mUser.numAppts = mUser.numAppts + 1;
            mOverlayVH.appointment_count.setText(String.format(Locale.getDefault(), "%d", mUser.numAppts));
        }
    }

    private boolean canProceedToBAA() {
        if (mUser != null) {
            if (!TextUtils.isEmpty(mUser.name) && !TextUtils.isEmpty(mUser.getImageUrl())
                    && mUser.id != -1) {
                return true;
            }
            return false;
        }
        return false;
    }


    private final int REQUEST_CODE_BAA = IDUtils.generateViewId();

    public void displayBookAppointment() {

        try {
            if (!canProceedToBAA()) {
                Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, BookAppointment.class);
            /*Bundle bundle = new Bundle();
            bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_NAME, mUser.name);
            bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_URL, mUser.getImageUrl());
            bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_ID, String.valueOf(mUser.id));
            intent.putExtras(bundle);*/
            BookAppointmentDataObj baaDataObj = new BookAppointmentDataObj(BookAppointment.DESIGNER);
            baaDataObj.setDesignerId(String.valueOf(mUser.id))
                    .setItemImageUrl(mUser.getImageUrl())
                    .setItemName(mUser.name);
            intent.putExtra(IntentKeys.BOOK_APPOINT_DATA, baaDataObj);
            startActivityForResult(intent, REQUEST_CODE_BAA);
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* final Dialog dialog = new Dialog(this, R.style.BookAppointmentDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        final View tempView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_book_appointment_sc1, null);
        WindowManager.LayoutParams tempParams = new WindowManager.LayoutParams();
        tempParams.copyFrom(dialog.getWindow().getAttributes());

		*//*tempParams.width = dialogWidthInPx;
        tempParams.height = dialogHeightInPx;*//*
        tempParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        tempParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        tempParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        tempParams.dimAmount = 0; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.setContentView(tempView);

        ((TextView) dialog.findViewById(R.id.tvPositive)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etPhNum = (EditText) dialog.findViewById(R.id.etPhNum);
                EditText etMessage = (EditText) dialog.findViewById(R.id.etMsgAppoint);
                if (etPhNum.getText().toString().trim().length() < 10) {
                    Toast.makeText(getApplicationContext(), "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(etMessage.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Please leave a message", Toast.LENGTH_SHORT).show();
                    return;
                }
                doTransition(dialog, tempView, etPhNum.getText().toString().trim(), etMessage.getText().toString().trim());
            }
        });

        dialog.findViewById(R.id.tvNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.getWindow().setAttributes(tempParams);
        dialog.show();*/
    }

    /*interface ServerResponseListener {
        void onPositiveResponse();

        void onNegativeResponse();
    }

    private ServerResponseListener srspListener;
    private final int BOOK_APPOINTMENT_CALL = IDUtils.generateViewId();

    private void doTransition(final Dialog dialog, final View tempView, String phone, String msg) {
        srspListener = new ServerResponseListener() {
            @Override
            public void onPositiveResponse() {
                bringUpSuccessBookingScreen(dialog, tempView);
            }

            @Override
            public void onNegativeResponse() {
                Toast.makeText(getApplicationContext(), "Not able to make an Appointment, Please try again ", Toast.LENGTH_LONG).show();
            }
        };
        Map<String, String> params = new HashMap<>();
        params.put("designer", mUser.name);
        params.put("phone", phone);
        params.put("message", msg);
        Log.d("djweb", "req params- book appointment: " + params);
        sendAppointmentRequest(params);
    }


    private void bringUpSuccessBookingScreen(final Dialog dialog, final View oldView) {
        try {
            startAnim(oldView, R.anim.slide_out_into_left);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                oldView.setAlpha(0);
                oldView.setVisibility(View.GONE);
                oldView.setVisibility(View.VISIBLE);
                View newView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_book_appointment_sc2, null);
                try {
                    dialog.setContentView(newView);//// TODO: 19-06-2016
                    startAnim(newView, R.anim.slide_in_from_right);
                    newView.setAlpha(1);
                    newView.findViewById(R.id.tvOkay).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 400);
    }


    private void startAnim(View view, int animResID) throws Exception {

        Animation anim = AnimationUtils.loadAnimation(this, animResID);
        view.startAnimation(anim);
    }

    private void sendAppointmentRequest(Map<String, String> params) {
        showOverLay(null, R.color.colorPrimaryDark);
        ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(BOOK_APPOINTMENT_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        getAQueryCustom().ajax(ApiKeys.getAppointmentAPI(), params, String.class, ajaxCallback);
    }


    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        Log.d("djweb", "url queried- ShowcaseActivity: " + url);
        Log.d("djweb", "response- ShowcaseActivity: " + json);
        dismissOverLay();
        if (id == BOOK_APPOINTMENT_CALL) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mTabLayout, this);
            if (success) {
                if (json == null)
                    srspListener.onNegativeResponse();
                else {
                    srspListener.onPositiveResponse();
                }
            } else srspListener.onNegativeResponse();
        } else super.serverCallEnds(id, url, json, status);
    }*/


    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        Log.d("djweb", "url queried- ShowcaseActivity: " + url);
        Log.d("djweb", "response- ShowcaseActivity: " + json);
        if (id == ProductsFragment.DES_COLL_ID_CALL) {
            if (prodFrag != null) {
                boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                        prodFrag.mCardStack, this);
                if (success) {
                    if (json == null)
                        return;
                    prodFrag.continueTry((String) json);
                } else {
                    String errMsg = "";
                    try {
                        errMsg = new JSONObject((String) json).getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        errMsg = Constants.ERR_MSG_1;
                    }
                    Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_SHORT).show();
                }
            }
        } else super.serverCallEnds(id, url, json, status);
    }

    public int getUserId() {
        return mUser.id;
    }

    public ExtendedAjaxCallback getAjaxCallBackCustom(int requestId) {
        return getAjaxCallback(requestId);
    }

    public AQuery getAQueryCustom() {
        return getAQuery();
    }

    private Dialog overLayDialog;

    private void showOverLay(String text, int colorResId) {
        if (overLayDialog == null) {
            overLayDialog = WindowUtils.getInstance(getApplicationContext()).displayOverlayLogo(this, text, colorResId,
                    WindowUtils.PROGRESS_FRAME_GRAVITY_TOP);
        }
        overLayDialog.show();
    }

    private void dismissOverLay() {
        if (overLayDialog != null) {
            if (overLayDialog.isShowing()) {
                overLayDialog.dismiss();
            }
        }
    }


    LinearLayoutManager recyclerLinLayManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase);

        showOverLay(null, R.color.Black);
        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: SHOWCASE");
        logEventsAnalytics(GAAnalyticsEventNames.SHOWCASE);

        drawerLayout.setBackgroundColor(Color.WHITE);
        mContext = this;
        mOverlayVH = new OverlayViewHolder(mBrandButtonsLayout);
        mTabViewHolder = new TabViewHolder(mContext, mTabLayout);

        final DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mStartHeight = (int) (.7f * dm.heightPixels);
        mCollapsedHeight = (int) (.28f * dm.heightPixels);

        mPagerDummy.getLayoutParams().height = mStartHeight;
        mRecyclerView.getLayoutParams().height = mStartHeight;
        mOverlay.getLayoutParams().height = mStartHeight;
        topLayout.getLayoutParams().height = mStartHeight;
        mToolBar.getLayoutParams().height = mCollapsedHeight;

        final int tabStart = mStartHeight - getResources().getDimensionPixelSize(
                R.dimen.tab_height) + getResources().getDimensionPixelSize(R.dimen.shadow_height);
        final int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, dm);
        final int maxPad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, dm);
        final int maxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, dm);

        recyclerLinLayManger = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setAdapter(mShowCaseAdapter = new ShowcasePagerAdapter(mContext, dm.widthPixels - 2 * pad, mStartHeight));
        mRecyclerView.setLayoutManager(recyclerLinLayManger);

        mFrame.animate().setDuration(0).y(mStartHeight);
        mTabLayout.animate().setDuration(0).y(tabStart);
        mTabViewHolder.initTabs(getString(R.string.collections), getString(R.string.products), getString(R.string.social), mTabClickListener);

        //setUpInterceptListener();
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mVerticalOffset != verticalOffset) {
                    Log.d(TAG, "offset : " + verticalOffset + "--" + mStartHeight + "---" + mCollapsedHeight);
                    boolean change = Math.abs(verticalOffset) <= .1f * mStartHeight;
                    int visibility = change ? View.VISIBLE : View.GONE;
                    topLayout.getLayoutParams().height = change ? mStartHeight : mCollapsedHeight;
                    mOverlayVH.setVisisbility(visibility);
                    mOverlay.getLayoutParams().height = mStartHeight + verticalOffset;
                    mRecyclerView.getLayoutParams().height = mStartHeight + verticalOffset;
                    int p = (int) (((maxPad * 0.25f * verticalOffset) / maxHeight) - pad);
                    mShowCaseAdapter.setDimens(dm.widthPixels + 2 * p,
                            mStartHeight + verticalOffset);
                    mRecyclerView.getLayoutManager().offsetChildrenHorizontal(-p);
                    mRecyclerView.setPadding(-p, 0, -p, 0);
                    mRecyclerView.scrollToPosition(mCurrentPosition);
                    mFrame.animate().setDuration(0).yBy(verticalOffset - mVerticalOffset);
                    mTabLayout.animate().setDuration(0).yBy(verticalOffset - mVerticalOffset);
                    mTabViewHolder.setSides(-p);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mVerticalOffset == 0) {
                                mTabLayout.animate().setDuration(0).y(tabStart);
                            }
                        }
                    }, 180);
                }
                mVerticalOffset = verticalOffset;
            }
        });


        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition--;
                if (mCurrentPosition < 0) mCurrentPosition = mShowCaseAdapter.getItemCount() - 1;
                mRecyclerView.smoothScrollToPosition(mCurrentPosition);
                mHandler.removeCallbacks(mUserChangeRunnable);
                mHandler.postDelayed(mUserChangeRunnable, 100);
                configureUI(mUIState);
                mTabViewHolder.setSelected(0);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition++;
                if (mCurrentPosition > mShowCaseAdapter.getItemCount() - 1) mCurrentPosition = 0;
                mRecyclerView.smoothScrollToPosition(mCurrentPosition);
                mHandler.removeCallbacks(mUserChangeRunnable);
                mHandler.postDelayed(mUserChangeRunnable, 100);
                configureUI(mUIState);
                mTabViewHolder.setSelected(0);
            }
        });

        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);


        //        mOverlayVH.itemView.setVisibility(View.INVISIBLE);
        configureUI(mUIState);
        UIController.getShowCase(mContext,
                new IResultListener<TimelineResponse>() {
                    @Override
                    public void onResult(TimelineResponse result) {
                        //mProgressFrame.setVisibility(View.GONE);
                        dismissOverLay();
                        Intent data = getIntent();
                        if (data != null) {
                            int userId = data.getIntExtra(IntentKeys.DESIGNER_ID, -1);
                            if (userId != -1) {
                                if (DbHelper.mapOfUserIds == null || DbHelper.mapOfUserIds.size() == 0){
                                    int position = offlinemapOfUser.get(userId);
                                    if (position != -1) smoothScrollToPosition(position);
                                }else {
                                    int position = DbHelper.mapOfUserIds.get(userId);
                                    if (position != -1) smoothScrollToPosition(position);
                                }
                            }
                        }

                        getSupportLoaderManager().restartLoader(mShowCaseCallback.hashCode(), null, mShowCaseCallback);
                    }
                });
        getSupportLoaderManager().initLoader(mShowCaseCallback.hashCode(), null, mShowCaseCallback);

        tourThisScreen();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int userId = intent.getIntExtra(IntentKeys.DESIGNER_ID, -1);
            if (userId != -1) {
                int position = DbHelper.mapOfUserIds.get(userId);
                if (position != -1) smoothScrollToPosition(position);
            }
        }
    }


    private void manupilateToggle(){
        mAppBarLayout.setExpanded(false);
        //mOverlayVH.brandName.setVisibility(View.GONE);
        mOverlayVH.setVisisbility(View.GONE);
        /*layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        mProductCollection.setVisibility(View.GONE);
        mProductCost.setVisibility(View.GONE);*/
        //mTabLayout.animate().setDuration(0).scaleY(0.8f).scaleX(.8f);
    }

    private void smoothScrollToPosition(int position) {
        mCurrentPosition = position;
        /*if (mCurrentPosition < 0) mCurrentPosition = mShowCaseAdapter.getItemCount() - 1;
        else if (mCurrentPosition > mShowCaseAdapter.getItemCount() - 1) mCurrentPosition = 0;*/
        mRecyclerView.scrollToPosition(mCurrentPosition);
        mHandler.removeCallbacks(mUserChangeRunnable);
        mHandler.postDelayed(mUserChangeRunnable, 100);
        configureUI(mUIState);
        mTabViewHolder.setSelected(0);
        //recyclerLinLayManger.scrollToPosition(position);
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        mAppBarLayout.onTouchEvent(motionEvent);
        mRecyclerView.onTouchEvent(motionEvent);
        mFrame.onTouchEvent(motionEvent);
        topLayout.onTouchEvent(motionEvent);
        mTabLayout.onTouchEvent(motionEvent);
        //mAppBarLayout.onInterceptTouchEvent(motionEvent);
        mCoordinatorLayout.onTouchEvent(motionEvent);
        if (mUIState == UISTATE_SOCIAL)
            return *//*super.dispatchTouchEvent(motionEvent)*//*false;
        else return super.dispatchTouchEvent(motionEvent);
    }*/


    //Author DJphy
    @Bind(R.id.transViewMain)
    View transViewMain;
    @Bind(R.id.transViewSwipeUp)
    View transViewSwipeUp;
    @Bind(R.id.transViewProds)
    View transViewProds;
    @Bind(R.id.transViewBAA)
    View transViewBAA;

    private AppTourGuideHelper mTourHelper;

    private void tourThisScreen() {

        /*resRdr = ResourceReader.getInstance(getApplicationContext());
        coachMarkMgr = DjphyPreferenceManager.getInstance(getApplicationContext());*/
        mTourHelper = AppTourGuideHelper.getInstance(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /*if (!coachMarkMgr.isHomeScreenTourDone())
                    testTourGuide();*/
                Log.d(Constants.TAG, "tour showcase");
                mTourHelper.displayShowcaseTour(ShowcaseActivity.this, new View[]{transViewMain, transViewSwipeUp, /*transViewProds*/
                        transViewBAA});
            }
        }, 1500);
    }


    TabViewHolder.TabClickListener mTabClickListener = new TabViewHolder.TabClickListener() {
        @Override
        public void onTabClick(int position) {
            configureUI(position);
        }
    };


    @Override
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(mShowCaseCallback.hashCode());
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        /*if (mUser != null) {
            Log.e("iii--Notnull--", "" + mUser.id);
            mUser = UserInfoCache.getInstance(mContext).getUserInfoDB(mUser.id, true);
            bindOverlay(mUser);
            if (isCollectionLike) {
                isCollectionLike = false;
                UIController.getShowCase(mContext,
                        new IResultListener<TimelineResponse>() {
                            @Override
                            public void onResult(TimelineResponse result) {
                                //mProgressFrame.setVisibility(View.GONE);
                                getSupportLoaderManager().restartLoader(mShowCaseCallback.hashCode(), null, mShowCaseCallback);
                            }
                        });
            }
        } else {
            Log.e("iii--null--", "");
        }*/
        super.onResume();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        //menu.findItem(R.id.nav_my_overflow).setVisible(false);
        return value;
    }

    @Override
    public void updateCollectionCount(int count) {
        Log.d("updateCount: ", count + "");
        mTabViewHolder.collectionCount(count);
    }

    @Override
    public void updateProductCounts(int count) {
        // mTabViewHolder.productsCount(count);
    }

    private void onUserChange(User user) {
        if (user != null && !user.equals(mUser)) {
            mUser = user;
            bindOverlay(user);
            String social = getString(R.string.social).toLowerCase();
            if (!TextUtils.isEmpty(user.name)) {
                social += "@";
                social += user.name.toLowerCase().replace(" ", "");
            }
            mTabViewHolder.tabName3.setText(social);
            for (UserChangeListener l : mUserChangeListeners)
                l.onUserChange(user);
        }
    }

    public void registerUserChangeListener(UserChangeListener listener) {
        if (!mUserChangeListeners.contains(listener)) mUserChangeListeners.add(listener);
    }

    public void unRegisterUserChangeListener(UserChangeListener listener) {
        mUserChangeListeners.remove(listener);
    }

    private Runnable mUserChangeRunnable = new Runnable() {
        @Override
        public void run() {
            onUserChange(mShowCaseAdapter.getUser(mCurrentPosition));
        }
    };

    ProductsFragment prodFrag;

    private void configureUI(int uiState) {
        Fragment f = null;
        int id = R.id.frame;
        mFrame.setVisibility(View.VISIBLE);
        mFrameScrollDummy.setVisibility(View.INVISIBLE);
        mFrameNoScrollDummy.setVisibility(View.INVISIBLE);
        if (uiState == UISTATE_SOCIAL) {
            //f = new SocialFeedFragment();
            manupilateToggle();
            FilterPostParams fpp = new FilterPostParams(String.valueOf(mUser.id), "0", "0");
            f = FilterTimelineFragment.newInstance(fpp);
            id = R.id.frame_no_scroll_dummy;
            mFrame.setVisibility(View.INVISIBLE);
            mFrameScrollDummy.setVisibility(View.INVISIBLE);
            mFrameNoScrollDummy.setVisibility(View.VISIBLE);
        } else if (uiState == UISTATE_PRODUCT) {
            startTourGuideForProduct();
            f = prodFrag = ProductsFragment.newInstance(ProductsFragment.MODE_USER, mUser, null);
        } else {
            f = CollectionsFragment.newInstance(mUser);
            id = R.id.frame_no_scroll_dummy;
            mFrame.setVisibility(View.INVISIBLE);
            mFrameScrollDummy.setVisibility(View.INVISIBLE);
            mFrameNoScrollDummy.setVisibility(View.VISIBLE);
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, f);
            fragmentTransaction.commit();
        }
    }

    private void startTourGuideForProduct() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTourHelper.displayProductShowcaseScreenTour(ShowcaseActivity.this, transViewProds);
            }
        }, 500);
    }


    private class ShowCaseCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        Cursor cursor;
        private boolean otpFlag = true;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(mContext, Users.CONTENT_URI, null, null,
                    null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (cursor != null) cursor.close();
            this.cursor = data;
            mShowCaseAdapter.changeCursor(data);
            Log.e("iiiii--", "enter");
            if (data.getCount() > 0) {
                if (otpFlag) {
                    otpFlag = false;
                    mHandler.post(mUserChangeRunnable);
                }
                mOverlayVH.itemView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }


    private void bindOverlay(User user) {
        mOverlayVH.brandName.setText(user.name);
        mOverlayVH.appointment_count.setText(String.format(Locale.getDefault(), "%d", user.numAppts));
        mOverlayVH.followersCount.setText(
                String.format(Locale.getDefault(), "%d", user.followers_cnt));
        mOverlayVH.followingCount.setText(
                String.format(Locale.getDefault(), "%d", user.following_cnt));

        mOverlayVH.followButton.setTag(user);
        mOverlayVH.btnBookAppoint.setTag(user);
        mTabViewHolder.setCounts(user.collections_cnt, user.products_cnt);
        mOverlayVH.setBadges(user.trending, user.featured);
        //mOverlayVH.btnBookAppoint.setSelected(user.isLiked);
        mOverlayVH.followButton.setSelected(user.isFollowed);

    }

    private SparseArray<Integer> offlinemapOfUser;
    private class ShowcasePagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final Context context;
        private int height;
        private int width;
        Cursor cursor = null;
        List<User> userList;
        boolean isOrderedSetAvailable = false;

        public ShowcasePagerAdapter(Context context, int width, int height) {
            this.context = context;
            this.width = width;
            this.height = height;
        }


        private void setResponseState(){
            boolean temp1 = DbHelper.mapOfUserIds == null ? false: true;
            boolean temp2 = false;
            if (temp1)
                temp2 = DbHelper.mapOfUserIds.size() == 0 ? false : true;

            isOrderedSetAvailable = temp1 && temp2 ? true: false;
        }

        public User getUser(int position) {
            if (isOrderedSetAvailable)
                return userList.get(position);
            else {
                if (cursor != null && cursor.moveToPosition(position)) {
                    return User.extractFromCursor(cursor);
                }
            }

            return null;
        }

        public void changeCursor(Cursor cursor) {
            setResponseState();
            //this.cursor = cursor;
            if (isOrderedSetAvailable)
                userList = getOrdereduser(cursor);
            else {
                this.cursor = cursor;
                setOfflinedataMap(cursor);
            }
            notifyDataSetChanged();
        }

        private void setOfflinedataMap(Cursor cursor) {
            offlinemapOfUser = new SparseArray<>();
            if (cursor != null){
                if (cursor.getCount() == 0)
                    return;
                int i = 0;
                if (cursor.moveToFirst()){
                    do {
                        offlinemapOfUser.put(User.getUserId(cursor), i);
                        i++;
                    }while (cursor.moveToNext());

                }
            }
        }

        private List<User> getOrdereduser(Cursor randomOrderedUser/*, int position*/) {
            if (randomOrderedUser != null) {
                if (randomOrderedUser.getCount() == 0)
                    return null;
                List<User> userListTemp = new ArrayList<>();
                for (int i =0 ; i< randomOrderedUser.getCount(); i++){
                    userListTemp.add(null);
                }
                //userListTemp.clear();
                Log.d("dj", "size of userListTemp: "+userListTemp.size());
                if (randomOrderedUser.moveToFirst()) {
                    do {
                        //int desId = DbHelper.mapOfUserIds.keyAt(position);
                        int cursorDesId = User.getUserId(randomOrderedUser);
                        int positionToAllot = DbHelper.mapOfUserIds.get(cursorDesId);
                        userListTemp.set(positionToAllot, User.extractFromCursor(randomOrderedUser));
                        /*if (desId == cursorDesId)
                            return User.extractFromCursor(cursor);*/
                    } while (randomOrderedUser.moveToNext());
                }
                return userListTemp;
            }
            return null;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = new RecyclerView.ViewHolder(
                    getLayoutInflater().inflate(R.layout.layout_image, parent, false)) {
            };
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            User user = getUser(position);
            ImageView image = (ImageView) holder.itemView.findViewById(R.id./*image*/coverImage);
            Log.d("dj","desImageUrl: "+user.imageUrl);
            if (!TextUtils.isEmpty(user.imageUrl)) {
                Picasso.with(mContext).load(user.imageUrl).into(image);
                image.getLayoutParams().width = this.width;
                image.getLayoutParams().height = this.height;
                image.requestLayout();
            }
        }

        @Override
        public int getItemCount() {
            if (isOrderedSetAvailable)
                return userList == null ? 0 : userList.size();
            else return cursor == null || cursor.isClosed() ? 0 : cursor.getCount();
        }

        public void setDimens(int width, int height) {
            this.width = width;
            this.height = height;
            notifyDataSetChanged();
        }


    }


    class OverlayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.brand_name)
        TextView brandName;
        //        @Bind(R.id.brand_description)
        //        TextView mBrandDescription;

        @Bind(R.id.layout_1)
        View layout1;
        @Bind(R.id.layout_2)
        View layout2;
        @Bind(R.id.layout_3)
        View layout3;

        @Bind(R.id.badge_1)
        ImageView featured;
        @Bind(R.id.badge_2)
        ImageView trending;

        @Bind(R.id.appointment_count)
        TextView appointment_count;
        @Bind(R.id.followers_count)
        TextView followersCount;
        @Bind(R.id.following_count)
        TextView followingCount;

        @Bind(R.id.btnBookApoint)
        IconicsButton btnBookAppoint;
        @Bind(R.id.followButton)
        ImageView followButton;
        @Bind(R.id.shareButton)
        ImageView shareButton;

        public OverlayViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            btnBookAppoint.setOnClickListener(this);
            shareButton.setOnClickListener(this);
            followButton.setOnClickListener(this);
        }

        public void setBadges(boolean trending, boolean featured) {
            this.featured.setVisibility(featured ? View.VISIBLE : View.GONE);
            this.trending.setVisibility(trending ? View.VISIBLE : View.GONE);
        }

        public void setVisisbility(int visibility) {
            layout1.setVisibility(visibility);
            layout2.setVisibility(visibility);
            layout3.setVisibility(visibility);
            ((LinearLayout) this.itemView).setGravity(visibility == View.GONE ? Gravity.TOP :
                    Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        }

        @Override
        public void onClick(final View v) {
            int id = v.getId();
            Context context = v.getContext();
            if (id == R.id.btnBookApoint) {
                //v.setEnabled(false);
                displayBookAppointment();
                /*final User user = (User) v.getTag();
                final boolean isLiked = v.isSelected();
                UIController.like(context, user, !isLiked, new IResultListener<LikeResponse>() {

                    @Override
                    public void onResult(LikeResponse result) {
                        v.setEnabled(true);
                        v.setSelected(result.success != isLiked);
                        if(isLiked){
                            user.likes_cnt=user.likes_cnt-1;
                            mOverlayVH.appointment_count.setText(String.format(Locale.getDefault(), "%d", user.likes_cnt));
                           // Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", user.likes_cnt))),Toast.LENGTH_SHORT).show();
                        }else{
                            user.likes_cnt=user.likes_cnt+1;
                            mOverlayVH.appointment_count.setText(String.format(Locale.getDefault(), "%d", user.likes_cnt));
                            //Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", user.likes_cnt))),Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
            } else if (id == R.id.followButton) {
                v.setEnabled(false);
                final User user = (User) v.getTag();
                final boolean isFollowing = v.isSelected();
                UIController.follow(context, user, !isFollowing, new IResultListener<LikeResponse>() {

                    @Override
                    public void onResult(LikeResponse result) {
                        v.setEnabled(true);
                        v.setSelected(result.success != isFollowing);
                        if (isFollowing) {
                            user.followers_cnt = user.followers_cnt - 1;
                            mOverlayVH.followersCount.setText(String.format(Locale.getDefault(), "%d", user.followers_cnt));
                            //Toast.makeText(getApplicationContext(), ((String.format(Locale.getDefault(), "%d", user.followers_cnt))), Toast.LENGTH_SHORT).show();
                        } else {
                            user.followers_cnt = user.followers_cnt + 1;
                            mOverlayVH.followersCount.setText(String.format(Locale.getDefault(), "%d", user.followers_cnt));
                            //Toast.makeText(getApplicationContext(), ((String.format(Locale.getDefault(), "%d", user.followers_cnt))), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (id == R.id.shareButton) {
                Toast.makeText(v.getContext(), "Feature Coming Soon!", Toast.LENGTH_SHORT).show();
                //// TODO: 30/3/16 share click
            }
        }
    }

}
