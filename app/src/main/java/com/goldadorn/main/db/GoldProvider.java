package com.goldadorn.main.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class GoldProvider extends ContentProvider {

    private LaunchDbHelper mLaunchDbHelper;


    @Override
    public boolean onCreate() {
        mLaunchDbHelper = new LaunchDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);
        SQLiteDatabase db = mLaunchDbHelper.getReadableDatabase();
        Cursor result;
        if (args.iscustomquery) {
            result = db.rawQuery(args.query, null);
        } else {
            result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SqlArguments args = new SqlArguments(uri);

        SQLiteDatabase db = mLaunchDbHelper.getWritableDatabase();
        final long rowId = db.insert(args.table, null, values);
        if (rowId <= 0) return null;

        uri = ContentUris.withAppendedId(uri, rowId);
        sendNotify(uri);

        return uri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mLaunchDbHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        if (count > 0) sendNotify(uri);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteDatabase db = mLaunchDbHelper.getWritableDatabase();
        int count = 0;
        try {
            if (args.iscustomquery) {
                db.execSQL(args.getUpdateQuery(values), selectionArgs);
            } else {
                count = db.update(args.table, values, args.where, args.args);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (count > 0) sendNotify(uri);

        return count;
    }

    private void sendNotify(Uri uri) {
        try {
            String notify = uri.getQueryParameter(Tables.PARAMETER_NOTIFY);
            if (notify == null || "true".equals(notify)) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LaunchDbHelper extends SQLiteOpenHelper {

        Context mContext;

        LaunchDbHelper(Context context) {
            super(context, Tables.DATABASE_NAME, null, Tables.DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Tables.CREATE_TABLE.USERS);
            db.execSQL(Tables.CREATE_TABLE.PRODUCTS);
            db.execSQL(Tables.CREATE_TABLE.COLLECTIONS);
            db.execSQL(Tables.CREATE_TABLE.ADDRESSES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static class SqlArguments {
        public final String table;
        public final String where;
        public final String[] args;
        public boolean iscustomquery = false;
        public String query;

        SqlArguments(Uri uri, String where, String[] args) {
            String customParam = uri.getQueryParameter(Tables.PARAMETER_CUSTOMQUERY);
            if (customParam != null && customParam.trim().equals("true")) {
                this.iscustomquery = true;
                query = where;
            }

            if (uri.getPathSegments().size() == 1) {
                this.table = uri.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (uri.getPathSegments().size() != 2) {
                if (!this.iscustomquery) throw new IllegalArgumentException("Invalid URI: " + uri);
                else {
                    this.table = "";
                    this.where = where;
                    this.args = args;
                }
            } else {
                this.table = uri.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(uri);
                this.args = null;
            }

        }

        public String getUpdateQuery(ContentValues values) {
            StringBuilder sql = new StringBuilder(120);
            sql.append("UPDATE ");
            sql.append(table);
            sql.append(" SET ");
            // move all bind args to one array
            int i = 0;
            for (String colName : values.keySet()) {
                sql.append((i > 0) ? "," : "");
                sql.append(colName);
                sql.append("=");
                sql.append(values.get(colName));
                i++;
            }
            if (!TextUtils.isEmpty(where)) {
                sql.append(" WHERE ");
                sql.append(where);
            }
            return sql.toString();
        }


        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }
}