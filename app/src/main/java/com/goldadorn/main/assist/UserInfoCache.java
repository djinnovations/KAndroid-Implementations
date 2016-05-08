package com.goldadorn.main.assist;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
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
    private static final String[] PROJECTION_CACHE_CHECK = new String[]{Users.DATAVERSION};

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
    private List<UserCacheListener> mCacheListeners = new ArrayList<>(2);
    private List<CICContentObesrver> mContentObservers = new ArrayList<>(5);

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
                        null, null, null, null);
            } catch (Exception e) {
            }
            Map<Integer, User> cache = new HashMap<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(Users._ID));
                    User info = null;
                    long lastUpdated = cursor.getLong(cursor.getColumnIndex(Users.DATAVERSION));
                    if (cache.containsKey(id)) {
                        User cachedInfo = cache.get(id);
                        if (cachedInfo != null) {
                            if (cachedInfo.dataVersion > lastUpdated)
                                info = cachedInfo;
                        }

                    }
                    if (info == null) {
                        cache.put(id, info=User.extractFromCursor(cursor));
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
            Cursor c = mContext.getContentResolver().query(Users.CONTENT_URI,null,
                    Users._ID + " = ?", new String[]{String.valueOf(id)}, null);
            if(c.moveToFirst())
            info=User.extractFromCursor(c);
        } catch (Exception e) {
        }
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


    public User getUserInfoDB(int id, boolean force) {
       /* if (mValid && mCache.containsKey(id)) {
            return mCache.get(id);
        } else {*/
        if(id!=0) {
            if (force) {
                return getBasicInfo(id);
            } else
                return null;
        }else
            return null;
      //  }
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