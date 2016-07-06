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
public class VotesEmptyViewHelper extends EmptyViewHelper {
    public VotesEmptyViewHelper(Context context, View emptyView, IEmptyViewHelper emptyViewHelper,
                                boolean showInetnetError, boolean showRetryButton) {
        super(context, emptyView, emptyViewHelper, showInetnetError, showRetryButton);
    }


    @Override
    public void updateView(List dataManager) {
        super.updateView(dataManager);
        if (NetworkUtilities.isConnected(super.context)) {
            mEmptyViewMessage.setText("Looks like nobody has voted the post yet. Go ahead and be the first one!");
            mEmptyViewMessage.setTextSize(16);
            UiRandomUtils.setPaddingLeftRight(mEmptyViewMessage);
        }
    }

}
