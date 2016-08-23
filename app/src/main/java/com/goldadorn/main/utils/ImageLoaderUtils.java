package com.goldadorn.main.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.lang.ref.WeakReference;

/**
 * Created by bhavinpadhiyar on 2/20/16.
 */
public class ImageLoaderUtils {

    public static void loadImage(Context context,Image imageData, ImageView image,int defaultImageRes,Callback callback) {
/*
        if(imageData.h!=-1)
        {
            ViewGroup.LayoutParams layoutParams=image.getLayoutParams();
            layoutParams.height = imageData.h;
            image.setLayoutParams(layoutParams);
        }
*/
        if(imageData!=null && imageData.url!=null && imageData.url.equals("")==false && imageData.isBroken==false)
        {
            final WeakReference<Image> img = new WeakReference(imageData);
            final WeakReference<Callback> call = new WeakReference(callback);
            final WeakReference<ImageView> imageView = new WeakReference(image);

            int placeholder= R.drawable.vector_icon_progress_animation;
            if(img.get().isLoaded)
                placeholder =defaultImageRes;
            Picasso.with(context)
                    .load(imageData.url).memoryPolicy(MemoryPolicy.NO_STORE)
                    .tag(context)
                    .placeholder(placeholder)
                    .error(defaultImageRes)
                    .fit()
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (img != null && img.get() != null) {
                                img.get().isLoaded = true;
                                if (imageView != null && imageView.get() != null) {
                                    img.get().w =imageView.get().getWidth();
                                    img.get().h =imageView.get().getHeight();
                                }
                            }

                            if (call != null && call.get() != null)
                                call.get().onSuccess();
                        }

                        @Override
                        public void onError() {
                            if (img != null && img.get() != null) {
                                img.get().isBroken = true;
                            }
                            if (call != null && call.get() != null)
                                call.get().onError();
                        }
                    });
        }
        else
            image.setImageResource(defaultImageRes);
    }


    public static void loadImageNew(Context context,/*Image imageData*/String url, ImageView image
            , int errorImageRes, int loaderImgRes) {
/*
        if(imageData.h!=-1)
        {
            ViewGroup.LayoutParams layoutParams=image.getLayoutParams();
            layoutParams.height = imageData.h;
            image.setLayoutParams(layoutParams);
        }
*/
        if(!TextUtils.isEmpty(url))
        {
            //final WeakReference<Image> img = new WeakReference(imageData);
            //final WeakReference<Callback> call = new WeakReference(callback);
            final WeakReference<ImageView> imageView = new WeakReference(image);

            int placeholder = -1;
            if (loaderImgRes != -1)
            placeholder= loaderImgRes;
            /*if(img.get().isLoaded)
                placeholder =defaultImageRes;*/
            RequestCreator reqCreator = Picasso.with(context)
                    .load(url)
                    .tag(context);
            if (placeholder != -1)
                reqCreator.placeholder(placeholder);
            reqCreator.error(errorImageRes).memoryPolicy(MemoryPolicy.NO_STORE).fit()
                    .into(image/*, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (img != null && img.get() != null) {
                                img.get().isLoaded = true;
                                if (imageView != null && imageView.get() != null) {
                                    img.get().w =imageView.get().getWidth();
                                    img.get().h =imageView.get().getHeight();
                                }
                            }

                            if (call != null && call.get() != null)
                                call.get().onSuccess();
                        }

                        @Override
                        public void onError() {
                            if (img != null && img.get() != null) {
                                img.get().isBroken = true;
                            }
                            if (call != null && call.get() != null)
                                call.get().onError();
                        }
                    }*/);
        }
        else
            image.setImageResource(errorImageRes);
    }

    public static void loadImage(Context context, Image img1, ImageView image,int defaultImageRes) {
        loadImage( context,  img1,  image,defaultImageRes,null);
    }


}
