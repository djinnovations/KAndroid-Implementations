package com.goldadorn.main.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.post.AbstractPostActivity;
import com.goldadorn.main.activities.post.PostBestOfActivity;
import com.goldadorn.main.activities.post.PostNormalActivity;
import com.goldadorn.main.activities.post.PostPollActivity;
import com.goldadorn.main.activities.post.SelectServerImageActivity;
import com.goldadorn.main.model.People;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class GalleryImageSelector extends ImageSelector
{
    public static final int PICK_SERVER_GALLERY = 3;
    private String path;


    public boolean isValid()
    {
        if(selectedMethod == PICK_SERVER_GALLERY && path!=null && path.equals("")==false)
            return true;
        else
            return super.isValid();
    }
    public Item[] getOptions() {
        Item[] items = {
                new Item("Take a picture", android.R.drawable.ic_menu_camera),
                new Item("Select from gallery", android.R.drawable.ic_menu_gallery),
                new Item("Select from our collections", R.drawable.vector_icon_about)
        };
        return items;
    }
    final
    public void onOptionSelect(int which) {
        if (which == 2) {
            registerImageUploadCallBack.registerImageUploadCallBack(this);
            openServerSelecton();
        }
        else
            super.onOptionSelect(which);
    }

    public void openServerSelecton() {
        Intent intent = new Intent(activity, SelectServerImageActivity.class);
        intent.putExtra("backEnabled",true);
        activity.startActivityForResult(intent, PICK_SERVER_GALLERY);
    }
    public String getLink()
    {
        return path;
    }
    public GalleryImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack)
    {
        super(activity,registerImageUploadCallBack);
    }
    public GalleryImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack, ImageView holder, View triger)
    {
        super(activity,registerImageUploadCallBack,holder,triger);
    }
    public GalleryImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack, ImageView holder)
    {
        super(activity,registerImageUploadCallBack,holder);
    }
     public void onActivityResult(int requestCode, int resultCode, Intent data)
     {
        if (requestCode==PICK_SERVER_GALLERY && resultCode == Activity.RESULT_OK)
        {
            path=data.getStringExtra("PATH");
            file=null;
            selectedMethod = PICK_SERVER_GALLERY;
            showPreview(data.getStringExtra("PREVIEW"));
            registerImageUploadCallBack.unRegisterImageUploadCallBack(this);
        }
        else
            super.onActivityResult(requestCode, resultCode,data);
    }
    public void imageLoaded(Bitmap bitmap,File file,int method) {
        super.imageLoaded(bitmap,file,method);
        if(method!=PICK_SERVER_GALLERY)
            path=null;
    }

    private void showPreview(String preview) {
        if(holder!=null)
            Picasso.with(activity).load(preview).into(holder);
    }

    public Bitmap decodeFile(String filePath) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        Bitmap out = BitmapFactory.decodeFile(filePath,o);
        return out;
    }
}
