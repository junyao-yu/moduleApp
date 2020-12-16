package com.xinrenlei.business.impl;

import com.xinrenlei.arouter_annotation.ARouter;
import com.xinrenlei.business.R;
import com.xinrenlei.common.business.BusinessDrawable;

/**
 * Auth：yujunyao
 * Since: 2020/12/11 1:54 PM
 * Email：yujunyao@xinrenlei.net
 * 自己决定暴露哪些图片资源
 */
@ARouter(path = "/business/drawable")
public class BusinessDrawableImpl implements BusinessDrawable {

    @Override
    public int getTestDrawable() {
        return R.drawable.icon_nav_01;
    }

}
