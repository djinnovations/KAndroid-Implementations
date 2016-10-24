package com.goldadorn.main.dj.adapter;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.utils.TypefaceHelper;
import com.google.repacked.apache.commons.lang3.ArrayUtils;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 29-07-2016.
 */
public class TrendingHashTagAdapter extends RecyclerView.Adapter<TrendingHashTagAdapter.ImageViewHolder> {

    public interface HashtagClick {
        void onItemClick(String hashtag);
    }

    private List<String> listOfItems;
    private HashtagClick itemClick;
    List<Integer> colorList;

    public TrendingHashTagAdapter(List<String> listOfItems, HashtagClick itemClick) {
        this.listOfItems = listOfItems;
        this.itemClick = itemClick;
        colorList = new ArrayList<>();
    }

    public void addList(List<String> listOfUrl) {
        this.listOfItems = new ArrayList<>(listOfUrl);
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageViewHolder imageViewHolder = new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_hashtag, parent, false));
        int[] colors = parent.getContext().getResources().getIntArray(R.array.colorsBank);
        Integer[] temp = new Integer[colors.length];
        int i = 0;
        for (int col : colors) {
            temp[i] = col;
            i++;
        }
        colorList.addAll(Arrays.asList(temp));
        Collections.shuffle(colorList);
        return imageViewHolder;
    }


    private void setUpLikeBtn(ImageView view) {
        int color = ResourceReader.getInstance(Application.getInstance()).getColorFromResource(R.color.votedColor);
        view.setImageDrawable(new IconicsDrawable(Application.getInstance())
                .icon(FontAwesome.Icon.faw_heart)
                .color(color)
                .sizeDp(20));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        /*Glide.with(holder.itemView.getContext()).load(listOfUrl.get(position))
                .into(holder.ivProd);*/
        try {
            /*Glide.with(holder.itemView.getContext())
                    .load(ImageFilePath.getImageUrlForProduct(listOfItems.get(position).getDesgnId(),
                            listOfItems.get(position).getProductId(), null, false))
                    .placeholder(R.drawable.vector_image_logo_square_100dp)
                    .into(holder.ivProd);*/
            Drawable mDrawable = ResourceReader.getInstance(Application.getInstance())
                    .getDrawableFromResId(R.drawable.circle_bg_for_price);
            if (position > 9) {
                int rand = RandomUtils.randInt(0, 9);
                mDrawable.setColorFilter(new PorterDuffColorFilter(colorList.get(rand), PorterDuff.Mode.SRC));
            }else mDrawable.setColorFilter(new PorterDuffColorFilter(colorList.get(position), PorterDuff.Mode.SRC));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.rlHolder.setBackground(mDrawable);
            }else holder.rlHolder.setBackgroundDrawable(mDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //setUpLikeBtn(holder.ivHeart);
        holder.tvName.setText(String.valueOf(listOfItems.get(position)));
    }

    @Override
    public int getItemCount() {
        return listOfItems.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.tvName)
        TextView tvName;
        @Bind(R.id.rlHolder)
        RelativeLayout rlHolder;
        /*@Bind(R.id.ivHeart)
        ImageView ivHeart;*/

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            UiRandomUtils.setTypefaceBold(tvName);
            rlHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.rlHolder) {
                itemClick.onItemClick(listOfItems.get(getAdapterPosition()));
            }
        }
    }

}
