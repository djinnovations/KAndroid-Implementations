package com.goldadorn.main.bindings;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.squareup.picasso.Picasso;

import org.github.images.CircularImageView;

import java.util.Random;

/**
 * Created by BhavinPadhiyar on 18/03/16.
 */
public class ImageBindings {

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String imageUrl) {
        if(imageUrl!=null && imageUrl.trim().equals("")==false)
            Picasso.with(imageView.getContext()).load(imageUrl).into(imageView);
    }
    @BindingAdapter({"imageUrl","placeHolder"})
    public static void loadImage(ImageView imageView, String imageUrl,Drawable placeHolder) {
        if(imageUrl!=null && imageUrl.trim().equals("")==false)
            Picasso.with(imageView.getContext()).load(imageUrl).placeholder(placeHolder).into(imageView);
        else {
            if(imageView instanceof CircularImageView)
            {
                CircularImageView circularImageView= (CircularImageView) imageView;
                int[] androidColors = imageView.getContext().getResources().getIntArray(R.array.colorsBank);
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                circularImageView.setBorderColor(randomAndroidColor);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setImageDrawable(placeHolder);
            }
            else {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageDrawable(placeHolder);
            }

        }
    }

    @BindingAdapter({"imageUrl","placeHolder","brandColor"})
    public static void loadImage(ImageView imageView, String imageUrl,Drawable placeHolder,int brandColor) {
        if(imageUrl!=null && imageUrl.trim().equals("")==false)
            Picasso.with(imageView.getContext()).load(imageUrl).placeholder(placeHolder).into(imageView);
        else {
            if(imageView instanceof CircularImageView)
            {
                CircularImageView circularImageView= (CircularImageView) imageView;
                if(brandColor!=-1)
                    circularImageView.setBorderColor(brandColor);
            }
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageDrawable(placeHolder);
        }
    }

    @BindingAdapter({"imageUrl","placeHolder","brandColor","randomColor"})
    public static void loadImage(ImageView imageView, String imageUrl,Drawable placeHolder,int brandColor,int randomColor) {
        if(imageUrl!=null && imageUrl.trim().equals("")==false)
            Picasso.with(imageView.getContext()).load(imageUrl).placeholder(placeHolder).into(imageView);
        else {
            if(imageView instanceof CircularImageView)
            {
                CircularImageView circularImageView= (CircularImageView) imageView;
                if(brandColor!=-1)
                    circularImageView.setBorderColor(brandColor);
                if(randomColor!=-1)
                    circularImageView.setBorderColor(randomColor);

            }
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageDrawable(placeHolder);
        }
    }
}
