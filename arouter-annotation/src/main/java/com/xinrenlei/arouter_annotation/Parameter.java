package com.xinrenlei.arouter_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auth：yujunyao
 * Since: 2020/12/9 1:57 PM
 * Email：yujunyao@xinrenlei.net
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Parameter {

    //不填写name的注解值表示该属性名就是key，填写了就用注解值作为key
    String name() default "";

}
