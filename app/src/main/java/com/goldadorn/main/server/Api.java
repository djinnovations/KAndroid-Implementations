package com.goldadorn.main.server;

import android.content.Context;
import android.database.sqlite.SQLiteDiskIOException;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.db.DbHelper;
import com.goldadorn.main.server.response.BasicResponse;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.ProfileResponse;
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
        L.d("User credentials "+response.mCookies);
    }

    public static void getDesigners(Context context, TimelineResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getDesigners(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeProductShowcaseData(context, response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void getDesignersSocial(Context context, TimelineResponse response, int retryCount){
        try {
            generateUserCredentials(context, response);
            ApiFactory.getDesignersSocial(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeDesignersSocial(context, response);
                response.responseContent = null;
                response.success = false;
                response.responseCode = -1;
                getDesigners(context,response,0);
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
                getProducts(context,response,0);
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

    public static void getBasicProfile(Context context, ProfileResponse response, int retryCount) {
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

    public static void setBasicProfile(Context context, ProfileResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.setBasicProfile(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void forgotPassword(Context context, ProfileResponse response, int retryCount) {
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

    public static void getCartDetails(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getCartDetails(context, response);
            if (response.success && response.responseContent != null) {
                ExtractResponse.extractGetCart(response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void addToCart(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.addToCart(context, response);
            if (response.success && response.responseContent != null) {
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    public static void removeFromCart(Context context, ProductResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.removeFromCart(context, response);
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
                DbHelper.writeLike(context,response);
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
                DbHelper.writeUnLike(context,response);
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
        response.success=false;
    }
}
