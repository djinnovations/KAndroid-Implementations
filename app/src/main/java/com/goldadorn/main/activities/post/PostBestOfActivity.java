package com.goldadorn.main.activities.post;

import android.widget.ImageView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.utils.ImageSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class PostBestOfActivity extends AbstractPostActivity {
    private ImageSelector imageSelector1;
    private ImageSelector imageSelector2;
    private ImageSelector imageSelector3;

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

    protected List<File> getFiles()
    {
        List<File> map= new ArrayList<>();
        map.add(imageSelector1.getFile());
        map.add(imageSelector2.getFile());
        if(imageSelector3.isValid())
            map.add(imageSelector3.getFile());
        return map;
    }
    protected List<String> getFilesPath()
    {
        List<String> map= new ArrayList<>();
        map.add(imageSelector1.getFilePath());
        map.add(imageSelector2.getFilePath());
        if(imageSelector3.isValid())
            map.add(imageSelector3.getFilePath());
        return map;
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


    protected void viewCreted(People people,int maxImageSize)
    {
        details.setText("Guys, which one is the best of these 3? #BOT");
        imageSelector1 =  new ImageSelector(this,this,previewIamge1);
        imageSelector1.setMaxSize(maxImageSize);
        imageSelector2 =  new ImageSelector(this,this,previewIamge2);
        imageSelector2.setMaxSize(maxImageSize);
        imageSelector3 =  new ImageSelector(this,this,previewIamge3);
        imageSelector3.setMaxSize(maxImageSize);
    }
}
