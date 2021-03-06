package com.goldadorn.main.activities.post;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.goldadorn.main.bindings.ImageBindings;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.utils.GalleryImageSelector;
import com.goldadorn.main.utils.ImageSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class PostBestOfActivity extends AbstractPostActivity {
    private GalleryImageSelector imageSelector1;
    private GalleryImageSelector imageSelector2;
    private GalleryImageSelector imageSelector3;

    protected String getPageTitle() {
        return /*"Best of Three"*/"Pick the best";
    }

    protected List<Integer> getPrice() {
        List<Integer> list = new ArrayList<>();
        list.add(imageSelector1.price);
        list.add(imageSelector2.price);
        list.add(imageSelector3.price);
        return list;
    }

    protected List<File> getFiles() {
        boolean isAdded = false;
        List<File> map = new ArrayList<>();
        if (imageSelector1.isValid() && imageSelector1.getFile() != null) {
            map.add(imageSelector1.getFile());
            isAdded = true;
        }


        if (imageSelector2.isValid() && imageSelector2.getFile() != null) {
            map.add(imageSelector2.getFile());
            isAdded = true;
        }


        if (imageSelector3.isValid() && imageSelector3.getFile() != null) {
            map.add(imageSelector3.getFile());
            isAdded = true;
        }

        if (isAdded)
            return map;
        return null;
    }

    protected List<String> getFilesPath() {

        boolean isAdded = false;
        List<String> map = new ArrayList<>();
        if (imageSelector1.isValid() && imageSelector1.getFilePath() != null) {
            map.add(imageSelector1.getFilePath());
            isAdded = true;
        }
        if (imageSelector2.isValid() && imageSelector2.getFilePath() != null) {
            map.add(imageSelector2.getFilePath());
            isAdded = true;
        }
        if (imageSelector3.isValid() && imageSelector3.getFilePath() != null) {
            map.add(imageSelector3.getFilePath());
            isAdded = true;
        }
        if (isAdded)
            return map;
        return null;
    }

    protected List<String> getLinks() {
        boolean isAdded = false;
        List<String> map = new ArrayList<>();
        if (imageSelector1.isValid() && imageSelector1.getLink() != null) {
            isAdded = true;
            //map.add(imageSelector1.getLink());
            String url = Product.getImageUrl(imageSelector1.getProductId(), imageSelector1.getDesId(), null, false, -1);
            url = url.substring(url.indexOf("defaults/"), url.length());
            map.add(url);
        }

        if (imageSelector2.isValid() && imageSelector2.getLink() != null) {
            isAdded = true;
            //map.add(imageSelector2.getLink());
            String url = Product.getImageUrl(imageSelector2.getProductId(), imageSelector2.getDesId(), null, false, -1);
            url = url.substring(url.indexOf("defaults/"), url.length());
            map.add(url);
        }

        if (imageSelector3.isValid() && imageSelector3.getLink() != null) {
            isAdded = true;
            //map.add(imageSelector3.getLink());
            String url = Product.getImageUrl(imageSelector3.getProductId(), imageSelector3.getDesId(), null, false, -1);
            url = url.substring(url.indexOf("defaults/"), url.length());
            map.add(url);
        }


        if (isAdded)
            return map;
        return null;
    }


    protected int getmainResID() {
        return R.layout.activity_post_best_of;
    }

    protected String isValid() {
        if (imageSelector1.isValid() == false || imageSelector2.isValid() == false)
            return "Please upload minimum two images";
        else if (details.getText().toString().equals(""))
            return "Please enter some details";
        return null;
    }


    @Bind(R.id.rlPlaceHolder2)
    View rlPlaceHolder2;
    @Bind(R.id.rlPlaceHolder3)
    View rlPlaceHolder3;
    /*@Override
    protected List<Integer> getCollIds() {

        List<Integer> collIdList = new ArrayList<>();
        boolean isAdded = false;
        if (imageSelector1.isValid()){
            isAdded = true;
            collIdList.add(imageSelector1.getCollId());
        }
        if (imageSelector2.isValid()){
            isAdded = true;
            collIdList.add(imageSelector2.getCollId());
        }
        if (imageSelector3.isValid()){
            isAdded = true;
            collIdList.add(imageSelector3.getCollId());
        }
        if(isAdded)
            return collIdList;
        else return null;
        //// TODO: 30-06-2016
    }

    @Override
    protected List<Integer> getDesignerIds() {

        List<Integer> desIdList = new ArrayList<>();
        boolean isAdded = false;
        if (imageSelector1.isValid()){
            isAdded = true;
            desIdList.add(imageSelector1.getDesId());
        }
        if (imageSelector2.isValid()){
            isAdded = true;
            desIdList.add(imageSelector2.getDesId());
        }
        if (imageSelector3.isValid()){
            isAdded = true;
            desIdList.add(imageSelector3.getDesId());
        }
        if(isAdded)
            return desIdList;
        else return null;
        //// TODO: 30-06-2016
    }*/

    @Override
    protected List<String> getClubbingData() {
        boolean isAdded = false;
        List<String> list = new ArrayList<>();
        if (imageSelector1.isValid()) {
            isAdded = true;
            StringBuilder sb = new StringBuilder();
            list.add(sb.append(imageSelector1.getProductId()).append(":")
                    .append(imageSelector1.getCollId()).append(":")
                    .append(imageSelector1.getDesId()).append(":")
                    .append(imageSelector1.getPrice()).append(":")
                    .append(imageSelector1.getRange()).append(":")
                    .append(imageSelector1.getDiscount()).toString());
        }

        if (imageSelector2.isValid()) {
            isAdded = true;
            StringBuilder sb = new StringBuilder();
            list.add(sb.append(imageSelector2.getProductId()).append(":")
                    .append(imageSelector2.getCollId()).append(":")
                    .append(imageSelector2.getDesId()).append(":")
                    .append(imageSelector2.getPrice()).append(":")
                    .append(imageSelector2.getRange()).append(":")
                    .append(imageSelector2.getDiscount()).toString());
        }

        if (imageSelector3.isValid()) {
            isAdded = true;
            StringBuilder sb = new StringBuilder();
            list.add(sb.append(imageSelector3.getProductId()).append(":")
                    .append(imageSelector3.getCollId()).append(":")
                    .append(imageSelector3.getDesId()).append(":")
                    .append(imageSelector3.getPrice()).append(":")
                    .append(imageSelector3.getRange()).append(":")
                    .append(imageSelector3.getDiscount()).toString());
        }

        if (isAdded)
            return list;
        else return null;
    }

    @Override
    protected List<String> getProdTypes() {
        List<String> list = new ArrayList<>();
        if (imageSelector1.isValid()) {
            if (imageSelector1.getProductId() > 0)
                list.add(imageSelector1.getProdType());
        }
        if (imageSelector2.isValid()) {
            if (imageSelector2.getProductId() > 0)
                list.add(imageSelector2.getProdType());
        }
        if (imageSelector3.isValid()) {
            if (imageSelector3.getProductId() > 0)
                list.add(imageSelector3.getProdType());
        }
        return list;
    }


    protected int getPostType() {
        return SocialPost.POST_TYPE_BEST_OF;
    }

    @Bind(R.id.previewIamge1)
    ImageView previewIamge1;
    @Bind(R.id.previewIamge2)
    ImageView previewIamge2;
    @Bind(R.id.previewIamge3)
    ImageView previewIamge3;

    @Bind(R.id.trigger1)
    ImageButton trigger1;

    @Bind(R.id.trigger2)
    ImageButton trigger2;

    @Bind(R.id.trigger3)
    ImageButton trigger3;


    protected void viewCreted(People people, int maxImageSize) {
        details.setText("Guys, which one is the best of these? #BOT");
        imageSelector1 = new GalleryImageSelector(this, this, previewIamge1, trigger1);
        imageSelector1.setMaxSize(maxImageSize);
        imageSelector2 = new GalleryImageSelector(this, this, previewIamge2, trigger2);
        imageSelector2.setMaxSize(maxImageSize);
        imageSelector3 = new GalleryImageSelector(this, this, previewIamge3, trigger3);
        imageSelector3.setMaxSize(maxImageSize);
        imageSelector1.setIsPtbCall(true);
        imageSelector2.setIsPtbCall(true);
        imageSelector3.setIsPtbCall(true);
        rlPlaceHolder2.setVisibility(View.VISIBLE);
        rlPlaceHolder3.setVisibility(View.VISIBLE);

        setGalleryImageObjects(imageSelector1, imageSelector2, imageSelector3);
        setHolders(rlPlaceHolder2, rlPlaceHolder3);
        /*imageSelector2.setIsPtbCall(true);
        imageSelector3.setIsPtbCall(true);*/
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageSelector1.onRequestPermResult(requestCode, permissions, grantResults);
    }
}
