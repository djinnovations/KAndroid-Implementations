package com.goldadorn.main.utils;

import android.os.AsyncTask;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class AsyncRunnableTask extends AsyncTask {
    private  Runnable runnable;

    public AsyncRunnableTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        runnable.run();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        runnable=null;
        super.onPostExecute(o);
    }
}
