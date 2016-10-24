package com.goldadorn.main.activities.post;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.FilterProductListing;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.utils.GalleryImageSelector;
import com.goldadorn.main.utils.ImageSelector;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.views.ColoredSnackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
abstract public class AbstractPostActivity extends BaseActivity implements ImageSelector.RegisterImageUploadCallBack {

    abstract protected int getmainResID();

    protected abstract int getPostType();

    abstract protected String isValid();

    /*abstract protected List<Integer> getCollIds();

    abstract protected List<Integer> getDesignerIds();*/

    abstract protected List<String> getClubbingData();

    abstract protected List<String> getProdTypes();

    abstract protected void viewCreted(People people, int maxImageSize);


    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    @Bind(R.id.postNow)
    View postNow;

    protected abstract List<File> getFiles();

    protected abstract List<String> getFilesPath();

    protected abstract List<Integer> getPrice();

    protected abstract List<String> getLinks();

    @Bind(R.id.details)
    EditText details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getmainResID());
        ButterKnife.bind(this);
        TypefaceHelper.setFont(details, postNow);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean backEnabled = getIntent().getExtras().getBoolean("backEnabled", true);
        if (backEnabled) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_icon_cross_brown);
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
        viewCreted(people, maxImageSize);
        setTitle(getPageTitle());

        postNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNow();
            }
        });
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

    protected void postNow() {
        String valid = isValid();
        if (valid == null) {
            String fileData = null;
            if (getFilesPath() != null)
                fileData = getFilesPath().toString();

            String msg = details.getText().toString().trim();
            int type = getPostType();
            Intent intent = new Intent();

            if (fileData != null && fileData.equals("") == false)
                intent.putExtra("fileData", fileData);

            /*List<Integer> price = getPrice();
            intent.putExtra("price", price.toArray());*/
            /*List<Integer> collIdList = getCollIds();
            intent.putExtra("collIdList", collIdList.toArray());
            List<Integer> desIdList = getDesignerIds();
            intent.putExtra("desIdList", desIdList.toArray());*/
            List<String> prodColDesIdPrice = getClubbingData();
            if (prodColDesIdPrice != null) {
                if (prodColDesIdPrice.size() != 0) {
                    String[] clubbedArr = new String[prodColDesIdPrice.size()];
                    int index = 0;
                    for (String s : prodColDesIdPrice) {
                        clubbedArr[index] = s;
                        index++;
                    }
                    intent.putExtra("clubbed", clubbedArr);
                }
            }

            List<File> fileList = getFiles();
            if (fileList != null && fileList.size() != 0) {
                File[] files = new File[fileList.size()];
                String[] uris = new String[fileList.size()];
                for (int i = 0; i < fileList.size(); i++) {
                    files[i] = fileList.get(i);
                    uris[i] = Uri.fromFile(fileList.get(i)).getPath();
                }
                intent.putExtra("files", files);
                intent.putExtra("filesURIs", uris);
            }

            List<String> linksList = getLinks();
            if (linksList != null && linksList.size() != 0) {
                String[] links = new String[linksList.size()];
                for (int i = 0; i < linksList.size(); i++) {
                    links[i] = linksList.get(i);
                }
                intent.putExtra("links", links);
            }

            intent.putExtra("type", type);
            intent.putExtra("msg", msg);
            ArrayList<String> tags = new ArrayList<>();
            tags.addAll(RandomUtils.getHashTagStrings(msg.trim()));
            List<String> prodtypelist = getProdTypes();
            if (prodtypelist != null) {
                prodtypelist.removeAll(Collections.singleton(null));
                for (String str : prodtypelist) {
                    String toadd = str.toLowerCase();
                    if (!tags.contains(toadd))
                        tags.add(str);
                }
            }
            intent.putStringArrayListExtra("hashtags", tags);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            final Snackbar snackbar = Snackbar.make(layoutParent, valid, Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
        }
    }

    public void registerImageUploadCallBack(ImageSelector imageSelector) {
        this.imageSelector = imageSelector;
    }

    public void unRegisterImageUploadCallBack(ImageSelector imageSelector) {
        if (imageSelector == this.imageSelector)
            this.imageSelector = null;
    }

    GalleryImageSelector ga1;
    GalleryImageSelector ga2;
    GalleryImageSelector ga3;

    protected void setGalleryImageObjects(GalleryImageSelector ga1, GalleryImageSelector ga2, GalleryImageSelector ga3) {
        this.ga1 = ga1;
        this.ga2 = ga2;
        this.ga3 = ga3;
    }

    private boolean isFirstTime = true;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*if (imageSelector != null)
            imageSelector.onActivityResult(requestCode, resultCode, data);*/

        if (imageSelector != null) {
            if (getPostType() == SocialPost.POST_TYPE_BEST_OF) {
                if (isFirstTime) {
                    if (requestCode == GalleryImageSelector.PICK_SERVER_GALLERY && resultCode == RESULT_OK) {
                        ArrayList<HashMap<String, Object>> listOfMapData = getListOfMapData(data);
                        if (listOfMapData.size() == 3) {
                            ga3.setDataFromOutside(listOfMapData.get(2), GalleryImageSelector.PICK_SERVER_GALLERY);
                            ga3.setIsPtbCall(false);
                            ga2.setDataFromOutside(listOfMapData.get(1), GalleryImageSelector.PICK_SERVER_GALLERY);
                            ga2.setIsPtbCall(false);
                            ga1.setDataFromOutside(listOfMapData.get(0), GalleryImageSelector.PICK_SERVER_GALLERY);
                            ga1.setIsPtbCall(false);

                            rlimageholder2.setVisibility(View.VISIBLE);
                            rlimageholder3.setVisibility(View.VISIBLE);
                            return;
                        }
                        if (listOfMapData.size() <= 2) {
                            ga2.setDataFromOutside(listOfMapData.get(1), GalleryImageSelector.PICK_SERVER_GALLERY);
                            ga2.setIsPtbCall(false);
                            ga1.setDataFromOutside(listOfMapData.get(0), GalleryImageSelector.PICK_SERVER_GALLERY);
                            ga1.setIsPtbCall(false);
                            ga3.setIsPtbCall(false);
                            rlimageholder2.setVisibility(View.VISIBLE);
                            return;
                        }
                        if (listOfMapData.size() == 1) {
                            ga1.setDataFromOutside(listOfMapData.get(0), GalleryImageSelector.PICK_SERVER_GALLERY);
                            ga1.setIsPtbCall(false);
                        }
                    } else imageSelector.onActivityResult(requestCode, resultCode, data);
                } else imageSelector.onActivityResult(requestCode, resultCode, data);
            } else imageSelector.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ArrayList<HashMap<String, Object>> getListOfMapData(Intent data) {
        ArrayList<FilterProductListing> dataFromSelection = data.getParcelableArrayListExtra(IntentKeys.FILTER_OBJ);
        ArrayList<HashMap<String, Object>> dataMap = new ArrayList<>();
        isFirstTime = false;
        for (FilterProductListing params : dataFromSelection) {
            HashMap<String, Object> eachMap = new HashMap<>();
            String path = /*".." +*/ params.getImage().substring(params.getImage()
                    .indexOf(/*"/product"*/"v200/"), params.getImage().length());
                    /*".." + params.getImage().substring(params.getImage().indexOf("/product"), params.getImage().length());*/
            eachMap.put(GalleryImageSelector.KEY_PATH, path);
            eachMap.put(GalleryImageSelector.KEY_PREVIEW, params.getImage());
            eachMap.put(GalleryImageSelector.KEY_PRICE, params.getProductPrice());
            eachMap.put(GalleryImageSelector.KEY_COLLID, params.getCollId());
            eachMap.put(GalleryImageSelector.KEY_DESID, params.getDesgnId());
            eachMap.put(GalleryImageSelector.KEY_PRODID, params.getProdId());
            eachMap.put(GalleryImageSelector.KEY_DISCOUNT, params.getDiscount());
            eachMap.put(GalleryImageSelector.KEY_RANGE, params.getRange());
            eachMap.put(GalleryImageSelector.KEY_PROD_TYPE, params.getProdType());
            dataMap.add(eachMap);
        }
        return dataMap;
    }

    View rlimageholder2;
    View rlimageholder3;

    protected void setHolders(View rlPlaceHolder2, View rlPlaceHolder3) {
        rlimageholder2 = rlPlaceHolder2;
        rlimageholder3 = rlPlaceHolder3;
    }
}


