package com.goldadorn.main.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ObjectResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
                mProfileData.dob = mCalendar.getTimeInMillis();
                mProfileData.imageToUpload = mImageFile;

                UIController.setBasicProfileInfo(mContext, mProfileData, new IResultListener<ObjectResponse<ProfileData>>() {
                    @Override
                    public void onResult(ObjectResponse<ProfileData> result) {
                        Toast.makeText(mContext, result.success ? "Profile Updated" : "Someething went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void bindUI(ProfileData profileData) {
        mProfileData = profileData;
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getDefault());
        mCalendar.setTimeInMillis(mProfileData.dob);
        mEmail.setText(mProfileData.email);
        mFirstName.setText(mProfileData.firstName);
        mLastName.setText(mProfileData.lastName);
        mDob.setText(mFormat.format(mCalendar.getTime()));
        mPhone.setText(mProfileData.phone);
        mAddress1.setText(mProfileData.address1);
        mAddress2.setText(mProfileData.address2);
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
