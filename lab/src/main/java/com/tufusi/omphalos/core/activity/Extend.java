package com.tufusi.omphalos.core.activity;

import android.app.Activity;

import com.tufusi.omphalos.ILab;
import com.tufusi.omphalos.ILabActivity;

import java.lang.ref.WeakReference;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description
 */
public class Extend<T extends ILabActivity> {

    //弱引用持有，防止出现内存泄露
    WeakReference<Activity> activityWeakReference;
    private T mILabApi;
    int requestCode;

    public Extend(T iLabApi) {
        this.mILabApi = iLabApi;
        this.activityWeakReference = new WeakReference<>(null);
    }

    public Extend<T> withResult(Activity startActivity, int requestCode) {
        if (this.activityWeakReference.get() == null && startActivity == null) {
            return this;
        }

        this.activityWeakReference = new WeakReference<>(startActivity);
        this.requestCode = requestCode;
        return this;
    }

    public T build() {
        return mILabApi;
    }
}
