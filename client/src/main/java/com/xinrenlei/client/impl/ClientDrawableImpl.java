package com.xinrenlei.client.impl;

import com.xinrenlei.arouter_annotation.ARouter;
import com.xinrenlei.client.R;
import com.xinrenlei.common.client.ClientDrawable;

/**
 * Auth：yujunyao
 * Since: 2020/12/11 1:57 PM
 * Email：yujunyao@xinrenlei.net
 * 自己决定要暴露的图片资源
 */
@ARouter(path = "/client/drawable")
public class ClientDrawableImpl implements ClientDrawable {

    @Override
    public int getTestDrawable() {
        return R.drawable.icon_nav_02;
    }

}
