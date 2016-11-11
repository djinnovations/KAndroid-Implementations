package com.goldadorn.main.dj.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
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
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.dj.utils.Constants;

/**
 * Created by User on 18-10-2016.
 */
public class SocialUtils extends SocialLoginUtil {

    private static SocialUtils socialUtils;

    private SocialUtils(Context context) {
        super(context);
        shareResults = new
                FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        if (activity instanceof MainActivity){
                            ((MainActivity) activity).notifyShare(recentPostId);
                        }
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

    public static SocialUtils getInstance(Context context) {
        if (socialUtils == null)
            socialUtils = new SocialUtils(context);
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
    private Activity activity;
    private String recentPostId;
    public void publishLinkPost(Activity activity, String contentUrl, String title, String descrip, String imageUrl, String postId) {
        this.activity = activity;
        recentPostId = postId;
        shareDialog = new ShareDialog(activity);
        callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, shareResults);
        String tile = "";
        if (TextUtils.isEmpty(descrip) || descrip.contains("null"))
            tile = "";
        else tile = descrip;

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(tile).setImageUrl(Uri.parse(imageUrl))
                    .setContentUrl(Uri.parse(contentUrl))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        //super.handleActivityResult(requestCode, resultCode, data);
        Log.d(Constants.TAG, "onActivity result - SocialUtils");
        if (callbackManager != null)
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
