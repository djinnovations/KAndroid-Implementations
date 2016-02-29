package com.goldadorn.main.modules.slideShow;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.StaticDataManger;
import com.kimeeo.library.listDataView.viewPager.BaseItemHolder;
import com.kimeeo.library.listDataView.viewPager.viewPager.HorizontalViewPager;
import com.squareup.picasso.Picasso;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 2/26/16.
 */
public class SimpleImageArray extends HorizontalViewPager
{

    protected void setUpIndicator(View indicator, ViewPager viewPager) {
        super.setUpIndicator(indicator,viewPager);
        if(indicator!=null)
            indicator.setVisibility(View.GONE);
    }
    @Override
    protected DataManager createDataManager()
    {
        DataManager dataManger =new StaticDataManger(getActivity());
        String[] images =(String[])getPramas();
        for (int i = 0; i < images.length; i++) {
            if(images[i]!=null && images[i].equals("")==false)
                dataManger.add(images[i]);
        }
        return dataManger;
    }
    public String getPageTitle(int position, Object o) {
        return position+"";
    }
    public View getView(int position, Object data) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.simple_slide_image,null);
        return view;
    }
    @Override
    public BaseItemHolder getItemHolder(View view, int position, Object data) {
        return new PageView(view);
    }
    // Update View Here
    public class PageView extends BaseItemHolder {

        @Bind(R.id.imageView)
        ImageView imageView;

        public PageView(View itemView)
        {
            super(itemView);
        }

        public void updateItemView(Object item,View view,int position)
        {
            String listObject = (String)item;
            Picasso.with(getActivity())
                    .load(listObject)
                    .tag(getActivity())
                    .placeholder(R.drawable.vector_icon_progress_animation)
                    .error(R.drawable.vector_image_logo_square_100dp)
                    .into(imageView);
        }
        public void cleanView(View itemView,int position)
        {

        }
    }
}
