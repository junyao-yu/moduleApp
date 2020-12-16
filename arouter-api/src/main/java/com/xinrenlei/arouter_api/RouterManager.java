package com.xinrenlei.arouter_api;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.collection.LruCache;

import com.blankj.utilcode.util.LogUtils;
import com.xinrenlei.arouter_annotation.RouterBean;


/**
 * Auth：yujunyao
 * Since: 2020/12/10 3:20 PM
 * Email：yujunyao@xinrenlei.net
 */

public class RouterManager {

    private static volatile RouterManager mInstance;
    private String path;
    private String group;
    private LruCache<String, ARouterGroup> groupLruCache;
    private LruCache<String, ARouterPath> pathLruCache;

    // 为了拼接，例如:ARouter$$Group$$business
    private final static String FILE_GROUP_NAME = "ARouter$$Group$$";

    public static RouterManager getInstance() {
        if (mInstance == null) {
            synchronized (RouterManager.class) {
                if (mInstance == null) {
                    mInstance = new RouterManager();
                }
            }
        }
        return mInstance;
    }

    private RouterManager() {
        groupLruCache = new LruCache<>(20);
        pathLruCache = new LruCache<>(20);
    }

    //如  /business/MainActivity
    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("path 路径不合规范 eg:/business/MainActivity");
        }

        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("path 路径不合规范 eg:/business/MainActivity");
        }

        //截取组名  /business/MainActivity groupName = business
        String groupName = path.substring(1, path.indexOf("/", 1));

        if (TextUtils.isEmpty(groupName)) {
            throw new IllegalArgumentException("path 路径不合规范 eg:/business/MainActivity");
        }

        this.path = path;
        this.group = groupName;

        return new BundleManager();
    }

    private final static String AROUTER_GENERATE_PATH = "com.xinrenlei.arouter";

    public Object navigation(Context context, BundleManager bundleManager) {
        String groupClassName = AROUTER_GENERATE_PATH + "." + FILE_GROUP_NAME + group;
        LogUtils.e("navigation>>>> " + groupClassName);

        try {
            //1、读取路由组group文件
            ARouterGroup aRouterGroup = groupLruCache.get(this.group);
            if (aRouterGroup == null) {
                Class<?> clazz = Class.forName(groupClassName);
                aRouterGroup = (ARouterGroup) clazz.newInstance();

                groupLruCache.put(this.group, aRouterGroup);
            }

            if (aRouterGroup.getGroupMap().isEmpty()) {
                throw new RuntimeException("路由表Group出问题了。。。");
            }

            //2、读取path文件
            ARouterPath aRouterPath = pathLruCache.get(this.path);
            if (aRouterPath == null) {
                Class<? extends ARouterPath> clazz = aRouterGroup.getGroupMap().get(this.group);
                aRouterPath = clazz.newInstance();

                pathLruCache.put(this.path, aRouterPath);
            }

            if (aRouterPath.getPathMap().isEmpty()) {
                throw new RuntimeException("路由表path出问题了。。。");
            }

            //3、跳转
            RouterBean routerBean = aRouterPath.getPathMap().get(this.path);

            if (routerBean != null) {
                switch (routerBean.getTypeEnum()) {
                    case ACTIVITY:
                        Intent intent = new Intent(context, routerBean.getClazz());
                        intent.putExtras(bundleManager.getBundle());
                        //以前不知道的地方，这算是activity的一种外部调用
                        //如果context是非activity启动activity的话，加入以下参数
                        //否则Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
                        if (context instanceof Application) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        context.startActivity(intent);
                        break;
                    case DRAWABLE:
                        Class<?> clazz = routerBean.getClazz();
                        AsDrawable asDrawable = (AsDrawable) clazz.newInstance();
                        bundleManager.setAsDrawable(asDrawable);
                        return bundleManager.getAsDrawable();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
