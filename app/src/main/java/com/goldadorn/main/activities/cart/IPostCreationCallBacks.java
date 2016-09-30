package com.goldadorn.main.activities.cart;

import java.util.List;

/**
 * Created by User on 26-09-2016.
 */
public interface IPostCreationCallBacks {

    int getPostType();
    List<String> getClubbingData();
    List<String> getLinks();
    String getPostMsg();
}
