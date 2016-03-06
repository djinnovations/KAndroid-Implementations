package com.goldadorn.main.assist;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.goldadorn.main.db.Tables.Users;
import com.goldadorn.main.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoCache {
    public final boolean DEBUG;
    public static final String USER_INFO_SERVICE = "userinfocache";
    private static String[] PROJECTION = new String[]{Users._ID, Users.NAME, Users.DESCRIPTION,
            Users.IMAGEURL, Users.TYPE, Users.COUNT_LIKES, Users.COUNT_FOLLOWING,
            Users.COUNT_FOLLOWERS, Users.COUNT_COLLECTIONS, Users.COUNT_PRODUCTS, Users.BADGES, Users.DATAVERSION};
    private static final String[] PROJECTION_CACHE_CHECK = new String[]{Users.DATAVERSION};
    private static final int INDEX_ID = 0;
    private static final int INDEX_NAME = 1;
    private static final int INDEX_DESCRIPTION = 2;
    private static final int INDEX_IMAGE = 3;
    private static final int INDEX_TYPE = 4;
    private static final int INDEX_COUNT_LIKES = 5;
    private static final int INDEX_COUNT_FOLLOWING = 6;
    private static final int INDEX_COUNT_FOLLOWERS = 7;
    private static final int INDEX_COUNT_COLLECTION = 8;
    private static final int INDEX_COUNT_PRODUCT = 9;
    private static final int INDEX_BADGE_DUMP = 10;
    private static final int INDEX_DATAVERSION = 11;

    protected Context mContext;
    private PreLoader mPreload;
    private int mActiveCount;
    protected volatile Boolean mValid = false;
    private static boolean mIsActive = false;
    private long mLastUpdatedTime = -1;
    protected final Handler mUiThreadHandler;
    public static final String TAG = "UserInfoCache";
    private long mLastRequest, mLastCancelled, mLastExecuted;
    protected Map<Integer, User> mCache = new HashMap<>();
    private List<UserCacheListener> mCacheListeners = new ArrayList<>(
            2);
    private List<CICContentObesrver> mContentObservers = new ArrayList<>(
            5);

    public interface UserCacheListener {
        public void onCacheChanged();
    }

    public static UserInfoCache getInstance(Context context) {
        return (UserInfoCache) context.getApplicationContext()
                .getSystemService(USER_INFO_SERVICE);
    }

    public static synchronized UserInfoCache createInstance(Context context,
                                                            Handler handler) {
        return new UserInfoCache(context, handler, true);
    }

    public static void updateCacheAndDb(final Context context, final User user) {
        boolean isUpdated = false;
        if (mIsActive) {
            isUpdated = getInstance(context).updateCache(user);
        }
        if (isUpdated || !mIsActive)
            ((com.goldadorn.main.activities.Application) context.getApplicationContext()).postWork(new Runnable() {
                @Override
                public void run() {
                    ContentValues cv = new ContentValues();
                    cv.put(Users._ID, user.id);
                    cv.put(Users.NAME, user.getName());
                    cv.put(Users.DESCRIPTION, user.description);
                    cv.put(Users.IMAGEURL, user.imageUrl);
                    cv.put(Users.BADGES, user.badgesJson);
                    cv.put(Users.COUNT_FOLLOWERS, user.followers_cnt);
                    cv.put(Users.COUNT_FOLLOWING, user.following_cnt);
                    cv.put(Users.COUNT_LIKES, user.likes_cnt);
                    cv.put(Users.COUNT_COLLECTIONS, user.collections_cnt);
                    cv.put(Users.COUNT_PRODUCTS, user.products_cnt);

                    Uri uri = Users.CONTENT_URI_NO_NOTIFICATION;
                    String selection = Users._ID + " = ? ";
                    String[] selection_args = new String[]{String.valueOf(user.id)};
                    Cursor c = context.getContentResolver().query(uri, PROJECTION_CACHE_CHECK, selection, selection_args, null);
                    int cnt = 0;
                    long dbTsp = 0;
                    if (c != null) {
                        cnt = c.getCount();
                        if (c.moveToFirst())
                            dbTsp = c.getLong(0);
                        c.close();
                    }
                    if (cnt == 0) {
                        context.getContentResolver().insert(uri, cv);
                    } else if (user.dataVersion > dbTsp) {
                        context.getContentResolver().update(uri, cv, selection, selection_args);
                    }
                }
            });

    }


    protected UserInfoCache(Context context, Handler handler,
                            boolean debug) {
        if (!(context instanceof Application))
            throw new IllegalArgumentException(
                    "context should be of application class");
        DEBUG = debug;
        mUiThreadHandler = (handler == null) ? new Handler() : handler;
        mContext = context;
        mContentObservers.add(new CICContentObesrver(Users.CONTENT_URI));

    }

    private void startListening() {
        for (CICContentObesrver obs : mContentObservers)
            mContext.getContentResolver().registerContentObserver(obs.mUri,
                    true, obs);
        mPreload = new PreLoader();
        mPreload.start();
        mIsActive = true;
        if (DEBUG)
            Log.i(TAG, "startListening");
    }

    private void stopListening() {
        for (ContentObserver obs : mContentObservers)
            try {
                mContext.getContentResolver().unregisterContentObserver(obs);
            } catch (Exception e) {
            }
        mIsActive = false;
        if (DEBUG)
            Log.i(TAG, "stopListening");
    }

    public synchronized void start() {
        mActiveCount++;
        startEngine(true);

    }

    public synchronized void stop() {
        mActiveCount--;
        startEngine(false);
    }

    private void startEngine(boolean start) {
        mUiThreadHandler.removeCallbacks(mEngineStarter);
        mUiThreadHandler.postDelayed(mEngineStarter, start ? 0 : 2000);
    }

    Runnable mEngineStarter = new Runnable() {

        @Override
        public void run() {
            if (mActiveCount == 1 && !mIsActive)
                startListening();
            else if (mActiveCount == 0 && mIsActive)
                stopListening();

        }
    };

    public static boolean isActive() {
        return mIsActive;
    }

    public void registerCacheListeners(UserCacheListener cacheChangeNotfier) {
        if (!mCacheListeners.contains(cacheChangeNotfier))
            mCacheListeners.add(cacheChangeNotfier);

    }

    public void unRegisterCacheListeners(UserCacheListener cacheChangeNotfier) {
        mCacheListeners.remove(cacheChangeNotfier);
    }

    private void noifyCachelisteners() {
        for (int i = 0; i < mCacheListeners.size(); i++) {
            mCacheListeners.get(i).onCacheChanged();
        }
    }

    public boolean isValid() {
        return mValid && mIsActive;
    }

    protected void invalidate() {
        final long reqTsp = System.currentTimeMillis();
        mLastRequest = reqTsp;
        if ((reqTsp - mLastUpdatedTime) > 3000 && mValid && mIsActive) {
            mLastExecuted = reqTsp;
            if (DEBUG)
                Log.w(TAG, "invaldiate  " + mValid);
        } else {
            mLastCancelled = reqTsp;
            if (DEBUG)
                Log.i(TAG, "invaldiate cancelled " + mValid + " t-"
                        + (reqTsp - mLastUpdatedTime));
            if (mIsActive)
                mUiThreadHandler.postDelayed(mChecker, 1000);
            return;
        }
        if (mPreload.isAlive()) {
            mPreload.interrupt();
            if (DEBUG)
                Log.d(TAG, "invaldiate thread interupted ");
        }
        mPreload = new PreLoader();
        mPreload.start();
    }

    private Runnable mChecker = new Runnable() {

        @Override
        public void run() {
            if (mLastExecuted != mLastRequest)
                invalidate();

        }
    };


    private class PreLoader extends Thread {
        public PreLoader() {
            mLastUpdatedTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            if (DEBUG)
                Log.i(TAG, "RUNNING THE CONTACT CACHE THREAD");
            long DbugTime = System.currentTimeMillis();
            Cursor cursor = null;
            try {
                cursor = mContext.getContentResolver().query(Users.CONTENT_URI,
                        PROJECTION, null, null, null);
            } catch (Exception e) {
            }
            Map<Integer, User> cache = new HashMap<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(INDEX_ID);
                    User info = null;
                    long lastUpdated = cursor.getLong(INDEX_DATAVERSION);
                    if (cache.containsKey(id)) {
                        User cachedInfo = cache.get(id);
                        if (cachedInfo != null) {
                            if (cachedInfo.dataVersion > lastUpdated)
                                info = cachedInfo;
                        }

                    }
                    if (info == null) {
                        info = new User(id, cursor.getType(INDEX_TYPE));
                        info.dataVersion = lastUpdated;
                        extractFromCursor(info, cursor);
                        cache.put(id, info);
                    }

                } while (cursor.moveToNext());
            }

            if (cursor != null)
                cursor.close();

            cache.remove(null);
            mValid = false;
            mCache.clear();
            mCache.putAll(cache);
            cache.clear();
            cache = null;
            mValid = true;

            mUiThreadHandler.removeCallbacks(mAfterUpdatingCache);
            mUiThreadHandler.post(mAfterUpdatingCache);
            super.run();
            if (DEBUG) {
                Log.i(TAG, "FINISHED RUNNING THE CONTACT CACHE THREAD");
                Log.w(TAG, "(rc)ms- " + (System.currentTimeMillis() - DbugTime));
            }
        }

    }

    private boolean updateCache(User user) {
        User t = mCache.get(user.id);
        if (t == null || user.dataVersion > t.dataVersion) {
            mCache.put(user.id, user);
            return true;
        }
        return false;
//        mUiThreadHandler.removeCallbacks(mAfterUpdatingCache);
//        mUiThreadHandler.postDelayed(mAfterUpdatingCache, 2000);
    }


    protected Runnable mAfterUpdatingCache = new Runnable() {

        @Override
        public void run() {
            if (DEBUG)
                Log.i(TAG, "notifying contact listeners");
            noifyCachelisteners();
        }
    };

    public class CICContentObesrver extends ContentObserver {
        Uri mUri;

        public CICContentObesrver(Uri uri) {
            super(mUiThreadHandler);
            mUri = uri;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (DEBUG)
                Log.d(TAG, "onChange " + mUri + "   " + selfChange);
            if (mUri.equals(Users.CONTENT_URI)) {
                invalidate();
            }
        }
    }

    // User available methods

    protected User getBasicInfo(int id) {
        if (DEBUG)
            Log.d(TAG, "querying basic info of " + id);
//        if (TextUtils.isEmpty(id))
//            return null;
        User info = null;
        try {
            Cursor c = mContext.getContentResolver().query(Users.CONTENT_URI, PROJECTION,
                    Users._ID + " = ?", new String[]{String.valueOf(id)}, null);
            if (c != null && c.moveToFirst()) {
                info = new User(id, c.getInt(INDEX_TYPE));
                info = extractFromCursor(info, c);
            }
            if (c != null)
                c.close();
        } catch (Exception e) {
        }
        return info;
    }

    private User extractFromCursor(User info, Cursor cursor) {
        info.name = cursor.getString(INDEX_NAME);
        info.description = cursor.getString(INDEX_DESCRIPTION);
        info.imageUrl = cursor.getString(INDEX_IMAGE);
        info.likes_cnt = cursor.getInt(INDEX_COUNT_LIKES);
        info.followers_cnt = cursor.getInt(INDEX_COUNT_FOLLOWERS);
        info.following_cnt = cursor.getInt(INDEX_COUNT_FOLLOWING);
        info.collections_cnt = cursor.getInt(INDEX_COUNT_COLLECTION);
        info.products_cnt = cursor.getInt(INDEX_COUNT_PRODUCT);
        info.badgesJson = cursor.getString(INDEX_BADGE_DUMP);
        info.dataVersion=cursor.getLong(INDEX_DATAVERSION);
        return info;
    }


    public User getUserInfo(int id, boolean force) {
        if (mValid && mCache.containsKey(id)) {
            return mCache.get(id);
        } else {
            if (force) {
                return getBasicInfo(id);
            } else
                return null;
        }
    }

    //

    protected void finalize() throws Throwable {
        mCache.clear();
        mCacheListeners.clear();
        mCache = null;
        mCacheListeners = null;
        super.finalize();
    }
}