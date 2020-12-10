package com.xinrenlei.arouter_api;

import java.util.Map;

/**
 * Auth：yujunyao
 * Since: 2020/12/7 11:22 AM
 * Email：yujunyao@xinrenlei.net
 */

public interface ARouterGroup {

    Map<String, Class<? extends ARouterPath>> getGroupMap();

}