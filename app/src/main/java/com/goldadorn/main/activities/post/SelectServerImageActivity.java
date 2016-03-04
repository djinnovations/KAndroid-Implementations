package com.goldadorn.main.activities.post;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.ServerFolderActivity;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.icons.GoldadornIconFont;
import com.goldadorn.main.icons.IconsUtils;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.modules.likes.VotersView;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.fragments.BaseFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.logging.Handler;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class SelectServerImageActivity extends ServerFolderActivity{

    public void processItem(ServerFolderObject data) {
        if(data.getPath()!=null) {
            Intent intent = new Intent();
            intent.putExtra("PATH", data.getPath());
            intent.putExtra("PREVIEW", data.getPreview());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else {
            final Snackbar snackbar = Snackbar.make(getLayoutParent(), "Please select image", Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
        }
    }
}
