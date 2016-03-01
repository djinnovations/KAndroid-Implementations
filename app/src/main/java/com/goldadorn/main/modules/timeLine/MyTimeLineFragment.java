package com.goldadorn.main.modules.timeLine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class MyTimeLineFragment extends UsersTimeLineFragment {

    public void onViewCreated(View view) {
        super.onViewCreated(view);
        getFloatingActionsMenu().setVisibility(View.GONE);
    }
}
