package com.goldadorn.main.dj.support;

/**
 * Created by User on 20-07-2016.
 */
public class ShowcaseLikeUpdateHelper {

    private static ShowcaseLikeUpdateHelper ourInstance;

    public static ShowcaseLikeUpdateHelper getInstance(){
        if (ourInstance == null)
            ourInstance = new ShowcaseLikeUpdateHelper();
        return ourInstance;
    }


    private ShowcaseLikeUpdateHelper(){

    }


}
