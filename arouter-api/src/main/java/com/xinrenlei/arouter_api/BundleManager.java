package com.xinrenlei.arouter_api;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Auth：yujunyao
 * Since: 2020/12/10 3:32 PM
 * Email：yujunyao@xinrenlei.net
 */

public class BundleManager {

    private AsDrawable asDrawable;

    public AsDrawable getAsDrawable() {
        return asDrawable;
    }

    public void setAsDrawable(AsDrawable asDrawable) {
        this.asDrawable = asDrawable;
    }

    private Bundle bundle = new Bundle();

    public Bundle getBundle() {
        return this.bundle;
    }

    public BundleManager withString(@Nullable String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleManager withInt(@Nullable String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBoolean(@Nullable String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public Object navigation(Context context) {
        return RouterManager.getInstance().navigation(context, this);
    }

    public BundleManager withSerializable(@Nullable String key, Serializable value) {
        bundle.putSerializable(key, value);
        return this;
    }

}
