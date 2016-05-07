package com.goldadorn.main.server;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.ProductOptions;
import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.response.CreatePostForBuyResponse;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ObjectResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.SearchResponse;
import com.goldadorn.main.server.response.TimelineResponse;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class UIController {

    public static void getShowCase(final Context context, final IResultListener<TimelineResponse> listener) {
        final TimelineResponse response= new TimelineResponse();
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

    public static void getPriceForCustomization(final Context context,  Product product, final IResultListener<ProductResponse> listener) {
        final ProductResponse response= new ProductResponse();
        response.product=product;
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

    public static void getCartDetails(final Context context, final IResultListener<ProductResponse> listener) {
        final ProductResponse response=ProductResponse.getListResponse();
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
        final ProductResponse response =ProductResponse.getAddToListResponse(product);
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


    public static void addToCartNewProduct(final Context context, Product product, ProductInfo mProductInfo, ProductOptions mProductOptions, final IResultListener<ProductResponse> listener) {
        final ProductResponse response =ProductResponse.getAddToListResponseNew(product,mProductInfo,mProductOptions);
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.addToCartNew(context, response, 0);
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
        } else if (model instanceof User) {
            User p = (User) model;
            Log.e("iii",p.id+"");
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
                if(follow)
                    Api.follow(context, response, 0);
                else
                    Api.Unfollow(context, response, 0);
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

    public static void getBasicProfileInfo(final Context context, final IResultListener<ObjectResponse<ProfileData>> listener) {
        final ObjectResponse<ProfileData> response = new ObjectResponse<>();
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

    public static void forgotPassword(final Context context, String email, final IResultListener<ObjectResponse<ProfileData>> listener) {
        final ObjectResponse<ProfileData> response = new ObjectResponse<>();
        response.object = new ProfileData();
        response.object.email = email;
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.forgotPassword(context, response, 0);
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

    public static void getSearchTags(final Context context,final SearchResponse response,final IResultListener<SearchResponse> listener){
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getSearchTags(context, response, 0);
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

    public static void getWishlist(final Context context,final IResultListener<ProductResponse> listener){
        final ProductResponse response=ProductResponse.getListResponse();
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.getWishlist(context, response, 0);
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

    public static void deleteFromWhishlist(final Context context,Product product,final IResultListener<ProductResponse> listener) {
        final ProductResponse response = ProductResponse.deleteWishlistReponse(product);
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.deleteWishList(context, response, 0);
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

    public static void addToWhishlist(final Context context,Product product,final IResultListener<ProductResponse> listener){
       final ProductResponse response=ProductResponse.getAddToListResponse(product);
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.addToWishlist(context, response, 0);
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
    public static void buyorNobuy(final Context context, final CreatePostForBuyResponse response, final IResultListener<CreatePostForBuyResponse> listener){
        Runnable runnable = new Runnable() {
            public void run() {
                Handler handler = ((Application) context.getApplicationContext()).getUIHandler();
                Api.buyorNobuy(context, response, 0);
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
