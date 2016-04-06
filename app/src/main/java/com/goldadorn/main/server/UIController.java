package com.goldadorn.main.server;

import android.content.Context;
import android.os.Handler;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.ProfileResponse;
import com.goldadorn.main.server.response.TimelineResponse;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class UIController {

    public static void getDesigners(final Context context, final TimelineResponse response, final IResultListener<TimelineResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getDesignersSocial(context, response, 0);
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
                Api.getProductsSocial(context, response, 0);
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

    public static void getProductOptions(final Context context, final ProductResponse response, final IResultListener<ProductResponse> listener) {
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

    public static void getCartDetails(final Context context, final ProductResponse response, final IResultListener<ProductResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getCartDetails(context, response, 0);
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

    public static void addToCart(final Context context, Product product, final IResultListener<ProductResponse> listener) {
        final ProductResponse response = new ProductResponse();
        response.product = product;
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.addToCart(context, response, 0);
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

    public static void removeFromCart(final Context context, Product product, final IResultListener<ProductResponse> listener) {
        final ProductResponse response = new ProductResponse();
        response.product = product;
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.removeFromCart(context, response, 0);
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

    public static void notifyPayment(final Context context, final ProductResponse response, final IResultListener<ProductResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.notifyPayment(context, response, 0);
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

    public static void like(final Context context, Object model, final boolean like, final IResultListener<LikeResponse> listener) {
        final LikeResponse response = new LikeResponse();
        if (model instanceof Product) {
            Product p = (Product) model;
            response.productId = p.id;
            response.collectionId = p.collectionId;
            response.userId = p.userId;
        } else if (model instanceof Collection) {
            Collection p = (Collection) model;
            response.collectionId = p.id;
            response.userId = p.userId;
        }else if (model instanceof User) {
            User p = (User) model;
            response.userId = p.id;
        } else {
            throw new IllegalArgumentException("Undefined model to like");
        }
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                if (like)
                    Api.like(context, response, 0);
                else
                    Api.unLike(context, response, 0);
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

    public static void follow(final Context context, User designer, final boolean follow, final IResultListener<LikeResponse> listener) {
        final LikeResponse response = new LikeResponse();
        response.userId = designer.id;
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.follow(context, response, 0);
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

    public static void getBasicProfileInfo(final Context context, final ProfileResponse response, final IResultListener<ProfileResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getBasicProfile(context, response, 0);
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
    public static void setBasicProfileInfo(final Context context, final ProfileResponse response, final IResultListener<ProfileResponse> listener) {
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.setBasicProfile(context, response, 0);
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
