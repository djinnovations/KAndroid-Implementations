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
import com.goldadorn.main.utils.TypefaceHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 26-10-2016.
 */
public class DesignerSearchAdapter extends RecyclerView.Adapter<DesignerSearchAdapter.BaseHolder>{

    protected List<SearchDataObject.UserSearchData> dataList = new ArrayList<>();
    EverythingSearchFragment.ItemSelectListener listener;
    List<Integer> colorList;
    public DesignerSearchAdapter(EverythingSearchFragment.ItemSelectListener listener){
        this.listener = listener;
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

    public void changeData(List<SearchDataObject.UserSearchData> dataList){
        this.dataList = new ArrayList<>(dataList);
        notifyDataSetChanged();
    }



    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_base_search, parent, false);
        return new BaseHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        SearchDataObject.UserSearchData searchDataObject = dataList.get(position);
        holder.userName.setText(searchDataObject.getUsername());
        if (!TextUtils.isEmpty(searchDataObject.getProfilePic())) {
            holder.userImage.setVisibility(View.VISIBLE);
            holder.llNameHolder.setVisibility(View.INVISIBLE);
            Picasso.with(holder.itemView.getContext())
                    .load(searchDataObject.getProfilePic())
                    .placeholder(R.drawable.vector_image_place_holder_profile)
                    .tag(holder.itemView.getContext())
                    .resize(100, 100)
                    .into(holder.userImage);
        } else {
            holder.userImage.setVisibility(View.INVISIBLE);
            holder.llNameHolder.setVisibility(View.VISIBLE);
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
                    holder.llNameHolder.setBackground(mDrawable);
                } else holder.llNameHolder.setBackgroundDrawable(mDrawable);
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
                    holder.tvName.setText(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (dataList.size() > 2)
            return 2;
        return dataList.size();
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
            listener.onItemClick(dataList.get(getAdapterPosition()));
        }
    }
}
