package com.goldadorn.main.utils;

import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bhavinpadhiyar on 2/17/16.
 */
public class IDUtils {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

}
