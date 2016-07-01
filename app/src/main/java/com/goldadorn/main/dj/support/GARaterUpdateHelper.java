package com.goldadorn.main.dj.support;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.objects.Update;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.kobakei.ratethisapp.RateThisApp;

/**
 * Created by User on 27-06-2016.
 */
public class GARaterUpdateHelper {
    private static GARaterUpdateHelper ourInstance;

    public static GARaterUpdateHelper getInstance() {

        if (ourInstance == null) {
            ourInstance = new GARaterUpdateHelper();
        }
        return ourInstance;
    }

    private GARaterUpdateHelper() {

    }


    public void checkForUpdates(Activity activity) {
        final AppUpdater appUpdater = new AppUpdater(activity);
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(activity);
        appUpdaterUtils.withListener(new AppUpdaterUtils.UpdateListener() {
            @Override
            public void onSuccess(Update update, Boolean isUpdateAvailable) {
                Log.d("AppUpdater", update.getLatestVersion() + ", " + update.getUrlToDownload() + ", " + Boolean.toString(isUpdateAvailable));
                if (isUpdateAvailable)
                    displayUpdateDialog(appUpdater);
            }

            @Override
            public void onFailed(AppUpdaterError error) {
                Log.d("AppUpdater", "Something went wrong");
            }

        });
        appUpdaterUtils.start();
    }

    private void displayUpdateDialog(AppUpdater appUpdater) {

        try {
            appUpdater.setDisplay(Display.DIALOG)
                    .setDialogTitleWhenUpdateAvailable("Update available")
                    .setDialogDescriptionWhenUpdateAvailable("Check out the latest version available on Play Store!")
                    .setDialogButtonUpdate("Update now?")
                    .setDialogButtonDoNotShowAgain("Not Interested")
                    .setDialogTitleWhenUpdateNotAvailable("Update not available")
                    .setDialogDescriptionWhenUpdateNotAvailable("No update available. Check for updates again later!");
            appUpdater.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void rateApp(Activity activity) {
        if (Application.getInstance().getPrefManager().getIsAppRatingDone())
            return;
        if (isRateAppDoneForThisSession)
            return;
        if (Application.getInstance().getPrefManager().canStartForRatingApp()) {
            onStartObserverForRateApp(activity);
            setAppRateCallBack();
        }
    }

    private boolean isRateAppDoneForThisSession = false;

    private void onStartObserverForRateApp(final Activity activity) {
        try {
            //RateThisApp.onStart(activity);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isRateAppDoneForThisSession = true;
                            RateThisApp.showRateDialog(activity/*, R.style.AppTheme_NoActionBar*/);
                        }
                    });
                }
            }, 4000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setAppRateCallBack() {

        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
                Log.d("djapprate", "Rate app: yes clicked by user");
                /*Application.getInstance().getPrefManager().setAppRatingDone();
                Intent toPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Application.getInstance().getPackageName()));
                toPlayStore.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                Application.getInstance().startActivity(toPlayStore);
                RateThisApp.stopRateDialog(Application.getInstance());*/
                RandomUtils.performAppRateTask();
            }

            @Override
            public void onNoClicked() {
                Log.d("djapprate", "Rate app: not interested clicked by user");
            }

            @Override
            public void onCancelClicked() {
                Log.d("djapprate", "Rate app: later clicked by user");
            }
        });
    }

    /*private void setAppRateCallBack(Activity activity) {

        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
                Log.d("djapprate","Rate app: yes clicked by user");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                RateThisApp.stopRateDialog(this);
            }

            @Override
            public void onNoClicked() {
                Log.d("djapprate","Rate app: not interested clicked by user");
            }

            @Override
            public void onCancelClicked() {
                Log.d("djapprate","Rate app: later clicked by user");
            }
        });
    }*/


}
