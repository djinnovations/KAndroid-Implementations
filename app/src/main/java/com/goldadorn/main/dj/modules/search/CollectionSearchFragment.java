package com.goldadorn.main.dj.modules.search;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
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
public class CollectionSearchFragment extends BaseSearchFragment{

    private static final String TAG = "CollSearchFragment";
    private int offset;

    @Override
    public String getNextDataURL(PageData data) {
        return ApiKeys.getSearchAPI(false);
    }

    //"tag": "bang", "prodOffset": 0, "prod" :1, "collOffset": 0, "coll" :1
    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put("coll", "1");
        params.put("tag", ((SearchActivity) getActivity()).getTag());
        params.put("collOffset", String.valueOf(offset));
        Log.d(TAG, "searchcolltab- req params: " + params);
        return params;
    }

    public Class getLoadedDataParsingAwareClass() {
        return CollectionResults.class;
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
        protected void updatePagingData(BaseDataParser loadedDataVO) {
            try {
                if (loadedDataVO != null && loadedDataVO instanceof CollectionResults) {
                    CollectionResults result = (CollectionResults) loadedDataVO;
                    if (result.collOffset != -1 && offset != result.collOffset) {
                        offset = result.collOffset;
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
        return new CollectionViewHolder(view);
    }

    class CollectionViewHolder extends BaseSearchViewHolder{

        SearchDataObject.CollectionSearchData searchDataObject;
        public CollectionViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void updateItemView(Object obj, View view, int i) {
            //super.updateItemView(obj, view, i);
            searchDataObject = (SearchDataObject.CollectionSearchData) obj;
            userName.setText(searchDataObject.getCollectionTitle());
            designer.setText("By "+searchDataObject.getDesgnName());
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

    public static class CollectionResults extends CodeDataParser {
        private List<SearchDataObject.CollectionSearchData> collList;
        public int collOffset;

        public List<?> getList() {
            return collList;
        }

        public Object getData() {
            return this;
        }

        public void setData(Object data) {
            this.collList = (List<SearchDataObject.CollectionSearchData>) data;
        }
    }
}
