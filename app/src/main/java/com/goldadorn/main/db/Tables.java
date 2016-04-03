package com.goldadorn.main.db;

import android.net.Uri;

import com.goldadorn.main.BuildConfig;

/**
 * Created by Kiran BH on 07/09/15.
 */
public class Tables {
    static final String DATABASE_NAME = "goldorn.db";
    static final int DATABASE_VERSION = 1;
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final String PARAMETER_NOTIFY = "notify";
    public static final String PARAMETER_CUSTOMQUERY = "customquery";
    public static final String SQL_DESC = " DESC";
    public static final String SQL_ASC = " ASC";

    public static Uri.Builder appendNotify(Uri.Builder builder, boolean notifiy) {
        return builder.appendQueryParameter(PARAMETER_NOTIFY, String.valueOf(notifiy));
    }

    public static Uri.Builder appendCustom(Uri.Builder builder) {
        return builder.appendQueryParameter(PARAMETER_CUSTOMQUERY, "true");
    }


    public interface IDataVersion {
        String DATAVERSION = "dataversion";
    }

    public static final class Products implements IDataVersion {
        public static final String TABLENAME = "products";
        public static final String _ID = "_id";
        public static final String COLLECTION_ID = "collectionids";
        public static final String USER_ID = "userid";
        public static final String NAME = "name";
        public static final String IMAGEURL = "imageurl";
        public static final String IMAGE_ASPECT_RATIO = "imgratio";
        public static final String PRICE = "price";
        public static final String PRICEUNIT = "currency";
        public static final String IS_LIKED = "isliked";
        public static final String DESCRIPTION = "description";
        public static final String COUNT_LIKES = "likes";
        public static final String COUNT_UNLIKES = "unlikes";
        public static final String CART_ADDED_TSP = "carttsp";
        public static final String BASIC_INFO = "basic_info";
        public static final String CUSTOMIZATION_INFO = "customization_info";


        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" +
                TABLENAME +
                "?" + PARAMETER_NOTIFY +
                "=true");

        /**
         * The content:// style URL for this table. When this Uri is used, no notification is
         * sent if the content changes.
         */
        public static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse("content://" +
                AUTHORITY +
                "/" + TABLENAME +
                "?" + PARAMETER_NOTIFY +
                "=false");
    }

    public static final class Collections implements IDataVersion {
        public static final String TABLENAME = "collections";
        public static final String _ID = "_id";
        public static final String USER_ID = "userid";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String IMAGEURL = "imageurl";
        public static final String CATEGORY = "category";
        public static final String IMAGE_ASPECT_RATIO = "imgratio";
        public static final String COUNT_LIKES = "likes";
        public static final String COUNT_PRODUCTS = "products";
        public static final String IS_LIKED = "isliked";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" +
                TABLENAME +
                "?" + PARAMETER_NOTIFY +
                "=true");

        /**
         * The content:// style URL for this table. When this Uri is used, no notification is
         * sent if the content changes.
         */
        public static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse("content://" +
                AUTHORITY +
                "/" + TABLENAME +
                "?" + PARAMETER_NOTIFY +
                "=false");
    }

    public static final class Users implements IDataVersion {
        public static final String TABLENAME = "users";

        public static final String _ID = "_id";
        public static final String TYPE = "type";
        public static final String NAME = "name";

        public static final String DESCRIPTION = "description";
        public static final String IMAGEURL = "imageurl";
        public static final String IMAGE_ASPECT_RATIO = "imgratio";
        public static final String TRENDING = "trending";
        public static final String FEATURED = "featured";

        public static final String COUNT_LIKES = "likes";
        public static final String COUNT_FOLLOWERS = "followers";
        public static final String COUNT_FOLLOWING = "following";
        public static final String COUNT_COLLECTIONS = "collections";
        public static final String COUNT_PRODUCTS = "products";
        public static final String IS_LIKED = "isliked";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" +
                TABLENAME +
                "?" + PARAMETER_NOTIFY +
                "=true");

        /**
         * The content:// style URL for this table. When this Uri is used, no notification is
         * sent if the content changes.
         */
        public static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse("content://" +
                AUTHORITY +
                "/" + TABLENAME +
                "?" + PARAMETER_NOTIFY +
                "=false");


        /**
         * The content:// style URL for a given row, identified by its id.
         *
         * @param id     The row id.
         * @param notify True to send a notification is the content changes.
         * @return The unique content URL for the specified row.
         */
        public static Uri getContentUri(long id, boolean notify) {
            return Uri.parse("content://" + AUTHORITY +
                    "/" + TABLENAME + "/" + id + "?" + PARAMETER_NOTIFY + "=" + notify);
        }


    }

    public static final class Addresses implements IDataVersion {
        public static final String TABLENAME = "addresses";
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String STREET = "street";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String COUNTRY = "country";
        public static final String PINCODE = "pincode";
        public static final String PHONENUMBER = "number";
        public static final String LAST_USED = "lastused";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" +
                TABLENAME +
                "?" + PARAMETER_NOTIFY +
                "=true");

        /**
         * The content:// style URL for this table. When this Uri is used, no notification is
         * sent if the content changes.
         */
        public static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse("content://" +
                AUTHORITY +
                "/" + TABLENAME +
                "?" + PARAMETER_NOTIFY +
                "=false");
    }

    public static final class CREATE_TABLE {

        static final String USERS = "CREATE TABLE IF NOT EXISTS " + Users.TABLENAME + " (" +
                Users._ID + " INTEGER PRIMARY KEY ," +
                Users.NAME + " TEXT," +
                Users.DESCRIPTION + " TEXT," +
                Users.IMAGEURL + " TEXT," +
                Users.IMAGE_ASPECT_RATIO + " REAL DEFAULT 1," +
                Users.TYPE + " INTEGER DEFAULT 0," +
                Users.TRENDING + " INTEGER DEFAULT 0," + Users.FEATURED + " INTEGER DEFAULT 0," +
                Users.COUNT_LIKES + " INTEGER DEFAULT 0," +
                Users.IS_LIKED + " INTEGER DEFAULT 0," +
                Users.COUNT_FOLLOWERS + " INTEGER DEFAULT 0," +
                Users.COUNT_FOLLOWING + " INTEGER DEFAULT 0," +
                Users.COUNT_COLLECTIONS + " INTEGER DEFAULT 0," +
                Users.COUNT_PRODUCTS + " INTEGER DEFAULT 0," +
                IDataVersion.DATAVERSION + " INTEGER DEFAULT 0)";

        static final String PRODUCTS = "CREATE TABLE IF NOT EXISTS " + Products.TABLENAME + " (" +
                Products._ID + " INTEGER PRIMARY KEY ," +
                Products.USER_ID + " INTEGER DEFAULT -1," +
                Products.COLLECTION_ID + " INTEGER DEFAULT -1," +
                Products.NAME + " TEXT," +
                Products.DESCRIPTION + " TEXT," +
                Products.IMAGEURL + " TEXT," +
                Products.IMAGE_ASPECT_RATIO + " REAL DEFAULT 1," +
                Products.PRICE + " INTEGER," +
                Products.PRICEUNIT + " TEXT," +
                Products.BASIC_INFO + " TEXT," +
                Products.CUSTOMIZATION_INFO + " TEXT," +
                Products.COUNT_LIKES + " INTEGER DEFAULT 0," +
                Products.IS_LIKED + " INTEGER DEFAULT 0," +
                Products.COUNT_UNLIKES + " INTEGER DEFAULT 0," +
                Products.CART_ADDED_TSP + " INTEGER DEFAULT 0," +
                IDataVersion.DATAVERSION + " INTEGER DEFAULT 0)";

        static final String COLLECTIONS = "CREATE TABLE IF NOT EXISTS " + Collections.TABLENAME + " (" +
                Collections._ID + " INTEGER PRIMARY KEY ," +
                Collections.USER_ID + " INTEGER DEFAULT -1," +
                Collections.NAME + " TEXT," +
                Collections.DESCRIPTION + " TEXT," +
                Collections.CATEGORY + " TEXT," +
                Collections.IMAGEURL + " TEXT," +
                Collections.IMAGE_ASPECT_RATIO + " REAL DEFAULT 1," +
                Collections.COUNT_LIKES + " INTEGER DEFAULT 0," +
                Collections.IS_LIKED + " INTEGER DEFAULT 0," +
                Collections.COUNT_PRODUCTS + " INTEGER DEFAULT 0," +
                IDataVersion.DATAVERSION + " INTEGER DEFAULT 0)";

        static final String ADDRESSES = "CREATE TABLE IF NOT EXISTS " + Addresses.TABLENAME + " (" +
                Addresses._ID + " INTEGER PRIMARY KEY ," +
                Addresses.NAME + " TEXT," +
                Addresses.STREET + " TEXT," + Addresses.CITY + " TEXT," + Addresses.STATE + " TEXT," + Addresses.COUNTRY + " TEXT," +
                Addresses.PHONENUMBER + " TEXT," +
                Addresses.PINCODE + " TEXT," +
                Addresses.LAST_USED + " INTEGER," +
                IDataVersion.DATAVERSION + " INTEGER DEFAULT 0)";

    }


}
