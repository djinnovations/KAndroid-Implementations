package com.goldadorn.main.activities.post;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.People;
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
    protected String getPageTitle()
    {
        return /*"Best of Three"*/"Pick the best";
    }

    protected List<Integer> getPrice()
    {
        List<Integer> list= new ArrayList<>();
        list.add(imageSelector1.price);
        list.add(imageSelector2.price);
        list.add(imageSelector3.price);
        return list;
    }

    protected List<File> getFiles()
    {
        boolean isAdded=false;
        List<File> map= new ArrayList<>();
        if(imageSelector1.isValid() && imageSelector1.getFile()!=null)
        {
            map.add(imageSelector1.getFile());
            isAdded=true;
        }


        if(imageSelector2.isValid() && imageSelector2.getFile()!=null)
        {
            map.add(imageSelector2.getFile());
            isAdded=true;
        }


        if(imageSelector3.isValid() && imageSelector3.getFile()!=null)
        {
            map.add(imageSelector3.getFile());
            isAdded=true;
        }

        if(isAdded)
            return map;
        return null;
    }
    protected List<String> getFilesPath()
    {

        boolean isAdded=false;
        List<String> map= new ArrayList<>();
        if(imageSelector1.isValid() && imageSelector1.getFilePath()!=null) {
            map.add(imageSelector1.getFilePath());
            isAdded=true;
        }
        if(imageSelector2.isValid() && imageSelector2.getFilePath()!=null) {
            map.add(imageSelector2.getFilePath());
            isAdded=true;
        }
        if(imageSelector3.isValid() && imageSelector3.getFilePath()!=null)
        {
            map.add(imageSelector3.getFilePath());
            isAdded=true;
        }
         if(isAdded)
            return map;
        return null;
    }

    protected List<String> getLinks()
    {
        boolean isAdded=false;
        List<String> map= new ArrayList<>();
        if(imageSelector1.isValid() && imageSelector1.getLink()!=null)
        {
            isAdded=true;
            map.add(imageSelector1.getLink());
        }

        if(imageSelector2.isValid() && imageSelector2.getLink()!=null)
        {
            isAdded=true;
            map.add(imageSelector2.getLink());
        }

        if(imageSelector3.isValid() && imageSelector3.getLink()!=null)
        {
            isAdded=true;
            map.add(imageSelector3.getLink());
        }


        if(isAdded)
            return map;
        return null;
    }





    protected int getmainResID()
    {
        return R.layout.activity_post_best_of;
    }

    protected String isValid()
    {
        if(imageSelector1.isValid()==false || imageSelector2.isValid()==false)
            return "Please upload minimum two images";
        else if(details.getText().toString().equals(""))
            return "Please enter some details";
        return null;
    }

    @Override
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
    }


    protected int getPostType()
    {
        return SocialPost.POST_TYPE_BEST_OF;
    }

    @Bind(R.id.previewIamge1)
    ImageView previewIamge1;
    @Bind(R.id.previewIamge2)
    ImageView previewIamge2;
    @Bind(R.id.previewIamge3)
    ImageView previewIamge3;

    @Bind(R.id.trigger1)
    View trigger1;

    @Bind(R.id.trigger2)
    View trigger2;

    @Bind(R.id.trigger3)
    View trigger3;


    protected void viewCreted(People people,int maxImageSize)
    {
        details.setText("Guys, which one is the best of these? #BOT");
        imageSelector1 =  new GalleryImageSelector(this,this,previewIamge1,trigger1);
        imageSelector1.setMaxSize(maxImageSize);
        imageSelector2 =  new GalleryImageSelector(this,this,previewIamge2,trigger2);
        imageSelector2.setMaxSize(maxImageSize);
        imageSelector3 =  new GalleryImageSelector(this,this,previewIamge3,trigger3);
        imageSelector3.setMaxSize(maxImageSize);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageSelector1.onRequestPermResult(requestCode, permissions, grantResults);
    }
}
