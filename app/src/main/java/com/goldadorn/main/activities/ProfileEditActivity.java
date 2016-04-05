package com.goldadorn.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

/**
 * Created by Vijith Menon on 5/4/16.
 */
public class ProfileEditActivity extends BaseDrawerActivity{
    private final static String TAG = ProfileEditActivity.class.getSimpleName();
    private final static boolean DEBUG = true;

    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.first_name)
    EditText firstName;
    @Bind(R.id.last_name)
    EditText lastName;
    @Bind(R.id.gender)
    AppCompatSpinner gender;
    @Bind(R.id.dob)
    EditText dob;
    @Bind(R.id.phone)
    EditText phone;
    @Bind(R.id.image)
    ImageView image;

    @Bind(R.id.address1)
    EditText address1;
    @Bind(R.id.address2)
    EditText address2;

    @Bind(R.id.country)
    AppCompatSpinner country;
    @Bind(R.id.state)
    AppCompatSpinner state;
    @Bind(R.id.city)
    AppCompatSpinner city;
    @Bind(R.id.zipcode)
    EditText zipcode;

    @Bind(R.id.doneButton)
    Button done;
    private File mImageFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        initData();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ProfileEditActivity.this.email.getText().toString();
                String firstName = ProfileEditActivity.this.firstName.getText().toString();
                String lastName = ProfileEditActivity.this.lastName.getText().toString();
                String gender = ProfileEditActivity.this.gender.getSelectedItem().toString();
                String dob = ProfileEditActivity.this.dob.getText().toString();
                String phone = ProfileEditActivity.this.phone.getText().toString();
                String address1 = ProfileEditActivity.this.address1.getText().toString();
                String address2 = ProfileEditActivity.this.address2.getText().toString();
                String country = ProfileEditActivity.this.country.getSelectedItem().toString();
                String state = ProfileEditActivity.this.state.getSelectedItem().toString();
                String city = ProfileEditActivity.this.city.getSelectedItem().toString();
                String zipcode = ProfileEditActivity.this.zipcode.getText().toString();

                //todo call update
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(ProfileEditActivity.this,"Pick an image",
                        EasyImageConfig.REQ_SOURCE_CHOOSER);
            }
        });
    }

    private void initData() {
        email.setText("");
        firstName.setText("");
        lastName.setText("");
        dob.setText("");
        phone.setText("");
        address1.setText("");
        address2.setText("");
        zipcode.setText("");


        gender.setSelection(0);
        country.setSelection(0);
        state.setSelection(0);
        city.setSelection(0);

//        image.setImageDrawable(null);
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
                //Handle the image
                mImageFile = imageFile;
                Picasso.with(ProfileEditActivity.this).load(mImageFile).into(image);
            }

        });
    }
}
