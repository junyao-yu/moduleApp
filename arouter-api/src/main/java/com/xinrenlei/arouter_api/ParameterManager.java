package com.xinrenlei.arouter_api;

import android.app.Activity;

import androidx.collection.LruCache;

import com.blankj.utilcode.util.LogUtils;

/**
 * Auth：yujunyao
 * Since: 2020/12/10 1:32 PM
 * Email：yujunyao@xinrenlei.net
 */

public class ParameterManager {

    private static volatile ParameterManager mInstance;

    public static ParameterManager getInstance() {
        if (mInstance == null) {
            synchronized (ParameterManager.class) {
                if (mInstance == null) {
                    mInstance = new ParameterManager();
                }
            }
        }
        return mInstance;
    }


    private LruCache<String, ParameterGet> cache;

    private ParameterManager() {
        cache = new LruCache<>(20);//缓存是为了性能
    }

    // 为了这样检索：MainActivity + $$Parameter
    private final static String FILE_SUFFIX_NAME = "$$Parameter";

    //Activity中调用这个方法进行参数获取
    public void loadParameter(Activity activity) {
        String className = activity.getClass().getName();// == xxx.xxx.xx.MainActivity

        ParameterGet load = cache.get(className);

        if (load == null) {
            try {
                LogUtils.e("loadParameter>>>>" + className + FILE_SUFFIX_NAME);
                Class<?> clazz = Class.forName(className + FILE_SUFFIX_NAME);
                load = (ParameterGet) clazz.newInstance();
                cache.put(className, load);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        load.getParameter(activity);
    }

}
