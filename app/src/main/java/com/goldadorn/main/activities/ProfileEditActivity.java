package com.goldadorn.main.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ObjectResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.Bind;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

/**
 * Created by Vijith Menon on 5/4/16.
 */
public class ProfileEditActivity extends BaseDrawerActivity {
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
    AppCompatSpinner mGender;
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
    AppCompatSpinner mCountry;
    @Bind(R.id.state)
    AppCompatSpinner mState;
    @Bind(R.id.city)
    AppCompatSpinner mCity;
    @Bind(R.id.zipcode)
    EditText mPincode;

    @Bind(R.id.doneButton)
    Button mDone;
    private File mImageFile;
    private ProfileData mProfileData;
    Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mCalendar.set(year,monthOfYear,dayOfMonth,0,0,0);
                    mDob.setText(android.text.format.DateFormat.format("dd/M/yyyy",mCalendar));
                }
            };
    private DatePickerDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        mContext = this;
        initData();
        setupDOB();

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileData.email = mEmail.getText().toString();
                mProfileData.firstName = mFirstName.getText().toString();
                mProfileData.lastName = mLastName.getText().toString();
                mProfileData.gender = mGender.getSelectedItem().toString();
                mProfileData.phone = mPhone.getText().toString();
                mProfileData.address1 = mAddress1.getText().toString();
                mProfileData.address2 = mAddress2.getText().toString();
                mProfileData.country = mCountry.getSelectedItem().toString();
                mProfileData.state = mState.getSelectedItem().toString();
                mProfileData.city = mCity.getSelectedItem().toString();
                mProfileData.pincode = mPincode.getText().toString();
                mProfileData.dob =mCalendar.getTimeInMillis();

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
                if (result.success) {
                    bindUI(result.object);
                }
            }
        });
    }

    private void bindUI(ProfileData profileData) {
        mProfileData = profileData;

    }

    private void setupDOB() {
        mDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDialog==null)
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
        if(mDialog!=null && mDialog.isShowing())
            mDialog.dismiss();
    }

    private void initData() {
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getDefault());
        mEmail.setText("");
        mFirstName.setText("");
        mLastName.setText("");
        mDob.setText("");
        mPhone.setText("");
        mAddress1.setText("");
        mAddress2.setText("");
        mPincode.setText("");


        mGender.setSelection(0);
        mCountry.setSelection(0);
        mState.setSelection(0);
        mCity.setSelection(0);

//        mImage.setImageDrawable(null);
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
