package com.goldadorn.main.activities.post;

import com.goldadorn.main.model.SocialPost;

/**
 * Created by User on 03-11-2016.
 */
public class RecommendedNewPostActivity extends PostNormalActivity{

    @Override
    protected int getPostType() {
        return SocialPost.POST_TYPE_RECO_NEW;
    }
}
