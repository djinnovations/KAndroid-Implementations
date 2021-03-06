package com.goldadorn.main.dj.modules.search;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.model.UserSession;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.TypefaceHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 26-10-2016.
 */
public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.BaseHolder> {

    List<SearchDataObject.ProductSearchData> dataList = new ArrayList<>();

    public void changeDataProd(List<SearchDataObject.ProductSearchData> dataList){
        this.dataList = new ArrayList<>(dataList);
        this.notifyDataSetChanged();
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_base_search, parent, false);
        return new BaseHolder(view);
    }

    @Override
    public int getItemCount() {
        if (dataList.size() > 2)
            return 2;
        return dataList.size();
    }


    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        //super.onBindViewHolder(holder, position);
        SearchDataObject.ProductSearchData searchDataObject = dataList.get(position);
        holder.userName.setText(searchDataObject.getProductLabel());
        holder.designer.setText("By " + searchDataObject.getDesgnName());
        if (!TextUtils.isEmpty(searchDataObject.getImageUrl())) {
            holder.userImage.setVisibility(View.VISIBLE);
            holder.llNameHolder.setVisibility(View.INVISIBLE);
            Picasso.with(holder.itemView.getContext())
                    .load(searchDataObject.getImageUrl())
                    .placeholder(R.drawable.vector_image_place_holder_profile)
                    .tag(holder.itemView.getContext())
                    .resize(100, 100)
                    .into(holder.userImage);
        } else {
            holder.userImage.setVisibility(View.INVISIBLE);
            holder.llNameHolder.setVisibility(View.VISIBLE);
        }

    }


    class BaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.llNameHolder)
        View llNameHolder;
        @Bind(R.id.tvName)
        TextView tvName;
        @Bind(R.id.userImage)
        ImageView userImage;

        @Bind(R.id.userName)
        TextView userName;
        @Bind(R.id.designer)
        TextView designer;

        public BaseHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            TypefaceHelper.setFont(tvName, userName, designer);
        }


        @Override
        public void onClick(View v) {
            SocialFeedFragment tempFeed = UserSession.getInstance().getSocialFeedFragment();
            if (tempFeed != null) {
                com.goldadorn.main.model.SocialPost post = new com.goldadorn.main.model.SocialPost();
                post.setPostType(com.goldadorn.main.model.SocialPost.POST_TYPE_NORMAL_POST);
                SearchDataObject.ProductSearchData prod = dataList.get(getAdapterPosition());
                post.setImage1loc(prod.getImageUrl());
                post.setIsLiked(0);
                int likeCnt = 0;
                try {
                    likeCnt = 0;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                post.setLikeCount(likeCnt);
                tempFeed.zoomImages(post, 0);
            }
        }
    }
}
