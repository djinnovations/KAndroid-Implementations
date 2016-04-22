package com.goldadorn.main.model;

import android.databinding.BaseObservable;

/**
 * Created by bpa001 on 4/21/16.
 */
public class FilterPrice extends BaseObservable
{
    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    private String min;

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    private String max;
}
