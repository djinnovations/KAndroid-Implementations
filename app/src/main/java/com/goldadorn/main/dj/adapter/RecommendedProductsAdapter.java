package com.goldadorn.main.dj.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.model.RecommendedProduct;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.model.Image;
import com.goldadorn.main.utils.ImageFilePath;
import com.goldadorn.main.utils.ImageLoaderUtils;
import com.goldadorn.main.utils.TypefaceHelper;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 29-07-2016.
 */
public class RecommendedProductsAdapter extends RecyclerView.Adapter<RecommendedProductsAdapter.ImageViewHolder> {

    public interface RecommendedPostClick {
        void onRecommendedPostClick(String url, int isLiked, int likeCounts);
    }

    private List<RecommendedProduct> listOfRecoProds;
    RecommendedPostClick itemClick;

    public RecommendedProductsAdapter(List<RecommendedProduct> listOfRecoProds, RecommendedPostClick itemClick) {
        this.listOfRecoProds = listOfRecoProds;
        this.itemClick = itemClick;
    }

    public void addList(List<RecommendedProduct> listOfUrl) {
        this.listOfRecoProds = listOfUrl;
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageViewHolder imageViewHolder = new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_recommended_products, parent, false));
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
                    .load(ImageFilePath.getImageUrlForProduct(listOfRecoProds.get(position).getDesgnId(),
                            listOfRecoProds.get(position).getProductId(), null, false))
                    .placeholder(R.drawable.vector_image_logo_square_100dp)
                    .into(holder.ivProd);*/
            ImageLoaderUtils.loadImage(Application.getInstance()
                    , new Image(ImageFilePath.getImageUrlForProduct(listOfRecoProds.get(position).getDesgnId(),
                            listOfRecoProds.get(position).getProductId(), null, false, 200)),
                    holder.ivProd, R.drawable.vector_image_logo_square_100dp, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpLikeBtn(holder.ivHeart);
        holder.tvLikeCount.setText(String.valueOf(listOfRecoProds.get(position).getLikeCount()));
    }

    @Override
    public int getItemCount() {
        return listOfRecoProds.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.image)
        ImageView ivProd;
        @Bind(R.id.tvLikeCount)
        TextView tvLikeCount;
        @Bind(R.id.ivHeart)
        ImageView ivHeart;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivProd.setOnClickListener(this);
            TypefaceHelper.setFont(tvLikeCount);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image) {
                itemClick.onRecommendedPostClick(
                        ImageFilePath.getImageUrlForProduct(listOfRecoProds.get(getAdapterPosition()).getDesgnId(),
                                listOfRecoProds.get(getAdapterPosition()).getProductId(), null, false, 200),
                        listOfRecoProds.get(getAdapterPosition()).getIsLiked(),
                        listOfRecoProds.get(getAdapterPosition()).getLikeCount());
            }
        }
    }

}
