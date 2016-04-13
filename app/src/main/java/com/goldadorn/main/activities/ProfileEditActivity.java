package com.goldadorn.main.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ObjectResponse;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.mixpanel.android.util.StringUtils;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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

    @Bind(R.id.address1)
    EditText mAddress1;
    @Bind(R.id.address2)
    EditText mAddress2;

    @Bind(R.id.country)
    Spinner mCountry;
    @Bind(R.id.state)
    Spinner mState;
    @Bind(R.id.city)
    Spinner mCity;
    @Bind(R.id.zipcode)
    EditText mPincode;

    @Bind(R.id.doneButton)
    Button mDone;

    @Bind(R.id.progress_frame)
    View mProgress;

    private File mImageFile;
    private ProfileData mProfileData;
    Calendar mCalendar;
    SimpleDateFormat mFormat;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                    mDob.setText(android.text.format.DateFormat.format("dd/M/yyyy", mCalendar));
                }
            };
    private DatePickerDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        mContext = this;
        ButterKnife.bind(this);
        mFormat = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
        setupDOB();
        mToolbar.setTitle("Profile");
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProfileData == null)
                    mProfileData = new ProfileData();
                mProfileData.email = mEmail.getText().toString();
                mProfileData.firstName = mFirstName.getText().toString();
                mProfileData.lastName = mLastName.getText().toString();
                mProfileData.genderType = mGender.getSelectedItemPosition();
                mProfileData.phone = mPhone.getText().toString();
                mProfileData.address1 = mAddress1.getText().toString();
                mProfileData.address2 = mAddress2.getText().toString();
                mProfileData.country = mCountry.getSelectedItem().toString();
                mProfileData.state = mState.getSelectedItem().toString();
                mProfileData.city = mCity.getSelectedItem().toString();
                mProfileData.pincode = mPincode.getText().toString();
                if (mCalendar != null)
                    mProfileData.dob = mCalendar.getTimeInMillis();
                mProfileData.imageToUpload = mImageFile;


                submitProfile(mProfileData);

/*
                UIController.setBasicProfileInfo(mContext, mProfileData, new IResultListener<ObjectResponse<ProfileData>>() {
                    @Override
                    public void onResult(ObjectResponse<ProfileData> result) {
                        Toast.makeText(mContext, result.success ? "Profile Updated" : "Someething went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
                */

            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(ProfileEditActivity.this, "Pick an image",
                        EasyImageConfig.REQ_SOURCE_CHOOSER);
            }
        });
        UIController.getBasicProfileInfo(mContext, new IResultListener<ObjectResponse<ProfileData>>() {
            @Override
            public void onResult(ObjectResponse<ProfileData> result) {
                mProgress.setVisibility(View.GONE);
                if (result.success) {
                    bindUI(result.object);
                }
            }
        });
    }

    final private int postCallToken = IDUtils.generateViewId();
    protected void submitProfile(ProfileData mProfileData)
    {
        MultipartEntity reqEntity = new MultipartEntity();


        if(mProfileData.imageToUpload!=null && mProfileData.imageToUpload.exists()  && mProfileData.imageToUpload.canRead())
            reqEntity.addPart("file1", new FileBody(mProfileData.imageToUpload));

        putStringBody(reqEntity,"prof_username",mProfileData.email);
        putStringBody(reqEntity,"prof_fname",mProfileData.firstName);
        putStringBody(reqEntity,"prof_gender",mProfileData.genderType+"");


        Map<String, Object> params = new HashMap<>();
        params.put(AQuery.POST_ENTITY, reqEntity);


        String url = getUrlHelper().getSetBasicProfileURL();
        ExtendedAjaxCallback ajaxCallback =getAjaxCallback(postCallToken);
        ajaxCallback.setClazz(String.class);
        ajaxCallback.setParams(params);
        ajaxCallback.method(AQuery.METHOD_POST);
        getAQuery().ajax(url, params, String.class, ajaxCallback);
    }
    public void serverCallEnds(int id,String url, Object json, AjaxStatus status) {
        if(id== postCallToken)
        {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, layoutParent, this);
            if(success)
            {
                Toast.makeText(ProfileEditActivity.this, "Profile has been updated", Toast.LENGTH_SHORT).show();
            }
            else {

            }

        }
        else
            super.serverCallEnds(id,url, json, status);
    }
    private void putStringBody(MultipartEntity reqEntity, String key, String value) {
        try {
            if(value!=null && value.equals("")==false)
                reqEntity.addPart(key, new StringBody(value));
        }catch (Exception e){

        }
    }

    private void bindUI(ProfileData profileData) {
        mProfileData = profileData;
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getDefault());
        mCalendar.setTimeInMillis(mProfileData.dob);

        if(!TextUtils.isEmpty(mProfileData.email))
            mEmail.setText(mProfileData.email);

        if(!TextUtils.isEmpty(mProfileData.firstName))
            mFirstName.setText(mProfileData.firstName);

        if(!TextUtils.isEmpty(mProfileData.lastName))
            mLastName.setText(mProfileData.lastName);

        mDob.setText(mFormat.format(mCalendar.getTime()));

        if(!TextUtils.isEmpty(mProfileData.phone))
            mPhone.setText(mProfileData.phone);

        if(!TextUtils.isEmpty(mProfileData.address1))
            mAddress1.setText(mProfileData.address1);

        if(!TextUtils.isEmpty(mProfileData.address2))
            mAddress2.setText(mProfileData.address2);

        if(!TextUtils.isEmpty(mProfileData.pincode))
            mPincode.setText(mProfileData.pincode);


        String[] array = getResources().getStringArray(R.array.gender);
        int selected = 0;
        for (int i = 0; i < array.length; i++) {
            if (i == mProfileData.genderType)
                selected = i;
        }
        mGender.setSelection(selected);

        array = getResources().getStringArray(R.array.country);
        selected = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(mProfileData.country))
                selected = i;
        }
        mCountry.setSelection(selected);

        array = getResources().getStringArray(R.array.states);
        selected = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(mProfileData.state))
                selected = i;
        }
        mState.setSelection(selected);

        array = getResources().getStringArray(R.array.cities);
        selected = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(mProfileData.city))
                selected = i;
        }
        mCity.setSelection(selected);

        Picasso.with(ProfileEditActivity.this).load(mProfileData.imageUrl).into(mImage);

        //        mImage.setImageDrawable(null);

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
                Picasso.with(ProfileEditActivity.this).load(mImageFile).into(mImage);

            }

        });
    }
}
