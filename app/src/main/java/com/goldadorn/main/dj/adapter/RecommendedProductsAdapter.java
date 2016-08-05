package com.goldadorn.main.dj.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.model.RecommendedProduct;
import com.goldadorn.main.model.Image;
import com.goldadorn.main.utils.ImageFilePath;
import com.goldadorn.main.utils.ImageLoaderUtils;
import com.goldadorn.main.utils.TypefaceHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 29-07-2016.
 */
public class RecommendedProductsAdapter extends RecyclerView.Adapter<RecommendedProductsAdapter.ImageViewHolder>{

    public interface RecommendedPostClick{
        void onRecommendedPostClick(String url, int isLiked, int likeCounts);
    }

    private List<RecommendedProduct> listOfRecoProds;
    RecommendedPostClick itemClick;

    public RecommendedProductsAdapter(List<RecommendedProduct> listOfRecoProds, RecommendedPostClick itemClick) {
        this.listOfRecoProds = listOfRecoProds;
        this.itemClick = itemClick;
    }

    public void addList(List<RecommendedProduct> listOfUrl){
        this.listOfRecoProds = listOfUrl;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageViewHolder imageViewHolder = new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_recommended_products, parent, false));
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        /*Glide.with(holder.itemView.getContext()).load(listOfUrl.get(position))
                .into(holder.ivProd);*/
        ImageLoaderUtils.loadImage(Application.getInstance()
                , new Image(ImageFilePath.getImageUrlForProduct(listOfRecoProds.get(position).getProductId())),
                holder.ivProd, R.drawable.vector_image_logo_square_100dp);
        holder.tvName.setText("Product Name");
    }

    @Override
    public int getItemCount() {
        return listOfRecoProds.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.image)
        ImageView ivProd;
        @Bind(R.id.tvName)
        TextView tvName;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivProd.setOnClickListener(this);
            TypefaceHelper.setFont(tvName);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image) {
                itemClick.onRecommendedPostClick(ImageFilePath.getImageUrlForProduct(listOfRecoProds.get(getAdapterPosition())
                        .getProductId()), listOfRecoProds.get(getAdapterPosition())
                        .getIsLiked(), listOfRecoProds.get(getAdapterPosition())
                        .getLikeCount());
            }
        }
    }

}
