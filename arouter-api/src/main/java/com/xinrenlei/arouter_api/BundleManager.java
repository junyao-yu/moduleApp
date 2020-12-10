package com.xinrenlei.arouter_api;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Auth：yujunyao
 * Since: 2020/12/10 3:32 PM
 * Email：yujunyao@xinrenlei.net
 */

public class BundleManager {

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

    public void navigation(Context context) {
        RouterManager.getInstance().navigation(context, this);
    }

}
