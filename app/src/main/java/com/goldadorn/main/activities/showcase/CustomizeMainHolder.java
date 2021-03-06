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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.ViewHolder;
import com.goldadorn.main.dj.model.Swatches;
import com.goldadorn.main.dj.uiutils.DisplayProperties;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.model.OptionKey;
import com.goldadorn.main.model.OptionValue;
import com.goldadorn.main.model.ProductOptions;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.utils.URLHelper;
import com.google.repacked.apache.commons.lang3.ArrayUtils;
import com.rey.material.widget.CircleCheckedTextView;
import com.squareup.picasso.MemoryPolicy;
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
    public ImageView addRemoveButton;
    private View separator;
    private LinearLayout extraLayout;

    //private IResultListener<Map.Entry<OptionKey, OptionValue>> listener;
    private IResultListener<Map.Entry<OptionKey, Swatches.MixedSwatch>> listener;

    //private Map.Entry<OptionKey, ArrayList<OptionValue>> data;
    private Map.Entry<OptionKey, ArrayList<Swatches.MixedSwatch>> data;

    private void changeDrawable(boolean isAdd) {
        ResourceReader rsrdr = ResourceReader.getInstance(extraLayout.getContext());
        Drawable drawable;
        if (isAdd) {
            drawable = rsrdr.getDrawableFromResId(R.drawable.add);
            addRemoveButton.setPadding(defaultRect.left, defaultRect.top, defaultRect.right, defaultRect.bottom);
        } else {
            drawable = rsrdr.getDrawableFromResId(R.drawable.minus);
            addRemoveButton.setPadding(rectForMinusSym.left, rectForMinusSym.top, rectForMinusSym.right, rectForMinusSym.bottom);
        }
        drawable.setColorFilter(rsrdr.getColorFromResource(R.color.White),
                PorterDuff.Mode.SRC_ATOP);
        addRemoveButton.setImageDrawable(drawable);
    }

    private Rect rectForMinusSym;

    private void setPaddedRect() {
        DisplayProperties displayProperties = DisplayProperties.getInstance(name.getContext(), DisplayProperties.ORIENTATION_PORTRAIT);
        int paddInPx = (int) (1.5 * displayProperties.getXPixelsPerCell());
        rectForMinusSym = new Rect(paddInPx, paddInPx, paddInPx, paddInPx);
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

    private Rect defaultRect;

    public CustomizeMainHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        extra = (TextView) itemView.findViewById(R.id.extra);
        image = (ImageView) itemView.findViewById(R.id.image);
        addRemoveButton = (ImageView) itemView.findViewById(R.id.addRemoveButton);
        defaultRect = new Rect(addRemoveButton.getPaddingLeft(), addRemoveButton.getPaddingTop()
                , addRemoveButton.getPaddingRight(), addRemoveButton.getPaddingBottom());
        setPaddedRect();
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
                /*if (true) {
                    Toast.makeText(v.getContext(), "Feature Coming Soon!", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                //// TODO: 16-07-2016  
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

        setFontTypeface();
    }

    private void setFontTypeface(){
        TypefaceHelper.setFont(name, extra);
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
            /*Circular*/ImageView imageView = (/*Circular*/ImageView) v.findViewById(R.id.image);
            Swatches.MixedSwatch swatch = ((Swatches.MixedSwatch) v.getTag());
            extra.setVisibility(View.GONE);
            if (swatch.getWHICH_TYPE() == Swatches.TYPE_GEMSTONE) {
                extra.setVisibility(View.VISIBLE);
                extra.setText(swatch.getSwatchDisplayTxt());
            }
            image.setImageDrawable(imageView.getDrawable());
            if (listener != null)//// TODO: 16-07-2016  uncomment this block
                ///listener.onResult(new AbstractMap.SimpleEntry<>(data.getKey(), (OptionValue) v.getTag()));
                listener.onResult(new AbstractMap.SimpleEntry<>(data.getKey(), swatch));
        }
    };


    //public void disable


    private void populateExtraLayout() {
        //List<OptionValue> list = data.getValue();
        List<Swatches.MixedSwatch> list = data.getValue();
        for (int i = 0; i < list.size(); i++) {
            //OptionValue s = productInfoList.get(i);
            Swatches.MixedSwatch swatch = list.get(i);
            View child = extraLayout.getChildAt(i);
            View v = LayoutInflater.from(extraLayout.getContext()).inflate(R.layout.item_customize_type, extraLayout, false);
            //v.setTag(s);
            v.setTag(swatch);
            v.setOnClickListener(mItemClick);
            TextView tv = (TextView) v.findViewById(R.id.name);
            TypefaceHelper.setFont(name);
            /*CircleCheckedTextView image = (CircleCheckedTextView) v.findViewById(R.id.image);
            image.setText(s.getDisplayString());*/
            /*Circular*/ImageView imageView = (/*Circular*/ImageView) v.findViewById(R.id.image);
            if (data.getKey().keyID.equalsIgnoreCase("metal"))
                imageView.setImageResource(swatch.getSwatchDisplayIconResId());
            else {
                Picasso.with(v.getContext())
                        //.load("http://buisnessofjewelry.com/images/passionfire-round_small.png")
                        //.load(UiRandomUtils.DIAMOND_URL)
                        .load(ProductOptions.getDiaQualityResId(swatch.getSwatchDisplayTxt()))
                        .centerCrop()
                        .fit().memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                        //.placeholder(R.drawable.vector_image_place_holder_profile_dark)
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

        //int j = 0;
        if (disableList != null){
            for (int i = 0; i< disableList.length; i++){
                /*if (disableList[j] == i){*/
                    extraLayout.getChildAt(disableList[i]).setVisibility(View.GONE);
                    //j++;
                //}
            }
        }
    }

    public void clearCurrentSelection(){
        image.setImageDrawable(null);
        image.setVisibility(View.GONE);
        extra.setText("");
        extra.setVisibility(View.GONE);
    }

    private int[] disableList;

    public void setDisableList(List<Integer> disableList){
        this.disableList = buildIntArray(disableList);
    }

    private int[] buildIntArray(List<Integer> integers) {
        int[] ints = new int[integers.size()];
        int i = 0;
        for (Integer n : integers) {
            ints[i] = n;
            i++;
        }
        return ints;
    }

}