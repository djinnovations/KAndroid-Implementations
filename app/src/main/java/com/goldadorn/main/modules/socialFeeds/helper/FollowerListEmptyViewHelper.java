package com.goldadorn.main.modules.socialFeeds.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.kimeeo.library.listDataView.EmptyViewHelper;
import com.kimeeo.library.utils.NetworkUtilities;

import java.util.List;

/**
 * Created by User on 27-06-2016.
 */
public class FollowerListEmptyViewHelper extends EmptyViewHelper {
    public FollowerListEmptyViewHelper(Context context, View emptyView, IEmptyViewHelper emptyViewHelper,
                                       boolean showInetnetError, boolean showRetryButton) {
        super(context, emptyView, emptyViewHelper, showInetnetError, showRetryButton);
    }


    @Override
    public void updateView(List dataManager) {
        super.updateView(dataManager);
        if (NetworkUtilities.isConnected(super.context)) {
            mEmptyViewMessage.setText("Looks like nobody has followed you yet!");
            mEmptyViewMessage.setTextSize(16);
            mEmptyViewMessage.setGravity(Gravity.CENTER);
            UiRandomUtils.setPaddingLeftRight(mEmptyViewMessage);
        }
    }

}
