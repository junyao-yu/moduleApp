package com.xinrenlei.arouter_compiler.utils;

/**
 * Auth：yujunyao
 * Since: 2020/12/6 5:04 PM
 * Email：yujunyao@xinrenlei.net
 */

public interface ProcessorConfig {

    String AROUTER_PACKAGE = "com.xinrenlei.arouter_annotation.ARouter";

    String MODULE_NAME = "moduleName";

    String AROUTER_GENERATED_PATH = "aRouterGeneratedPath";

    // Activity全类名
    String ACTIVITY_PACKAGE = "android.app.Activity";

    String AROUTER_API_PACKAGE = "com.xinrenlei.arouter_api";

    String AS_DRAWABLE = AROUTER_API_PACKAGE + ".AsDrawable";

    String AROUTER_API_GROUP = AROUTER_API_PACKAGE + ".ARouterGroup";

    String AROUTER_API_PATH = AROUTER_API_PACKAGE + ".ARouterPath";

    String PATH_METHOD_NAME = "getPathMap";

    String GROUP_METHOD_NAME = "getGroupMap";

    // 路由组，中的 Path 里面 的 变量名 1
    String PATH_VAR1 = "pathMap";

    // 路由组，中的 Group 里面 的 变量名 1
    String GROUP_VAR1 = "groupMap";

    // 路由组，PATH 最终要生成的 文件名
    String PATH_FILE_NAME = "ARouter$$Path$$";

    // 路由组，GROUP 最终要生成的 文件名
    String GROUP_FILE_NAME = "ARouter$$Group$$";

    String PARAMETER_PACKAGE = "com.xinrenlei.arouter_annotation.Parameter";

    String AROUTER_API_PARAMETER_GET = AROUTER_API_PACKAGE + ".ParameterGet";

    // ARouter api 的 ParmeterGet 方法的名字
    String PARAMETER_METHOD_NAME = "getParameter";

    // ARouter api 的 ParameterGet 方法参数的名字
    String PARAMETER_NAME = "activity";

    // String全类名
    String STRING = "java.lang.String";

    //Serializable类全名
    String SERIALIZABLE = "java.io.Serializable";

    // ARouter aip 的 ParmeterGet 的 生成文件名称 $$Parameter
    String PARAMETER_FILE_NAME = "$$Parameter";

    String ROUTER_MANAGER = "RouterManager";

}
