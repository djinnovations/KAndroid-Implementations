package com.goldadorn.main.modules.socialFeeds;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.kimeeo.library.listDataView.EmptyViewHelper;
import com.kimeeo.library.utils.NetworkUtilities;

import java.util.List;

/**
 * Created by User on 27-06-2016.
 */
public class SocialFragmentEmptyViewHelper extends EmptyViewHelper {
    public SocialFragmentEmptyViewHelper(Context context, View emptyView, EmptyViewHelper.IEmptyViewHelper emptyViewHelper,
                                         boolean showInetnetError, boolean showRetryButton) {
        super(context, emptyView, emptyViewHelper, showInetnetError, showRetryButton);
    }


    @Override
    public void updateView(List dataManager) {
        super.updateView(dataManager);
        if (NetworkUtilities.isConnected(super.context)) {
            mEmptyViewMessage.setText("Oops! Looks like we had a glitch. Please restart the app to get back on");
            mEmptyViewMessage.setTextSize(16);
            ((Button) mRetry).setText("Restart App");
            mRetry.setVisibility(View.VISIBLE);
            UiRandomUtils.setPaddingLeftRight(mEmptyViewMessage);
        }
    }

}
