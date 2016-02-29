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
public class PostNormalActivity extends AbstractPostActivity {
    private ImageSelector imageSelector1;

    protected List<File> getFiles()
    {
        if(imageSelector1.isValid()) {
            List<File> map= new ArrayList<>();
            map.add(imageSelector1.getFile());
            return map;
        }
        return null;
    }
    protected List<String> getFilesPath()
    {
        if(imageSelector1.isValid()) {
            List<String> map= new ArrayList<>();
            map.add(imageSelector1.getFilePath());
            return map;
        }
        return null;
    }
    protected int getPostType()
    {
        return SocialPost.POST_TYPE_NORMAL_POST;
    }

    protected String isValid()
    {
        if(details.getText().toString().equals(""))
            return "Please enter some details";
        return null;
    }

    protected int getmainResID()
    {
        return R.layout.activity_post_normal;
    }

    @Bind(R.id.previewIamge)
    ImageView previewIamge;

    protected void viewCreted(People people,int maxImageSize)
    {
        imageSelector1 =  new ImageSelector(this,this,previewIamge);
        imageSelector1.setMaxSize(maxImageSize);
    }
}

