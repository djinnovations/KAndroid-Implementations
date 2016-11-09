package com.goldadorn.main.dj.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.ViewHolder;
import com.goldadorn.main.dj.model.UserSession;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.modules.home.HomePage;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.utils.TypefaceHelper;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 19-10-2016.
 */
public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    final ArrayList<Product> products = new ArrayList<>();

    public WishlistAdapter() {
    }

    protected boolean isLikeList() {
        return false;
    }

    @Override
    public WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WishlistViewHolder vh = new WishlistViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_wishlayout_v2, parent, false)) {
        };
            /*vh.addToCart.setVisibility(View.INVISIBLE);
            //vh.addToCart.setOnClickListener(mAddToCartClick);
            vh.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            });*/
        //vh.itemView.setOnClickListener();
        if (isShowButtons())
            vh.bottomBtnHolder.setVisibility(View.VISIBLE);
        else vh.bottomBtnHolder.setVisibility(View.GONE);
        return vh;
    }

    /*View.OnClickListener cardClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    }*/

    protected boolean isShowButtons(){
        return true;
    }

    private void setUpLikeBtn(ImageView view) {
        int color = ResourceReader.getInstance(Application.getInstance()).getColorFromResource(R.color.votedColor);
        view.setImageDrawable(new IconicsDrawable(Application.getInstance())
                .icon(FontAwesome.Icon.faw_heart)
                .color(color)
                .sizeDp(20));
    }

    @Override
    public void onBindViewHolder(WishlistViewHolder holder, int position) {
        Product prod = products.get(position);
        holder.name.setText(prod.name);
        Picasso.with(Application.getInstance()).load(prod.getImageUrl(prod.userId, prod.defMetal, false, 200)).into(holder.image);
        if (isLikeList()){
            /*holder.tvMetal.setText("Brand: "+prod.desName);
            holder.tvDiamond.setText(prod.displayPrice);*/
            holder.tvMetal.setText("Brand: "+prod.desName);
            holder.likesHolder.setVisibility(View.VISIBLE);
            setUpLikeBtn(holder.ivHeart);
            holder.tvLikeCount.setText(String.valueOf(prod.likecount));
        }else {
            String discTxt = "";
            if (prod.discount > 0)
                discTxt = "Discount: "+String.valueOf(Math.round(prod.discount)) + "%off";
            else discTxt = "Discount: "+"0% off";
            holder.tvMetal.setText(discTxt);
            holder.likesHolder.setVisibility(View.GONE);
        }
        holder.tvDiamond.setText(prod.displayPrice);

        //holder.description.setText(p.description);
        //holder.addToCart.setTag(p);
        holder.delete.setTag(prod);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void changeData(ArrayList<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }


    public void addNewData(ArrayList<Product> products){
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    View.OnClickListener removeFrmWishlistClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (v.getTag() != null && v.getTag() instanceof Product) {
                Product p = (Product) v.getTag();
                //TODO kiran delete click
                UIController.deleteFromWhishlist(v.getContext(), p, new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        if (result.success) {
                            products.remove(v.getTag());
                            notifyDataSetChanged();
                            Toast.makeText(Application.getInstance(), "Product removed from Wishlist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };

    View.OnClickListener addtocartClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    class WishlistViewHolder extends ViewHolder implements View.OnClickListener{

        @Bind(R.id.tvProdName)
        TextView name;
        @Bind(R.id.tvMetal)
        TextView tvMetal;
        @Bind(R.id.tvDiamond)
        TextView tvDiamond;
        /*@Bind(R.id.tvSize)
        TextView tvSize;*/
        @Bind(R.id.likesHolder)
        View likesHolder;
        @Bind(R.id.tvLikeCount)
        TextView tvLikeCount;
        @Bind(R.id.ivHeart)
        ImageView ivHeart;
        /* @Bind(R.id.description)
         TextView description;*/
        @Bind(R.id.ivProd)
        ImageView image;

        /*@Bind(R.id.tvPositive)
        TextView addToCart;*/
        @Bind(R.id.tvNegative)
        TextView delete;
        @Bind(R.id.bottomBtnHolder)
        View bottomBtnHolder;

        public WishlistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            UiRandomUtils.setTypefaceBold(name);
            TypefaceHelper.setFont(tvMetal, tvDiamond, tvLikeCount, /*addToCart,*/ delete);
            //addToCart.setOnClickListener(addtocartClick);
            delete.setOnClickListener(removeFrmWishlistClick);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (true){
                SocialFeedFragment tempFeed = UserSession.getInstance().getSocialFeedFragment();
                if (tempFeed != null){
                    com.goldadorn.main.model.SocialPost post = new com.goldadorn.main.model.SocialPost();
                    post.setPostType(com.goldadorn.main.model.SocialPost.POST_TYPE_NORMAL_POST);
                    Product prod = products.get(getAdapterPosition());
                    post.setImage1loc(prod.getImageUrl(prod.userId, prod.defMetal, false, 200));
                    post.setIsLiked(0);
                    int likeCnt = 0;
                    try {
                        likeCnt = prod.likecount;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    post.setLikeCount(likeCnt);
                    tempFeed.zoomImages(post, 0);
                }
            }
        }
    }
}
