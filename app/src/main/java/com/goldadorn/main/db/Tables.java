package com.goldadorn.main.db;

import android.net.Uri;

/**
 * Created by Kiran BH on 07/09/15.
 */
public class Tables {
    static final String DATABASE_NAME = "goldorn.db";
    static final int DATABASE_VERSION = 0;
    public static final String AUTHORITY = "com.goldadorn.main.provider";
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


    /**
     * Workspace Screens.
     * <p/>
     * Tracks the order of workspace screens.
     */
    public static final class WorkspaceScreens {
        public static final String TABLENAME = "workspaceScreens";

        /**
         * The rank of this screen -- ie. how it is ordered relative to the other screens.
         * <P>Type: INTEGER</P>
         */
        public static final String SCREEN_RANK = "screenRank";

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" +
                TABLENAME +
                "?" +
                PARAMETER_NOTIFY +
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


    /**
     * Favorites.
     */
    public static final class Users {
        /**
         * The dbtable for favorite
         * <P>Type: STRING</P>
         */
        public static final String TABLENAME = "users";

        public static final String _ID = "_id";

        public static final String TYPE = "type";

        public static final String NAME = "name";

        public static final String DESCRIPTION = "description";
        public static final String IMAGEURL = "imageurl";

        public static final String COUNT_LIKES = "likes";
        public static final String COUNT_FOLLOWERS = "followers";
        public static final String COUNT_FOLLOWING = "following";
        public static final String COUNT_COLLECTIONS = "collections";
        public static final String COUNT_PRODUCTS = "products";

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


    public static final class CREATE_TABLE {

        static final String USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + Users.TABLENAME + " (" +
                Users._ID + " INTEGER PRIMARY KEY ," +
                Users.NAME + " TEXT," +
                Users.DESCRIPTION + " TEXT," +
                Users.IMAGEURL + " TEXT," +
                Users.TYPE + " INTEGER DEFAULT 0," +
                Users.COUNT_LIKES + " INTEGER DEFAULT 0," +
                Users.COUNT_FOLLOWERS + " INTEGER DEFAULT 0," +
                Users.COUNT_FOLLOWING + " INTEGER DEFAULT 0," +
                Users.COUNT_COLLECTIONS + " INTEGER DEFAULT 0," +
                Users.COUNT_PRODUCTS + " INTEGER DEFAULT 0)";
    }


}
