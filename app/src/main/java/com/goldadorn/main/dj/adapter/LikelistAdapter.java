package com.goldadorn.main.dj.adapter;

/**
 * Created by User on 19-10-2016.
 */
public class LikelistAdapter extends WishlistAdapter{


    @Override
    protected boolean isShowButtons() {
        //return super.isShowButtons();
        return false;
    }


    @Override
    protected boolean isLikeList() {
        //return super.isLikeList();
        return true;
    }
}
