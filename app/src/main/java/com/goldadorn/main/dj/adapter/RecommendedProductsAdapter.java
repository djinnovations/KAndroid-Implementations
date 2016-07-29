package com.goldadorn.main.dj.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.goldadorn.main.R;
import com.goldadorn.main.assist.RecyclerAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 29-07-2016.
 */
public class RecommendedProductsAdapter extends RecyclerView.Adapter<RecommendedProductsAdapter.ImageViewHolder>{

    private List<String> listOfUrl;

    public RecommendedProductsAdapter(List<String> listOfUrl) {
        this.listOfUrl = listOfUrl;
    }

    public void addList(List<String> listOfUrl){
        this.listOfUrl = listOfUrl;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageViewHolder imageViewHolder = new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_recommended_products, parent, false));
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(listOfUrl.get(position)).into(
                (holder.ivProd));
    }

    @Override
    public int getItemCount() {
        return listOfUrl.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.image)
        ImageView ivProd;
        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
