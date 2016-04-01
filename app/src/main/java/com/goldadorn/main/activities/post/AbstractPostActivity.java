package com.goldadorn.main.activities.post;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.model.People;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.ImageSelector;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import android.net.Uri;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import javax.xml.validation.TypeInfoProvider;


/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
abstract public class AbstractPostActivity extends BaseActivity implements ImageSelector.RegisterImageUploadCallBack {

    abstract protected int getmainResID();
    protected abstract int getPostType();
    abstract protected String isValid();
    abstract protected void viewCreted(People people,int maxImageSize);


    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    @Bind(R.id.postNow)
    View postNow;

    @OnClick(R.id.postNow)
    void onPostNow() {
        String valid=isValid();
        if (valid==null) {
            String fileData = null;
            if(getFilesPath()!=null)
                fileData = getFilesPath().toString();

            String msg = details.getText().toString().trim();
            int type = getPostType();
            Intent intent = new Intent();

            if(fileData!=null && fileData.equals("")==false)
                intent.putExtra("fileData", fileData);

            List<File> fileList=getFiles();
            if(fileList!=null && fileList.size()!=0)
            {
                File[] files = new File[fileList.size()];
                String[] uris = new String[fileList.size()];
                for (int i = 0; i < fileList.size(); i++) {
                    files[i]= fileList.get(i);
                    uris[i]= Uri.fromFile(fileList.get(i)).getPath();
                }
                intent.putExtra("files",files);
                intent.putExtra("filesURIs",uris);
            }

            List<String> linksList=getLinks();
            if(linksList!=null && linksList.size()!=0)
            {
                String[] links = new String[linksList.size()];
                for (int i = 0; i < linksList.size(); i++) {
                    links[i]= linksList.get(i);
                }
                intent.putExtra("links",links);
            }

            intent.putExtra("type", type);
            intent.putExtra("msg", msg);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else {
            final Snackbar snackbar = Snackbar.make(layoutParent, valid, Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
        }
    }
    protected abstract List<File> getFiles();
    protected abstract List<String> getFilesPath();
    protected abstract List<String> getLinks();
    @Bind(R.id.details)
    EditText details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getmainResID());
        ButterKnife.bind(this);
        TypefaceHelper.setFont(details,postNow);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean backEnabled = getIntent().getExtras().getBoolean("backEnabled", true);
        if(backEnabled)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_icon_cross_white);
        }


        String name = getIntent().getExtras().getString("NAME");
        int followerCount = getIntent().getExtras().getInt("FOLLOWER_COUNT");
        int followingCount = getIntent().getExtras().getInt("FOLLOWING_COUNT");
        String profilePic = getIntent().getExtras().getString("PROFILE_PIC");
        int isDesigner = getIntent().getExtras().getInt("IS_DESIGNER");
        Boolean isSelf = getIntent().getExtras().getBoolean("IS_SELF");


        People people = new People();
        people.setUserName(name);
        people.setProfilePic(profilePic);
        people.setFollowingCount(followingCount);
        people.setFollowerCount(followerCount);
        people.setIsSelf(isSelf);
        people.setIsDesigner(isDesigner);


        int maxImageSize = getResources().getInteger(R.integer.maxUploadSize);
        viewCreted(people,maxImageSize);
        setTitle(getPageTitle());
    }

    protected abstract String getPageTitle();


    @Override
    public void onBackPressed() {
        final Snackbar snackbar = Snackbar.make(layoutParent, "Are you sure you want to exit without posting", Snackbar.LENGTH_LONG);
        snackbar.setAction("Yes", new View.OnClickListener() {
            public void onClick(View v) {
                snackbar.dismiss();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        ColoredSnackbar.warning(snackbar).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    ImageSelector imageSelector;
    public void registerImageUploadCallBack(ImageSelector imageSelector)
    {
        this.imageSelector = imageSelector;
    }
    public void unRegisterImageUploadCallBack(ImageSelector imageSelector)
    {
        if(imageSelector ==this.imageSelector)
            this.imageSelector = null;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(imageSelector !=null)
            imageSelector.onActivityResult(requestCode,resultCode,data);
    }
}


