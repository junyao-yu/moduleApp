package com.xinrenlei.arouter_annotation;

import javax.lang.model.element.Element;

/**
 * Auth：yujunyao
 * Since: 2020/12/7 11:23 AM
 * Email：yujunyao@xinrenlei.net
 */

public class RouterBean {

    public enum TypeEnum {
        ACTIVITY,
        DRAWABLE//eg: a模块调用b模块的图片资源
    }

    private TypeEnum typeEnum;//枚举类型，eg: activity, drawable等等
    private Element element;
    private Class<?> clazz;//被注解的class对象
    private String path;
    private String group;

    private RouterBean(TypeEnum typeEnum, Class<?> clazz, String path, String group) {
        this.typeEnum = typeEnum;
        this.clazz = clazz;
        this.path = path;
        this.group = group;
    }

    public static RouterBean create(TypeEnum typeEnum, Class<?> clazz, String path, String group) {
        return new RouterBean(typeEnum, clazz, path, group);
    }

    public TypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(TypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public static class Builder {
        private TypeEnum typeEnum;
        private Element element;
        private Class<?> clazz;
        private String path;
        private String group;

        public Builder addType(TypeEnum typeEnum) {
            this.typeEnum = typeEnum;
            return this;
        }

        public Builder addElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder addClazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder addPath(String path) {
            this.path = path;
            return this;
        }

        public Builder addGroup(String group) {
            this.group = group;
            return this;
        }

        public RouterBean build() {
            if (path == null || path.length() == 0) {
                throw new IllegalArgumentException("path必须不为空");
            }
            return new RouterBean(this);
        }
    }

    private RouterBean(Builder builder) {
        this.typeEnum = builder.typeEnum;
        this.element = builder.element;
        this.clazz = builder.clazz;
        this.path = builder.path;
        this.group = builder.group;
    }

    @Override
    public String toString() {
        return "RouterBean{" +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
