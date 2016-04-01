package com.goldadorn.main.activities.post;

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
public class PostPollActivity extends AbstractPostActivity {
    private GalleryImageSelector imageSelector1;
    protected String getPageTitle()
    {
        return "Buy or Not buy";
    }
    protected List<File> getFiles()
    {
        if(imageSelector1.isValid()) {
            List<File> map= new ArrayList<>();
            if(imageSelector1.getFile()!=null)
                map.add(imageSelector1.getFile());
            return map;
        }
        return null;
    }


    protected List<String> getLinks()
    {
        if(imageSelector1.isValid())
        {
            List<String> map= new ArrayList<>();
            if(imageSelector1.getLink()!=null)
                map.add(imageSelector1.getLink());
            return map;
        }
        return null;
    }
    protected List<String> getFilesPath()
    {
        if(imageSelector1.isValid())
        {
            List<String> map= new ArrayList<>();
            if(imageSelector1.getFilePath()!=null)
                map.add(imageSelector1.getFilePath());
            return map;
        }
        return null;
    }

    protected String isValid()
    {
        if(imageSelector1.isValid()==false)
            return "Please upload image";
        else if(details.getText().toString().equals(""))
            return "Please enter some details";
        return null;
    }

    protected int getPostType()
    {
        return SocialPost.POST_TYPE_POLL;
    }

    protected int getmainResID()
    {
        return R.layout.activity_post_buy;
    }
    @Bind(R.id.previewIamge)
    ImageView previewIamge;

    protected void viewCreted(People people,int maxImageSize)
    {
        details.setText("Folks, should I buy or not buy this? #BONB");
        imageSelector1 =  new GalleryImageSelector(this,this,previewIamge);
        imageSelector1.setMaxSize(maxImageSize);
    }
}
