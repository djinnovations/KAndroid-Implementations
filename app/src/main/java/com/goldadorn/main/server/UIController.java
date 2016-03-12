package com.goldadorn.main.server;

import android.content.Context;
import android.os.Handler;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.server.response.TimelineResponse;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class UIController {

    public static void getProductShowCase(final Context context, final TimelineResponse response, final IResultListener<TimelineResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getProductShowCase(context, response, 0);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) listener.onResult(response);
                    }
                });
            }
        };
        new Thread(runnable).start();
    }
}
