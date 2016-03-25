package com.goldadorn.main.assist;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

import com.goldadorn.main.activities.Application;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class AsyncLoader<D extends BaseResult> extends AsyncTaskLoader<D> {
    protected Context mContext;
    private final ForceLoadContentObserver mObserver;
    private D mResult;
    Handler mUIHandler;

    public AsyncLoader(Context context) {
        super(context);
        mContext = context;
        mUIHandler = ((Application) mContext.getApplicationContext()).getUIHandler();
        if (mUIHandler == null) mUIHandler = new Handler();
        mObserver = new ForceLoadContentObserver();
    }

    @Override
    protected D onLoadInBackground() {
        D d = super.onLoadInBackground();
        registerContentObserver(mObserver);
        return d;
    }

    /**
     * Registers an observer to get notifications from the content provider when
     * the result needs to be refreshed.
     */
    protected abstract void registerContentObserver(ContentObserver observer);

    protected abstract void unRegisterContentObserver(ContentObserver observer);

    /* Runs on the UI thread */
    @Override
    public void deliverResult(D result) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (result != null) {
                result.clear();
            }
            return;
        }
        BaseResult oldResult = mResult;
        mResult = result;

        if (isStarted()) {
            super.deliverResult(result);
        }

        if (oldResult != null && !oldResult.equals(result)) {
            oldResult.clear();
        }
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is
     * ready the callbacks will be called on the UI thread. If a previous load
     * has been completed and is still valid the result may be passed to the
     * callbacks immediately.
     * <p/>
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        }
        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(D data) {
        if (data != null) {
            data.clear();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        unRegisterContentObserver(mObserver);
        if (mResult != null) {
//            mResult.clear();
        }
        mResult = null;
        mContext = null;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer,
                     String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("mResult=");
        writer.println(mResult);
        writer.print(prefix);
        writer.print("mContentChanged=");
    }

    public final class ForceLoadContentObserver extends ContentObserver {
        public ForceLoadContentObserver() {
            super(mUIHandler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }
}