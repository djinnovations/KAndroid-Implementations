package com.goldadorn.main.dj.support;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.utils.Constants;

/**
 * Created by User on 18-10-2016.
 */
public class SocialUtils extends SocialLoginUtil {

    private static SocialUtils socialUtils;

    private SocialUtils() {
        super();
        shareResults = new
                FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(Application.getInstance(), "Shared on Facebook", android.widget.Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(Application.getInstance(), "Sharing Aborted", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(Application.getInstance(), " Network Error", Toast.LENGTH_LONG).show();
                    }
                };
    }

    public static SocialUtils getInstance() {
        if (socialUtils == null)
            socialUtils = new SocialUtils();
        return socialUtils;
    }

    ShareDialog shareDialog;
    FacebookCallback<Sharer.Result> shareResults /*= new
            FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Toast.makeText(Application.getInstance(), "Shared on Facebook", android.widget.Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(Application.getInstance(), "Sharing Aborted", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(Application.getInstance(), " Network Error", Toast.LENGTH_LONG).show();
                }
            }*/;


    private CallbackManager callbackManager;
    public void publishLinkPost(Activity activity, String contentUrl, String title, String descrip, String imageUrl) {
        shareDialog = new ShareDialog(activity);
        callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, shareResults);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(descrip).setImageUrl(Uri.parse(imageUrl))
                    .setContentUrl(Uri.parse(contentUrl))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        //super.handleActivityResult(requestCode, resultCode, data);
        Log.d(Constants.TAG, "onActivity result - SocialUtils");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void publishImagePost(Activity activity, Bitmap bitmap, String title) {
        shareDialog = new ShareDialog(activity);
        //callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, shareResults);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            /*ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello Facebook")
                    .setContentDescription("The 'Hello Facebook' sample  showcases simple Facebook integration")
                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .build();*/

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap).setCaption(title)
                    .build();
            SharePhotoContent photoContent = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            shareDialog.show(photoContent);
        }
    }

}
