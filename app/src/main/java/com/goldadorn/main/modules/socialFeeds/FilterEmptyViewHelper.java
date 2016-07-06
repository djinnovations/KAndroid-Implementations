package com.goldadorn.main.modules.socialFeeds;

import android.content.Context;
import android.view.View;

import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.kimeeo.library.listDataView.EmptyViewHelper;
import com.kimeeo.library.utils.NetworkUtilities;

import java.util.List;

/**
 * Created by User on 27-06-2016.
 */
public class FilterEmptyViewHelper extends EmptyViewHelper {
    public FilterEmptyViewHelper(Context context, View emptyView, IEmptyViewHelper emptyViewHelper,
                                 boolean showInetnetError, boolean showRetryButton) {
        super(context, emptyView, emptyViewHelper, showInetnetError, showRetryButton);
    }


    @Override
    public void updateView(List dataManager) {
        super.updateView(dataManager);
        if (NetworkUtilities.isConnected(super.context)) {
            mEmptyViewMessage.setText("Nothing here yet! Stay tuned");
            mEmptyViewMessage.setTextSize(16);
            /*((Button) mRetry).setText("Restart App");
            mRetry.setVisibility(View.VISIBLE);*/
            UiRandomUtils.setPaddingLeftRight(mEmptyViewMessage);
        }
    }

}
