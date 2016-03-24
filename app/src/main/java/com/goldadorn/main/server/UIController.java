package com.goldadorn.main.server;

import android.content.Context;
import android.os.Handler;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.TimelineResponse;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class UIController {

    public static void getDesigners(final Context context, final TimelineResponse response, final IResultListener<TimelineResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getDesigners(context, response, 0);
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

    public static void getProducts(final Context context, final ProductResponse response, final IResultListener<ProductResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getProducts(context, response, 0);
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

    public static void getProductBasicInfo(final Context context, final ProductResponse response, final IResultListener<ProductResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getProductBasicInfo(context, response, 0);
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
    public static void getProductCustomization(final Context context, final ProductResponse response, final IResultListener<ProductResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getProductCustomization(context, response, 0);
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

    public static void getPriceForCustomization(final Context context, final ProductResponse response, final IResultListener<ProductResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getPriceForCustomization(context, response, 0);
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
