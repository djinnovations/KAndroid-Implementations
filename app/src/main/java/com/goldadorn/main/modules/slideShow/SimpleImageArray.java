package com.goldadorn.main.modules.slideShow;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.WebActivity;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.StaticDataManger;
import com.kimeeo.library.listDataView.viewPager.BaseItemHolder;
import com.kimeeo.library.listDataView.viewPager.viewPager.HorizontalViewPager;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 2/26/16.
 */
public class SimpleImageArray extends HorizontalViewPager
{
    private int index;

    ProgressView progressBar;

    protected View createRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slide_show_page_view, container, false);
        progressBar = (ProgressView)rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0f);
        progressBar.start();
        setHasOptionsMenu(true);
        return rootView;
    }


    protected void setUpIndicator(View indicator, ViewPager viewPager) {
        super.setUpIndicator(indicator, viewPager);
        if(indicator!=null)
            indicator.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
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

        final Handler handler = new Handler();
        final Runnable runnablelocal = new Runnable() {
            @Override
            public void run() {

                try {
                    gotoItem(index, true);
                    progressBar.setVisibility(View.GONE);
                    getViewPager().setVisibility(View.VISIBLE);

                    YoYo.with(Techniques.FadeIn).duration(500).playOn(getViewPager());
                } catch (Exception e) {
                }
            }
        };
        handler.postDelayed(runnablelocal, 2000);

        return dataManger;
    }
    String imageURL;
    boolean hasLink=false;
    protected void onPageChange(Object itemPosition, int position) {
        imageURL =(String)itemPosition;

        if(imageURL!=null && isProductLink(imageURL)!=null)
            hasLink = true;
        else
            hasLink = false;
        ActivityCompat.invalidateOptionsMenu(getActivity());
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.buy_menu, menu);
        menu.findItem(R.id.nav_buy).setVisible(hasLink);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.nav_buy)
        {
            String profuctLink= URLHelper.getInstance().getWebSiteProductEndPoint()+isProductLink(imageURL)+".html";
            NavigationDataObject navigationDataObject =new NavigationDataObject(IDUtils.generateViewId(),"Our Collection",NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY,profuctLink,WebActivity.class);
            EventBus.getDefault().post(new AppActions(navigationDataObject));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected String isProductLink(String path)
    {
        if(path!=null && path.equals("")==false && path.indexOf("galink_")!=-1)
        {
            String link = path.substring(path.indexOf("galink_") + "galink_".length());
            link = link.substring(0,link.indexOf("."));
            try{
                String firstChar = link.substring(0,link.indexOf("_"));
                int val=Integer.parseInt(firstChar);
                link = link.substring(link.indexOf("_")+1);
            }
            catch (Exception e)
            {

            }
            if(link.indexOf("_")!=-1)
                link = link.replaceAll("_","-");
            return link;

        }
        return null;
    }

    public String getPageTitle(int position, Object o) {
        return position+"";
    }
    public View getView(int position, Object data) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.simple_slide_image,null);
        return view;
    }

    /*
    @Override
    public void removeView(View view, int position, BaseItemHolder itemHolder) {
        if(view!=null && view.findViewById(R.id.imageView)!=null && view.findViewById(R.id.imageView) instanceof ImageView)
        {
            ((ImageView)view.findViewById(R.id.imageView)).setImageBitmap(null);
        }
    }*/

    @Override
    public BaseItemHolder getItemHolder(View view, int position, Object data) {
        return new PageView(view);
    }

    public void setIndex(int index) {
        this.index=index;
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
            imageView.setImageBitmap(null);
        }
    }
}
