package com.goldadorn.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.post.ImageSelectorFragment;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.icons.IconsUtils;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.URLHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.fragments.BaseFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class OurCollectionsActivity extends ServerFolderActivity{

    protected String getPageTitle() {
        return "Our Collections";
    }
    public void processItem(ServerFolderObject data) {
        String preview = data.getPreview();
        if(isProductLink(preview)!=null)
        {
            String profuctLink= URLHelper.getInstance().getWebSiteProductEndPoint()+isProductLink(preview)+".html";
            NavigationDataObject navigationDataObject =new NavigationDataObject(IDUtils.generateViewId(),"Our Collection",NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_CHROME,profuctLink,WebActivity.class);
            action(navigationDataObject);
        }
        else
        {
            final Snackbar snackbar = Snackbar.make(getLayoutParent(), "Product Not Found", Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
        }

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
}
