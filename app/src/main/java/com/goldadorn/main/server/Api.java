package com.goldadorn.main.server;

import android.content.Context;
import android.database.sqlite.SQLiteDiskIOException;
import android.util.Log;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.db.DbHelper;
import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.server.response.BasicResponse;
import com.goldadorn.main.server.response.CreatePostForBuyResponse;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ObjectResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.SearchResponse;
import com.goldadorn.main.server.response.TimelineResponse;
import com.goldadorn.main.utils.L;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class Api {

    private static void generateUserCredentials(Context context,
                                                BasicResponse response) {
        List<Cookie> cookies = ((Application) context.getApplicationContext()).getCookies();
        if (!cookies.isEmpty()) {
            for (int i = 0; i < cookies.size(); i++) {
                response.mCookies += cookies.get(i).getName() + "=" + cookies.get(i).getValue() + ";";
            }
        }
        L.d("User credentials " + response.mCookies);
    }

    public static void getSearchTags(Context context, SearchResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getSearchTags(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }
    public static void getWishlist(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getWishlist(context, response);
            if (response.success && response.responseContent != null) {
                ExtractResponse.extractGetWishlist(response);
                response.responseContent = null;
                response.success = false;
                response.responseCode = -1;
                getProducts(context, response, 0);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void deleteWishList(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.deleteWishList(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void addToWishlist(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.addToWishlist(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void buyorNobuy(Context context, CreatePostForBuyResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.buyorNobuy(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void getDesignersSocial(Context context, TimelineResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getDesignersSocial(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeDesignersSocial(context, response);
                response.responseContent = null;
                response.success = false;
                response.responseCode = -1;
                ApiFactory.getDesigners(context, response);
                if (response.success && response.responseContent != null) {
                    DbHelper.writeProductShowcaseData(context, response);
                }
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void getProducts(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getProducts(context, response);
            Log.d("djtime","getProducts - data arrangement started time-: "+System.currentTimeMillis());
            if (response.success && response.responseContent != null) {
                DbHelper.writeProducts(context, response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void getProductsSocial(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getProductsSocial(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeProductsSocial(context, response);
                response.responseContent = null;
                response.success = false;
                response.responseCode = -1;
                getProducts(context, response, 0);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void getProductBasicInfo(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getProductBasicInfo(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeProductBasicInfo(context, response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void getBasicProfile(Context context, ObjectResponse<ProfileData> response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getBasicProfile(context, response);
            if (response.success && response.responseContent != null) {
                ExtractResponse.extractBasicProfile(response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void forgotPassword(Context context, ObjectResponse<ProfileData> response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.forgotPassword(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void getProductCustomization(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getProductCustomization(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeProductCustomization(context, response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void getPriceForCustomization(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getPriceForCustomization(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void getCartDetails(Context context, boolean isUseCart, String orderId, int offset,ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getCartDetails(context, isUseCart, orderId,offset, response);
            /*if (response.success && response.responseContent != null) {
                ExtractResponse.extractGetCart(response);
            }*/
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    /*public static void addToCart(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.addToCart(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }*/

    public static void addToCartNew(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.addToCartNew(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void removeFromCart(Context context, int transId, final int reduceQty, ProductResponse response, int orderQty) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.removeFromCart(context, transId, reduceQty, response, orderQty);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void notifyPayment(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.notifyPayment(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void like(Context context, LikeResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.like(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeLike(context, response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void unLike(Context context, LikeResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.unLike(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeUnLike(context, response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void follow(Context context, LikeResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.follow(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeFollow(context, response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void Unfollow(Context context, LikeResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.follow(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeUnFollow(context, response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    /**
     * this function will handle all the exceptions related to http calls to server. the exception
     * message will be extracted and exception type will be saved in response class.
     *
     * @param context
     * @param response {@link BasicResponse} response object saves the
     *                 exception details
     * @param e        exception to extract for the details.
     */
    private static void extractException(final Context context, BasicResponse response, Exception e) {
        if (e instanceof UnknownHostException) {
            response.responseCode = BasicResponse.IO_EXE;
        } else if (e instanceof SQLiteDiskIOException) {
            response.responseCode = BasicResponse.IO_EXE;
        } else if (e instanceof IOException) {
            response.responseCode = BasicResponse.IO_EXE;
        } else if (e instanceof JSONException) {
            response.responseCode = BasicResponse.JSON_EXE;
        }
        response.success = false;
    }
}
