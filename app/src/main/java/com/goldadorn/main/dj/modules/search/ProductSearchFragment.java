package com.goldadorn.main.dj.modules.search;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.model.UserSession;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 25-10-2016.
 */
public class ProductSearchFragment extends BaseSearchFragment {


    private static final String TAG = "ProdSearchFragment";
    private int offset;

    @Override
    public String getNextDataURL(PageData data) {
        return ApiKeys.getSearchAPI(false);
    }

    //"tag": "bang", "prodOffset": 0, "prod" :1, "collOffset": 0, "coll" :1
    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put("prod", "1");
        params.put("tag", ((SearchActivity) getActivity()).getTag());
        params.put("prodOffset", String.valueOf(offset));
        Log.d(TAG, "searchprodtab- req params: " + params);
        return params;
    }

    @Override
    public void onItemClick(Object baseObject) {
        //super.onItemClick(baseObject);

        SocialFeedFragment tempFeed = UserSession.getInstance().getSocialFeedFragment();
        if (tempFeed != null) {
            com.goldadorn.main.model.SocialPost post = new com.goldadorn.main.model.SocialPost();
            post.setPostType(com.goldadorn.main.model.SocialPost.POST_TYPE_NORMAL_POST);
            SearchDataObject.ProductSearchData prod = (SearchDataObject.ProductSearchData) baseObject;
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

        @Override
        public String getRefreshDataURL (PageData pageData){
            return getNextDataURL(null);
        }


    public void callLoad() {
        getDataManager().removeAll(getDataManager());
        loadRefreshData();
    }


    public Class getLoadedDataParsingAwareClass() {
        return ProductResult.class;
    }

    @Override
    protected DataManager createDataManager() {
        return new DefaultProjectDataManager1(getActivity(), this, getApp().getCookies());
    }


    private class DefaultProjectDataManager1 extends com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager {
        public DefaultProjectDataManager1(Context context, IDataManagerDelegate delegate, List<Cookie> cookies) {
            super(context, delegate, cookies);
        }

        public Map<String, Object> getNextDataServerCallParams(PageData data) {
            return getNextDataParams(data);
        }

        @Override
        public Map<String, Object> getRefreshDataServerCallParams(PageData data) {
            offset = 0;
            data.curruntPage = 1;
            data.totalPage = 2;
            return getNextDataParams(data);
        }

        @Override
        protected void updatePagingData(BaseDataParser loadedDataVO) {
            try {
                if (loadedDataVO != null && loadedDataVO instanceof ProductResult) {
                    ProductResult result = (ProductResult) loadedDataVO;
                    if (result.prodOffset != -1 && offset != result.prodOffset) {
                        offset = result.prodOffset;
                        pageData.curruntPage += 1;
                        pageData.totalPage += 1;
                    } else {
                        pageData.totalPage = pageData.curruntPage;
                    }
                } else {
                    pageData.totalPage = pageData.curruntPage;
                }
            } catch (Exception e) {
                pageData.curruntPage = pageData.totalPage = 1;
            }
        }

    }


    @Override
    public BaseItemHolder getItemHolder(int viewType, View view) {
        return new ProductViewHolder(view);
    }

    class ProductViewHolder extends BaseSearchViewHolder {

        SearchDataObject.ProductSearchData searchDataObject;

        public ProductViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void updateItemView(Object obj, View view, int i) {
            //super.updateItemView(obj, view, i);
            searchDataObject = (SearchDataObject.ProductSearchData) obj;
            userName.setText(searchDataObject.getProductLabel());
            designer.setText("By " + searchDataObject.getDesgnName());
            if (!TextUtils.isEmpty(searchDataObject.getImageUrl())) {
                userImage.setVisibility(View.VISIBLE);
                llNameHolder.setVisibility(View.INVISIBLE);
                Picasso.with(getContext())
                        .load(searchDataObject.getImageUrl())
                        .placeholder(R.drawable.vector_image_place_holder_profile)
                        .tag(getContext())
                        .resize(100, 100)
                        .into(userImage);
            } else {
                userImage.setVisibility(View.INVISIBLE);
                llNameHolder.setVisibility(View.VISIBLE);
                /*try {
            *//*Glide.with(holder.itemView.getContext())
                    .load(ImageFilePath.getImageUrlForProduct(listOfItems.get(position).getDesgnId(),
                            listOfItems.get(position).getProductId(), null, false))
                    .placeholder(R.drawable.vector_image_logo_square_100dp)
                    .into(holder.ivProd);*//*
                    Drawable mDrawable = ResourceReader.getInstance(Application.getInstance())
                            .getDrawableFromResId(R.drawable.circle_bg_for_price);
                    int rand = RandomUtils.randInt(0, 9);
                    mDrawable.setColorFilter(new PorterDuffColorFilter(colorList.get(rand), PorterDuff.Mode.SRC));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        llNameHolder.setBackground(mDrawable);
                    } else llNameHolder.setBackgroundDrawable(mDrawable);
                    String name = searchDataObject.getUsername();
                    if (!TextUtils.isEmpty(name)) {
                        String[] initial = searchDataObject.getUsername().trim().split(" ");
                        if (initial.length > 0) {
                            if (!TextUtils.isEmpty(initial[0])) {
                                name = String.valueOf(initial[0].trim().charAt(0));
                            }
                            if (initial.length > 1) {
                                if (!TextUtils.isEmpty(initial[1])) {
                                    name = name + String.valueOf(initial[1].trim().charAt(0));
                                }
                            }
                        }
                        tvName.setText(name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    public static class ProductResult extends CodeDataParser {
        private List<SearchDataObject.ProductSearchData> prodList;
        public int prodOffset;

        public List<?> getList() {
            return prodList;
        }

        public Object getData() {
            return this;
        }

        public void setData(Object data) {
            this.prodList = (List<SearchDataObject.ProductSearchData>) data;
        }
    }
}
