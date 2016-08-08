package com.goldadorn.main.activities.post;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Image;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.CreatePostForBuyResponse;
import com.goldadorn.main.utils.GalleryImageSelector;
import com.goldadorn.main.utils.ImageFilePath;
import com.goldadorn.main.utils.ImageLoaderUtils;
import com.goldadorn.main.views.ColoredSnackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class PostPollActivity extends AbstractPostActivity {

    private GalleryImageSelector imageSelector1;
    private Product mProduct;
    private ProgressDialog mProgressDialog;

    private final static String toPostKey = "productpollpost";
    public static Intent getLaunchIntent(Context context, Product product) {
        Intent in = new Intent(context, PostPollActivity.class);
        if (product != null)
            in.putExtra(toPostKey, product);
        People people = ((Application) context.getApplicationContext()).getPeople();
        in.putExtra("NAME", people.getUserName());
        in.putExtra("FOLLOWER_COUNT", people.getFollowerCount());
        in.putExtra("FOLLOWING_COUNT", people.getFollowingCount());
        in.putExtra("PROFILE_PIC", people.getProfilePic());
        in.putExtra("IS_DESIGNER", people.getIsDesigner());
        in.putExtra("ID", people.getUserId());
        in.putExtra("IS_SELF", people.isSelf());
        in.putExtra("backEnabled", true);
        return in;

    }

    protected List<Integer> getPrice() {
        List<Integer> list = new ArrayList<>();
        list.add(imageSelector1.price);
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        if (b != null)
            mProduct = (Product) b.getSerializable(toPostKey);
        super.onCreate(savedInstanceState);
    }

    protected String getPageTitle() {
        return "Buy or Not buy";
    }

    protected List<File> getFiles() {
        if (imageSelector1 != null && imageSelector1.isValid()) {
            List<File> map = new ArrayList<>();
            if (imageSelector1.getFile() != null)
                map.add(imageSelector1.getFile());
            return map;
        }
        return null;
    }

    /*@Override
    protected void postNow() {
        if (mProduct == null)
            super.postNow();
        else {
            String valid = isValid();
            if (valid == null) {
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(PostPollActivity.this);
                    mProgressDialog.setMessage("Loading");
                    mProgressDialog.setCancelable(false);
                }
                mProgressDialog.show();
                UIController.buyorNobuy(PostPollActivity.this, new CreatePostForBuyResponse(0, details.getText().toString(), mProduct.getImageUrl()), new IResultListener<CreatePostForBuyResponse>() {
                    @Override
                    public void onResult(CreatePostForBuyResponse result) {
                        mProgressDialog.dismiss();
                        if (result.success) {
                            finish();
                        } else {
                            Toast.makeText(PostPollActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Snackbar snackbar = Snackbar.make(layoutParent, valid, Snackbar.LENGTH_SHORT);
                ColoredSnackbar.alert(snackbar).show();
            }
        }
    }*/

    protected List<String> getLinks() {
        if (mProduct != null) {
            List<String> t = new ArrayList<>();
            String url = mProduct.getImageUrl();
            String path = ".." + url.substring(url.indexOf("/product"), url.length());
            t.add(path);
            return t;
        }
        if (imageSelector1 != null && imageSelector1.isValid()) {
            List<String> map = new ArrayList<>();
            if (imageSelector1.getLink() != null)
                map.add(imageSelector1.getLink());
            return map;
        }
        return null;
    }

    protected List<String> getFilesPath() {
        if (imageSelector1 != null && imageSelector1.isValid()) {
            List<String> map = new ArrayList<>();
            if (imageSelector1.getFilePath() != null)
                map.add(imageSelector1.getFilePath());
            return map;
        }
        return null;
    }

    protected String isValid() {
        if (mProduct == null) {
            if (imageSelector1 != null && imageSelector1.isValid() == false)
                return "Please upload image";
        }
        if (details.getText().toString().equals(""))
            return "Please enter some details";

        return null;
    }

   /* @Override
    protected List<Integer> getCollIds() {
        if (imageSelector1.isValid()) {
            List<Integer> collIdList = new ArrayList<>();
            collIdList.add(imageSelector1.getCollId());
            return collIdList;
        }
        return null;
    }

    @Override
    protected List<Integer> getDesignerIds() {
        if (imageSelector1.isValid()) {
            List<Integer> desIdList = new ArrayList<>();
            desIdList.add(imageSelector1.getDesId());
            return desIdList;
        }
        return null;
    }*/

    @Override
    protected List<String> getProductAppendedColDesIdPrice() {

        if (mProduct != null){
            List<String> list = new ArrayList<>();
            list.add(mProduct.id+":"+mProduct.collectionId+":"+mProduct.userId+":"+mProduct.getDisplayPrice());
            return list;
        }
        else if (imageSelector1.isValid()) {
            if (imageSelector1.getProductId() == -1)
                return null;
            List<String> list = new ArrayList<>();
            list.add(imageSelector1.getProductId()+":"+imageSelector1.getCollId()+":"+imageSelector1.getDesId()+":"+imageSelector1.getPrice());
            return list;
        }
        return null;
    }

    protected int getPostType() {
        return SocialPost.POST_TYPE_POLL;
    }

    protected int getmainResID() {
        return R.layout.activity_post_buy;
    }

    @Bind(R.id.previewIamge)
    ImageView previewIamge;

    @Bind(R.id.trigger)
    ImageButton trigger;


    protected void viewCreted(People people, int maxImageSize) {
        details.setText("Folks, should I buy or not buy this? #BONB");
        if (mProduct == null) {
            imageSelector1 = new GalleryImageSelector(this, this, previewIamge, trigger);
            imageSelector1.setMaxSize(maxImageSize);
        } else {
            imageSelector1 = null;
            trigger.setVisibility(View.GONE);
            //Picasso.with(this).load(mProduct.getImageUrl()).into(previewIamge);
            ImageLoaderUtils.loadImage(Application.getInstance()
                    , new Image(mProduct.getImageUrl()), previewIamge, R.drawable.vector_image_logo_square_100dp);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageSelector1.onRequestPermResult(requestCode, permissions, grantResults);
    }
}
