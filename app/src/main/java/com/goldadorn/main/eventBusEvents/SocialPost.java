package com.goldadorn.main.eventBusEvents;

import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class SocialPost {


    public int type;
    public SocialFeedFragment host;

    public SocialPost(int type,SocialFeedFragment host)
    {
        this.type=type;
        this.host=host;
    }
}
