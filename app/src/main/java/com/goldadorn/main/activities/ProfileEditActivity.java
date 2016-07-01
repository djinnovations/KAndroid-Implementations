package com.goldadorn.main.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.support.SocialLoginUtil;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.ConnectionDetector;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ObjectResponse;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.rey.material.widget.ProgressView;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AddressComponent;
import com.seatgeek.placesautocomplete.model.AddressComponentType;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;
import com.squareup.picasso.Picasso;
import com.vlonjatg.progressactivity.ProgressActivity;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

/**
 * Created by Vijith Menon on 5/4/16.
 */
public class ProfileEditActivity extends BaseActivity {
    private final static String TAG = ProfileEditActivity.class.getSimpleName();
    private final static boolean DEBUG = true;
    private Context mContext;

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    @Bind(R.id.email)
    EditText mEmail;
    @Bind(R.id.first_name)
    EditText mFirstName;
    @Bind(R.id.last_name)
    EditText mLastName;
    @Bind(R.id.gender)
    Spinner mGender;
    @Bind(R.id.dob)
    TextView mDob;
    @Bind(R.id.phone)
    EditText mPhone;
    @Bind(R.id.image)
    ImageView mImage;

    @Bind(R.id.places_autocomplete)
    PlacesAutocompleteTextView places_autocomplete;
    @Bind(R.id.address2)
    EditText mAddress2;

    @Bind(R.id.country)
    Spinner mCountry;
    @Bind(R.id.state)
    EditText mState;
    @Bind(R.id.city)
    EditText mCity;
    @Bind(R.id.zipcode)
    EditText mPincode;

    @Bind(R.id.titleAddress)
    TextView titleAddress;

    @Bind(R.id.titleBasic)
    TextView titleBasic;


    @Bind(R.id.doneButton)
    Button mDone;

    @Bind(R.id.ivLockEP)
    ImageView ivLockEP;
    @Bind(R.id.ivNextPage)
    ImageView ivNextPage;
    @Bind(R.id.rlEditPassword)
    View rlEditPassword;
    @Bind(R.id.layPasswordSet)
    View layPasswordSet;

    @Bind(R.id.etCurrentPassword)
    EditText etCurrentPassword;
    @Bind(R.id.etNewPassword)
    EditText etNewPassword;
    @Bind(R.id.etRetypeNewPassword)
    EditText etRetypeNewPassword;
    @Bind(R.id.scrollLayEP)
    View scrollLayEP;
    @Bind(R.id.llPassword)
    View llPassword;
    @Bind(R.id.tvResetPassword)
    View tvResetPassword;

    /*@Bind(R.id.progress_frame)
    View mProgress;*/

    private File mImageFile;
    private ProfileData mProfileData;
    Calendar mCalendar;
    SimpleDateFormat mFormat;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.labelChangePassword)
    TextView labelChangePassword;

    String oldData;
    String newData;

    private String getData(){
        StringBuilder sb = new StringBuilder();
        sb.append(mEmail.getText().toString().trim())
                .append(mFirstName.getText().toString().trim())
                .append(mLastName.getText().toString().trim())
                .append((String) mGender.getSelectedItem())
                .append(mDob.getText().toString().trim())
                .append(mPhone.getText().toString().trim())
                .append(places_autocomplete.getText().toString().trim())
                .append(mAddress2.getText().toString().trim())
                .append(mState.getText().toString().trim())
                .append(mCity.getText().toString().trim())
                .append(mPincode.getText().toString().trim());
        return sb.toString();
    }


    public void setupStyle() {
        View[] viewList = new View[20];
        viewList[0] = mEmail;
        viewList[1] = mFirstName;
        viewList[2] = mLastName;
        viewList[3] = mGender;
        viewList[4] = mDob;
        viewList[5] = mPhone;
        viewList[6] = places_autocomplete;
        viewList[7] = mAddress2;
        viewList[8] = mCountry;
        viewList[9] = mState;
        viewList[10] = mCity;
        viewList[11] = mPincode;
        viewList[12] = titleBasic;
        viewList[13] = titleAddress;
        viewList[14] = titlePassword;
        viewList[15] = etCurrentPassword;
        viewList[16] = etNewPassword;
        viewList[17] = etRetypeNewPassword;
        viewList[18] = tvResetPassword;
        viewList[19] = labelChangePassword;

        TypefaceHelper.setFont(viewList);


        SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_list_item_1, Arrays.asList(getResources().getStringArray(R.array.gender)));
        mGender.setAdapter(adapter);


        adapter = new SpinnerAdapter(this, android.R.layout.simple_list_item_1, Arrays.asList(getResources().getStringArray(R.array.country)));
        mCountry.setAdapter(adapter);

        /*adapter = new SpinnerAdapter(this,android.R.layout.simple_list_item_1,Arrays.asList(getResources().getStringArray(R.array.states)));
        mState.setAdapter(adapter);

        adapter = new SpinnerAdapter(this,android.R.layout.simple_list_item_1,Arrays.asList(getResources().getStringArray(R.array.cities)));
        mCity.setAdapter(adapter);*/
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                    mDob.setText(android.text.format.DateFormat.format("MM/dd/yyyy", mCalendar));
                }
            };
    private DatePickerDialog mDialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //finish();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    ResourceReader rsRdr;
    @Bind(R.id.progressActivity)
    ProgressActivity pga;
    @Bind(R.id.titlePassword)
    TextView titlePassword;
    @Bind(R.id.progressBarScreen1)
    ProgressView progressBarScreen1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        rsRdr = ResourceReader.getInstance(getApplicationContext());
        mContext = this;

        ButterKnife.bind(this);
        pga.showLoading();

        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: PROFILE");
        logEventsAnalytics(GAAnalyticsEventNames.PROFILE);

        mFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profile");
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverLayScreen1(null, 0);
                try {
                    if (mProfileData == null)
                        mProfileData = new ProfileData();
                    mProfileData.email = mEmail.getText().toString().trim();
                    mProfileData.firstName = mFirstName.getText().toString().trim();
                    mProfileData.lastName = mLastName.getText().toString().trim();
                    mProfileData.genderType = mGender.getSelectedItemPosition();
                    mProfileData.phone = mPhone.getText().toString().trim();
                    mProfileData.address1 = addrLine1;
                    mProfileData.address2 = mAddress2.getText().toString().trim();
                    mProfileData.country = mCountry.getSelectedItem().toString().trim();
                    mProfileData.state = mState.getText().toString().trim();
                    mProfileData.city = mCity.getText().toString().trim();
                    mProfileData.pincode = mPincode.getText().toString().trim();
                    if (mCalendar != null)
                        mProfileData.dob = mCalendar.getTimeInMillis();
                    mProfileData.imageToUpload = mImageFile;

                    submitProfile(mProfileData);
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissOverLayScreen1();
                }

            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(ProfileEditActivity.this, "Pick an image",
                        EasyImageConfig.REQ_SOURCE_CHOOSER);
            }
        });

        setDrawablesForPassword();
        setOnClickListeners();
        setupStyle();
        if (ConnectionDetector.getInstance(getApplicationContext()).isNetworkAvailable()) {
            setUpCalendarAndBindUi();
        } else showNetworkErr();
    }


    private View.OnClickListener retryClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("dj", "network stat: " + ConnectionDetector.getInstance(getApplicationContext()).isNetworkAvailable());
            pga.showLoading();
            if (!ConnectionDetector.getInstance(getApplicationContext()).isNetworkAvailable()) {
                showNetworkErr();
                return;
            }
            setUpCalendarAndBindUi();
        }
    };


    private void showNetworkErr() {
        pga.showError(rsRdr.getDrawableFromResId(R.drawable._vector_icon_internet_connection),
                null, "No network connection", "Retry", retryClick);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            pga.setBackground(new ColorDrawable(rsRdr.getColorFromResource(R.color.disabled_grey)));
        } else
            pga.setBackgroundDrawable(new ColorDrawable(rsRdr.getColorFromResource(R.color.disabled_grey)));
    }


    private void setUpCalendarAndBindUi() {

        setUpAutoComplete();
        setupDOB();
        UIController.getBasicProfileInfo(mContext, new IResultListener<ObjectResponse<ProfileData>>() {
            @Override
            public void onResult(ObjectResponse<ProfileData> result) {
                //mProgress.setVisibility(View.GONE);
                if (result.success) {
                    bindUI(result.object);
                    oldData = getData();
                }
            }
        });
    }


    private void setOnClickListeners() {
        if (Application.getInstance().getPrefManager().getSocialLoginStat()){
            layPasswordSet.setVisibility(View.GONE);
            return;
        }
        mPasswordInput.add(etCurrentPassword);
        mPasswordInput.add(etNewPassword);
        mPasswordInput.add(etRetypeNewPassword);
        rlEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bringUpPasswordScreen();
            }
        });

        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canProceed()) {
                    Map<String, String> params = new HashMap<>();
                    params.put("oldpassword", etCurrentPassword.getText().toString().trim());
                    params.put("newpassword", etNewPassword.getText().toString().trim());
                    params.put("confirmnewpassword", etRetypeNewPassword.getText().toString().trim());
                    postChangePasswordToServer(params);
                }
            }
        });
    }

    private boolean canProceed() {
        for (EditText et : mPasswordInput) {
            if (TextUtils.isEmpty(et.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "fields are empty", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    public void clearForm() {
        for (EditText et : mPasswordInput) {
            et.setText("");
        }
    }

    @Bind(R.id.progressBar)
    ProgressView pgView;

    private void showOverLay() {
        tvResetPassword.setEnabled(false);
        tvResetPassword.setClickable(false);
        pgView.setVisibility(View.VISIBLE);
    }

    private void dismissOverLay() {
        tvResetPassword.setEnabled(true);
        tvResetPassword.setClickable(true);
        pgView.setVisibility(View.GONE);
    }

    private Dialog overLayDialog;

    private void showOverLayScreen1(String text, int colorResId) {
        if (overLayDialog == null) {
            // WindowUtils.marginForProgressViewInGrid = 6;
            WindowUtils.justPlainOverLay = true;
            progressBarScreen1.setVisibility(View.VISIBLE);
            overLayDialog = WindowUtils.getInstance(getApplicationContext()).displayOverlay(this, text, colorResId,
                    WindowUtils.PROGRESS_FRAME_GRAVITY_BOTTOM);
        }
        overLayDialog.show();
    }

    private void dismissOverLayScreen1() {
        if (overLayDialog != null) {
            //WindowUtils.marginForProgressViewInGrid = 5;
            if (overLayDialog.isShowing()) {
                WindowUtils.justPlainOverLay = false;
                progressBarScreen1.setVisibility(View.INVISIBLE);
                overLayDialog.dismiss();
            }
        }
    }


    private void bringUpPasswordScreen() {
        setTitle("Change Password");
        try {
            UiRandomUtils.startAnim(scrollLayEP, R.anim.slide_out_into_left);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollLayEP.setAlpha(0);
                scrollLayEP.setVisibility(View.GONE);
                llPassword.setVisibility(View.VISIBLE);
                try {
                    UiRandomUtils.startAnim(llPassword, R.anim.slide_in_from_right);
                    llPassword.setAlpha(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    private final int CHANGE_PASSWORD_CALL = IDUtils.generateViewId();

    private void postChangePasswordToServer(Map<String, String> params) {
        showOverLay();
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(CHANGE_PASSWORD_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        getAQuery().ajax(ApiKeys.getChangePasswordAPI(), params, String.class, ajaxCallback);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (llPassword.getVisibility() == View.VISIBLE) {
            bringUpEditProfileScreen();
        } else askBeforeExit();
    }

    List<EditText> mPasswordInput = new ArrayList<>();

    private void bringUpEditProfileScreen() {
        clearForm();
        setTitle("Profile");
        try {
            UiRandomUtils.startAnim(llPassword, R.anim.slide_out_into_right);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                llPassword.setAlpha(0);
                llPassword.setVisibility(View.GONE);
                scrollLayEP.setVisibility(View.VISIBLE);
                try {
                    UiRandomUtils.startAnim(scrollLayEP, R.anim.slide_in_from_left);
                    scrollLayEP.setAlpha(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    //Press the DONE button below to save changes
    private void askBeforeExit() {
        newData = getData();
        if (oldData.length() != newData.length() || isProfileImageChanged) {
            final Snackbar snackbar = Snackbar.make(layoutParent, /*"Profile changes will be lost, if not saved"*/
                    "Press DONE to save changes", Snackbar.LENGTH_LONG);
            snackbar.setAction("Exit", new View.OnClickListener() {
                public void onClick(View v) {
                    snackbar.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 200);
                }
            });
            ColoredSnackbar.warning(snackbar).show();
        }
        else finish();
    }

    private void setDrawablesForPassword() {
        ResourceReader rsrdr = ResourceReader.getInstance(getApplicationContext());
        Drawable drawablelock = rsrdr.getDrawableFromResId(R.drawable.ic_lock);
        drawablelock.setColorFilter(rsrdr.getColorFromResource(com.kimeeo.library.R.color._emptyViewMessageColor),
                PorterDuff.Mode.SRC_ATOP);
        Drawable drawableNext = rsrdr.getDrawableFromResId(R.drawable.ic_next_page);
        drawableNext.setColorFilter(rsrdr.getColorFromResource(com.kimeeo.library.R.color._emptyViewMessageColor),
                PorterDuff.Mode.SRC_ATOP);
        ivLockEP.setImageDrawable(drawablelock);
        ivNextPage.setImageDrawable(drawableNext);
    }


    private String addrLine1;
    DetailsCallback placeDetailsCallBack = new DetailsCallback() {
        @Override
        public void onSuccess(PlaceDetails placeDetails) {
            Log.d("djplace", "complete address: " + places_autocomplete.getText());
            String addLine2 = getSecondLineAddr(places_autocomplete.getText().toString().trim());
            addrLine1 = getAddrLine1(places_autocomplete.getText().toString().trim());
            mAddress2.setText(addLine2 == null ? "" : addLine2);
            places_autocomplete.setSelection(0);
            for (AddressComponent component : placeDetails.address_components) {
                for (AddressComponentType type : component.types) {
                    switch (type) {
                        case STREET_NUMBER:
                            break;
                        case ROUTE:
                            /*places_autocomplete.setText(component.long_name);
                            places_autocomplete.setSelection(places_autocomplete.getText().length());*/
                            break;
                        case NEIGHBORHOOD:
                            break;
                        case SUBLOCALITY_LEVEL_1:
                            break;
                        case SUBLOCALITY:
                            break;
                        case LOCALITY:
                            mCity.setText(component.long_name);
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_1:
                            mState.setText(component.long_name);
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_2:
                            break;
                        case COUNTRY:
                            mCountry.setSelection(0, false);
                            break;
                        case POSTAL_CODE:
                            Log.d("djplace", "pin: " + component.long_name);
                            mPincode.setText(component.long_name);
                            break;
                        case POLITICAL:
                            break;
                    }
                }
            }

        }

        @Override
        public void onFailure(Throwable throwable) {

        }
    };


    public String getAddrLine1(String fullAddr) {

        String[] addrArr = fullAddr.split(",");
        return addrArr[0];
    }

    private String getSecondLineAddr(String fullAddr) {

        String[] addrArr = fullAddr.split(",");
        if (addrArr.length > 4) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < addrArr.length - 3; i++) {
                sb.append(addrArr[i] + ",");
            }
            sb = sb.deleteCharAt(sb.length() - 1);
            return sb.toString().trim();
        }
        return null;
    }


    private void setUpAutoComplete() {
        places_autocomplete.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place
                        places_autocomplete.getDetailsFor(place, placeDetailsCallBack);
                    }
                }
        );

    }

    final private int postCallToken = IDUtils.generateViewId();


    protected void submitProfile(ProfileData mProfileData) {
        MultipartEntity reqEntity = new MultipartEntity();


        if (mProfileData.imageToUpload != null && mProfileData.imageToUpload.exists() && mProfileData.imageToUpload.canRead())
            reqEntity.addPart("file1", new FileBody(mProfileData.imageToUpload));

        int genderCode = mProfileData.genderType == 1 ? 2 : 1;
        String genderText = genderCode == 2 ? "Male" : "Female";

        String bday = mDob.getText().toString().trim();
        putStringBody(reqEntity, "prof_username", mProfileData.email);
        putStringBody(reqEntity, "prof_fname", mProfileData.firstName);
        putStringBody(reqEntity, "prof_gender", genderText);
        putStringBody(reqEntity, "prof_address1", mProfileData.address1);
        putStringBody(reqEntity, "prof_address2", mProfileData.address2);
        putStringBody(reqEntity, "prof_lname", mProfileData.lastName);
        putStringBody(reqEntity, "prof_birthday", bday);
        putStringBody(reqEntity, "prof_phone", mProfileData.phone);
        putStringBody(reqEntity, "prof_pincode", mProfileData.pincode);
        putStringBody(reqEntity, "prof_city", mProfileData.city);
        putStringBody(reqEntity, "prof_state", mProfileData.state);
        putStringBody(reqEntity, "prof_country", mProfileData.country);


        Map<String, Object> params = new HashMap<>();
        params.put(AQuery.POST_ENTITY, reqEntity);


        String url = getUrlHelper().getSetBasicProfileURL();
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(postCallToken);
        ajaxCallback.setClazz(String.class);
        ajaxCallback.setParams(params);
        ajaxCallback.method(AQuery.METHOD_POST);
        Log.d("djplace", "updateProfile reqJson: " + reqEntity);
        getAQuery().ajax(url, params, String.class, ajaxCallback);
    }

    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        Log.d("djweb", "url queried- ProfileEditActivity: " + url);
        Log.d("djweb", "response- ProfileEditActivity: " + json);
        isProfileImageChanged = false;
        if (id == postCallToken) {
            dismissOverLayScreen1();
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, layoutParent, this);
            if (success) {
                oldData = getData();
                Toast.makeText(ProfileEditActivity.this, "Profile has been updated", Toast.LENGTH_SHORT).show();
            } else {

            }

        } else if (id == CHANGE_PASSWORD_CALL) {
            dismissOverLay();
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mImage, this);
            if (success) {
                if (json == null)
                    return;
                Toast.makeText(ProfileEditActivity.this, "Password has been updated", Toast.LENGTH_SHORT).show();
                bringUpEditProfileScreen();

            } else {
                String errMsg = "";
                try {
                    errMsg = new JSONObject((String) json).getString("msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                    errMsg = /*Constants.ERR_MSG_1*/ "Password could not be updated, try again";
                }
                failedInfo(errMsg);
            }

        } else
            super.serverCallEnds(id, url, json, status);
    }

    private void failedInfo(String msg) {
        final Snackbar snackbar = Snackbar.make(mImage, msg, Snackbar.LENGTH_SHORT);
        ColoredSnackbar.alert(snackbar).show();
    }

    private void putStringBody(MultipartEntity reqEntity, String key, String value) {
        try {
            if (value != null)
                reqEntity.addPart(key, new StringBody(value));
        } catch (Exception e) {

        }
    }


    private void bindUI(ProfileData profileData) {
        mProfileData = profileData;
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getDefault());
        mCalendar.setTimeInMillis(mProfileData.dob == 0 ? System.currentTimeMillis() : mProfileData.dob);

        if (!TextUtils.isEmpty(mProfileData.email))
            mEmail.setText(mProfileData.email);

        if (!TextUtils.isEmpty(mProfileData.firstName))
            mFirstName.setText(mProfileData.firstName);

        if (!TextUtils.isEmpty(mProfileData.lastName))
            mLastName.setText(mProfileData.lastName);

        mDob.setText(mFormat.format(mCalendar.getTime()));

        if (!TextUtils.isEmpty(mProfileData.phone))
            mPhone.setText(mProfileData.phone);

        if (!TextUtils.isEmpty(mProfileData.address1))
            places_autocomplete.setText(mProfileData.address1);

        if (!TextUtils.isEmpty(mProfileData.address2))
            mAddress2.setText(mProfileData.address2);

        if (!TextUtils.isEmpty(mProfileData.pincode))
            mPincode.setText(mProfileData.pincode);

        if (!TextUtils.isEmpty(mProfileData.city))
            mCity.setText(mProfileData.city);
        if (!TextUtils.isEmpty(mProfileData.state))
            mState.setText(mProfileData.state);

        mGender.setSelection(mProfileData.genderType == 2 ? 1 : 0);
        mCountry.setSelection(0);


        Log.d(Constants.TAG, "profilePic url - bindUI()-ProfileEditActivity: " + mProfileData.imageUrl);
        try {
            if (!TextUtils.isEmpty(mProfileData.imageUrl.trim()))
                Picasso.with(this).load(mProfileData.imageUrl.trim()).placeholder(R.drawable
                        .vector_image_place_holder_profile_dark).into(mImage);
            else Log.d(Constants.TAG, "no profile pic loaded bindUI()-ProfileEditActivity: ");
        } catch (Exception ex) {
            Picasso.with(this).load(R.drawable
                    .vector_image_place_holder_profile_dark).into(mImage);
        }
        pga.showContent();
    }

    private void setupDOB() {
        mDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog == null)
                    mDialog = new DatePickerDialog(ProfileEditActivity.this,
                            mDateSetListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH));
                else
                    mDialog.updateDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH));
                mDialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    private boolean isProfileImageChanged = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(final File imageFile, EasyImage.ImageSource source, int type) {
                //Handle the mImage
                mImageFile = imageFile;
                isProfileImageChanged = true;
                Picasso.with(ProfileEditActivity.this).load(mImageFile).into(mImage);
            }

        });
    }

    private static class SpinnerAdapter extends ArrayAdapter<String> {
        private SpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);

            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            view.setPadding(0, 0, 0, 0);
            TypefaceHelper.setFont(view);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            //view.setPadding(0,0,0,0);
            TypefaceHelper.setFont(view);
            return view;
        }
    }
}
