package com.xinrenlei.arouter_api;

import com.xinrenlei.arouter_annotation.RouterBean;

import java.util.Map;

public interface ARouterPath {

    /**
     * key  eg: /app/MainActivity
     * value eg: MainActivity.class
     * @return
     */
    Map<String, RouterBean> getPathMap();

}