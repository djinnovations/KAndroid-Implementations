package com.goldadorn.main.dj.uiutils;

import android.app.Activity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;

/**
 * Created by User on 29-06-2016.
 */
public class UiRandomUtils {

    public static void startAnim(View view, int animResID) throws Exception {
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), animResID);
        view.startAnimation(anim);
    }

    public static void setPaddingLeftRight(TextView textView){
        DisplayProperties dispProp = Application.getInstance().getDisplayPropInstance();
        textView.setPadding((int) (5*dispProp.getXPixelsPerCell()), 0,
                (int) (5*dispProp.getXPixelsPerCell()), (int) (5*dispProp.getYPixelsPerCell()));
    }

    public static void underLineTv(TextView textview, int start, int end) {
        String udata = "";
        try {
            udata=/*"Underlined Text"*/ textview.getText().toString().trim();
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), start, end, 0);
            textview.setText(content);
        } catch (Exception e) {
            e.printStackTrace();
            textview.setText(udata);
        }
        //textview.setPaintFlags(textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /*public interface LikeStatusCallback{
        void onLikeStatResult(LikeResponse result);
    }
*/

    /*public static void manupilateLikeStat(Activity activity, final LikeStatusCallback mCallback, final Product product, boolean isLikeAction) {

        Log.d("djlike", "product like stat: startPoint: " + product.likeStat);
        Log.d("djlike", "product id: startPoint: " + product.id);
        Log.d("djlike", "product name: startPoint: " + product.name);
        if (product.likeStat == 0) {
            Log.d("djlike", "product like stat=0");
            if (isLikeAction) {
                Log.d("djlike", "product like stat=0; likedAction " + isLikeAction);
                UIController.like(activity, product, true,
                        new IResultListener<LikeResponse>() {
                            @Override
                            public void onResult(LikeResponse result) {
                                mCallback.onLikeStatResult(result);
                               *//* isLikedHover(false);
                                if (result.success) {
                                    product.isLiked = true;
                                    product.likecount++;
                                }
                                if (mToast != null) mToast.cancel();
                                mToast = Toast.makeText(getActivity(),
                                        result.success ? product.name + " liked" : "failed to update", Toast.LENGTH_LONG);
                                mToast.show();*//*
                            }
                        });
            } else {
                Log.d("djlike", "product like stat=0; likedAction " + isLikeAction);
                UIController.like(getActivity(), product, false, new IResultListener<LikeResponse>() {
                    @Override
                    public void onResult(LikeResponse result) {
                        isDislikedHover(false);
                        if (result.success) {
                            product.isLiked = false;
                            product.likecount--;
                        }
                        if (mToast != null) mToast.cancel();
                        mToast = Toast.makeText(getActivity(),
                                result.success ? product.name + " dis-liked" : "failed to update", Toast.LENGTH_LONG);
                        mToast.show();
                    }
                });
            }
        } else if (product.likeStat == 1) {
            Log.d("djlike", "product like stat=1");
            if (isLikeAction) {
                Log.d("djlike", "product like stat=1; likedAction " + isLikeAction);
                if (mToast != null) mToast.cancel();
                mToast = Toast.makeText(getActivity(), "You have already liked this product!", Toast.LENGTH_LONG);
                mToast.show();
                isLikedHover(false);
            } else {
                Log.d("djlike", "product like stat=1; likedAction " + isLikeAction);
                UIController.like(getActivity(), product, false, new IResultListener<LikeResponse>() {
                    @Override
                    public void onResult(LikeResponse result) {
                        isDislikedHover(false);
                        if (result.success) {
                            product.isLiked = false;
                            product.likecount--;
                        }
                        if (mToast != null) mToast.cancel();
                        mToast = Toast.makeText(getActivity(),
                                result.success ? product.name + " dis-liked" : "failed to update", Toast.LENGTH_LONG);
                        mToast.show();
                    }
                });
            }
        } else if (product.likeStat == -1) {
            Log.d("djlike", "product like stat=-1");
            if (!isLikeAction) {
                Log.d("djlike", "product like stat=-1; likedAction " + isLikeAction);
                if (mToast != null) mToast.cancel();
                mToast = Toast.makeText(getActivity(), "You have already disliked this product!", Toast.LENGTH_LONG);
                mToast.show();
                isDislikedHover(false);
            } else {
                Log.d("djlike", "product like stat=-1; likedAction " + isLikeAction);
                UIController.like(getActivity(), product, true, new IResultListener<LikeResponse>() {
                    @Override
                    public void onResult(LikeResponse result) {
                        isLikedHover(false);
                        if (result.success) {
                            product.isLiked = false;
                            product.likecount--;
                        }
                        if (mToast != null) mToast.cancel();
                        mToast = Toast.makeText(getActivity(),
                                result.success ? product.name + " liked" : "failed to update", Toast.LENGTH_LONG);
                        mToast.show();
                    }
                });
            }
        }
    }*/
}
