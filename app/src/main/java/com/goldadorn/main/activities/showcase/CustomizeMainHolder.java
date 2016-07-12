package com.goldadorn.main.activities.showcase;

import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.ViewHolder;
import com.goldadorn.main.dj.model.Swatches;
import com.goldadorn.main.dj.uiutils.DisplayProperties;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.model.OptionKey;
import com.goldadorn.main.model.OptionValue;
import com.goldadorn.main.utils.URLHelper;
import com.rey.material.widget.CircleCheckedTextView;
import com.squareup.picasso.Picasso;

import org.github.images.CircularImageView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CustomizeMainHolder extends ViewHolder {

    public final TextView name;
    public final TextView extra;
    public final ImageView image;
    public ImageButton addRemoveButton;
    private View separator;
    private LinearLayout extraLayout;

    //private IResultListener<Map.Entry<OptionKey, OptionValue>> listener;
    private IResultListener<Map.Entry<OptionKey, Swatches.MixedSwatch>> listener;

    //private Map.Entry<OptionKey, ArrayList<OptionValue>> data;
    private Map.Entry<OptionKey, ArrayList<Swatches.MixedSwatch>> data;

    private void changeDrawable(boolean isAdd) {
        ResourceReader rsrdr = ResourceReader.getInstance(extraLayout.getContext());
        Drawable drawable;
        //Drawable addDrawable = rsrdr.getDrawableFromResId(R.drawable.add);;
        if (isAdd) {
            drawable = rsrdr.getDrawableFromResId(R.drawable.add);
        } else {
            drawable = rsrdr.getDrawableFromResId(R.drawable.minus);
            //drawable.setBounds(addDrawable.copyBounds());
        }
        drawable.setColorFilter(rsrdr.getColorFromResource(R.color.White),
                PorterDuff.Mode.SRC_ATOP);
        addRemoveButton.setImageDrawable(drawable);
    }


    /*private Rect getBound(){
        DisplayProperties displayProperties = DisplayProperties.getInstance(Application.getInstance()
                , DisplayProperties.ORIENTATION_PORTRAIT);
        int w = addRemoveButton.getWidth();
        int h = d.getIntrinsicHeight();
        int left = x - w / 2;
        int top = y - h / 2;
        int right = left + w;
        int bottom = top + h;
        Rect rect = new Rect();
    }*/

    public CustomizeMainHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        extra = (TextView) itemView.findViewById(R.id.extra);
        image = (ImageView) itemView.findViewById(R.id.image);
        addRemoveButton = (ImageButton) itemView.findViewById(R.id.addRemoveButton);
        separator = itemView.findViewById(R.id.separator);
        extraLayout = (LinearLayout) itemView.findViewById(R.id.extra_layout);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (extraLayout.getVisibility() == View.VISIBLE) {
                    extraLayout.setVisibility(View.GONE);
                } else {
                    extraLayout.setVisibility(View.VISIBLE);
                    populateExtraLayout();
                }*/
            }
        });

        addRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extraLayout.getVisibility() == View.VISIBLE) {
                    extraLayout.setVisibility(View.GONE);
                    separator.setVisibility(View.VISIBLE);
                    changeDrawable(true);
                } else {
                    extraLayout.setVisibility(View.VISIBLE);
                    separator.setVisibility(View.INVISIBLE);
                    changeDrawable(false);
                    populateExtraLayout();
                }
            }
        });

    }

    /*public void bindUI(Map.Entry<OptionKey, ArrayList<OptionValue>> option) {*/
    public void bindUI(Map.Entry<OptionKey, ArrayList<Swatches.MixedSwatch>> option) {
        data = option;
        name.setText(option.getKey().getDisplayString());
    }


    public interface IResultListener<R> {
        void onResult(R result);
    }

    /*public void setOptionSelectedListener(IResultListener<Map.Entry<OptionKey, OptionValue>> listener) {*/
    public void setOptionSelectedListener(IResultListener<Map.Entry<OptionKey, Swatches.MixedSwatch>> listener) {
        this.listener = listener;
    }

    private View.OnClickListener mItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CircularImageView imageView = (CircularImageView) v.findViewById(R.id.image);
            image.setImageDrawable(imageView.getDrawable());
            if (listener != null)
                ///listener.onResult(new AbstractMap.SimpleEntry<>(data.getKey(), (OptionValue) v.getTag()));
                listener.onResult(new AbstractMap.SimpleEntry<>(data.getKey(), (Swatches.MixedSwatch) v.getTag()));
        }
    };


    private void populateExtraLayout() {
        //List<OptionValue> list = data.getValue();
        List<Swatches.MixedSwatch> list = data.getValue();
        for (int i = 0; i < list.size(); i++) {
            //OptionValue s = list.get(i);
            Swatches.MixedSwatch swatch = list.get(i);
            View child = extraLayout.getChildAt(i);
            View v = LayoutInflater.from(extraLayout.getContext()).inflate(R.layout.item_customize_type, extraLayout, false);
            //v.setTag(s);
            v.setTag(swatch);
            v.setOnClickListener(mItemClick);
            TextView tv = (TextView) v.findViewById(R.id.name);
            /*CircleCheckedTextView image = (CircleCheckedTextView) v.findViewById(R.id.image);
            image.setText(s.getDisplayString());*/
            CircularImageView imageView = (CircularImageView) v.findViewById(R.id.image);
            if (data.getKey().keyID.equalsIgnoreCase("metal"))
                imageView.setImageResource(swatch.getSwatchDisplayIconResId());
            else {
                Glide.with(v.getContext())
                        //.load("http://buisnessofjewelry.com/images/passionfire-round_small.png")
                        .load("http://www.pngall.com/wp-content/uploads/2016/04/Diamond-PNG-Picture.png")
                        .centerCrop()
                        //.placeholder(R.drawable.vector_image_place_holder_profile_dark)
                        .crossFade()
                        .into(imageView);
            }
            /*if(data.getKey().keyID.equalsIgnoreCase("Metal Purity List"))
                tv.setText(s.getDisplayString()+"K");
            else
                tv.setText(s.getDisplayString());*/
            tv.setText(swatch.getSwatchDisplayTxt());
            v.setVisibility(View.VISIBLE);
            if (child == null) {
                extraLayout.addView(v);
            }
        }
        if (extraLayout.getChildCount() > list.size()) {
            for (int i = list.size(); i < extraLayout.getChildCount(); i++) {
                extraLayout.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }


}