package com.goldadorn.main.dj.support;

import android.app.Activity;
import android.util.Log;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.objects.Update;

/**
 * Created by User on 27-06-2016.
 */
public class GAUpdateHelper {
    private static GAUpdateHelper ourInstance;

    public static GAUpdateHelper getInstance() {

        if (ourInstance == null) {
            ourInstance = new GAUpdateHelper();
        }
        return ourInstance;
    }

    private GAUpdateHelper() {

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

        appUpdater.setDisplay(Display.DIALOG)
                .setDialogTitleWhenUpdateAvailable("Update available")
                .setDialogDescriptionWhenUpdateAvailable("Check out the latest version available on Play Store!")
                .setDialogButtonUpdate("Update now?")
                .setDialogButtonDoNotShowAgain("Not Interested")
                .setDialogTitleWhenUpdateNotAvailable("Update not available")
                .setDialogDescriptionWhenUpdateNotAvailable("No update available. Check for updates again later!");
        appUpdater.start();
    }

}
