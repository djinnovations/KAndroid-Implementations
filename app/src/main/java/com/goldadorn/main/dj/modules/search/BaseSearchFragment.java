package com.goldadorn.main.dj.modules.search;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.goldadorn.main.modules.people.DividerDecoration;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 20-10-2016.
 */
public abstract class BaseSearchFragment extends DefaultVerticalListView{

        /*public abstract void getQueryUrl();

    public abstract void getListTitle();*/

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.)
        //return super.onCreateView(inflater, container, savedInstanceState);
    }*/

    public static class ViewTypes {
        static final int VIEW_SEARCH_ITEM = 1111;
    }

    public int getListItemViewType(int position, Object item) {
        return ViewTypes.VIEW_SEARCH_ITEM;
    }

    public View getItemView(int viewType, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.adapter_base_search, container, false);
    }

    @Override
    public void onItemClick(Object baseObject) {
        super.onItemClick(baseObject);
    }

    @Override
    public void onViewCreated(View view) {
        //super.onViewCreated(view);
        colorList = new ArrayList<>();
        int[] colors = Application.getInstance().getResources().getIntArray(R.array.colorsBank);
        Integer[] temp = new Integer[colors.length];
        int i = 0;
        for (int col : colors) {
            temp[i] = col;
            i++;
        }
        colorList.addAll(Arrays.asList(temp));
        Collections.shuffle(colorList);
    }

    protected void configDataManager(DataManager dataManager) {
        super.configDataManager(dataManager);
        dataManager.setRefreshEnabled(true);
    }

    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DividerDecoration(this.getActivity());
    }


    public BaseItemHolder getItemHolder(int viewType, View view) {
        return new BaseSearchViewHolder(view);
    }

    List<Integer> colorList;

    public class BaseSearchViewHolder extends BaseItemHolder {

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

        SearchDataObject.UserSearchData searchDataObject;


        public BaseSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            TypefaceHelper.setFont(tvName, userName, designer);
        }

        @Override
        public void updateItemView(Object obj, View view, int i) {
            searchDataObject = (SearchDataObject.UserSearchData) obj;
            userName.setText(searchDataObject.getUsername());
            if (!TextUtils.isEmpty(searchDataObject.getProfilePic())) {
                userImage.setVisibility(View.VISIBLE);
                llNameHolder.setVisibility(View.INVISIBLE);
                Picasso.with(getContext())
                        .load(searchDataObject.getProfilePic())
                        .placeholder(R.drawable.vector_image_place_holder_profile)
                        .tag(getContext())
                        .resize(100, 100)
                        .into(userImage);
            } else {
                userImage.setVisibility(View.INVISIBLE);
                llNameHolder.setVisibility(View.VISIBLE);
                try {
            /*Glide.with(holder.itemView.getContext())
                    .load(ImageFilePath.getImageUrlForProduct(listOfItems.get(position).getDesgnId(),
                            listOfItems.get(position).getProductId(), null, false))
                    .placeholder(R.drawable.vector_image_logo_square_100dp)
                    .into(holder.ivProd);*/
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
                }
            }
        }
    }


}
