package com.goldadorn.main.model;

/**
 * Created by bhavinpadhiyar on 2/20/16.
 */
public class Image {
    public Image(String url)
    {
        this.url=url;
    }

    public String url;
    public int w=-1;
    public int h=-1;
    public boolean isLoaded;
    public boolean isBroken=false;
}
