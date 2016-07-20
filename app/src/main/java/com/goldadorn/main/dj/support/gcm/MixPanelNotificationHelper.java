package com.goldadorn.main.dj.support.gcm;

/**
 * Created by User on 20-07-2016.
 */
public class MixPanelNotificationHelper {
    private static MixPanelNotificationHelper ourInstance = new MixPanelNotificationHelper();

    public static MixPanelNotificationHelper getInstance() {
        return ourInstance;
    }

    private MixPanelNotificationHelper() {
    }
}
