package com.xinrenlei.arouter_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.xinrenlei.arouter_annotation.Parameter;
import com.xinrenlei.arouter_compiler.utils.ProcessorConfig;
import com.xinrenlei.arouter_compiler.utils.ProcessorUtils;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Auth：yujunyao
 * Since: 2020/12/9 3:04 PM
 * Email：yujunyao@xinrenlei.net
 */

/*
   目的 生成以下代码：
        @Override
      public void getParameter(Object activity) {
        MainActivity t = (MainActivity) activity;
        t.name = t.getIntent().getStringExtra("name");
        t.age = t.getIntent().getIntExtra("age", t.age);
        t.isAdult = t.getIntent().getBooleanExtra("isAdult", t.isAdult);
      }
 */
public class ParameterFactory {

    private MethodSpec.Builder method;

    private ClassName className;

    private Messager messager;

    private TypeMirror drawableMirror;

    private Types typeUtils;

    public void addFirstStatement() {
        method.addStatement("$T t = ($T) " + ProcessorConfig.PARAMETER_NAME, className, className);
    }

    public void addStatement(Element element) {
        //遍历注解的属性节点，生成函数体
        TypeMirror typeMirror = element.asType();

        //获取TypeKind 枚举类型的序列号
        int type = typeMirror.getKind().ordinal();

        //获取属性名
        String fieldName = element.getSimpleName().toString();

        //获取注解的值
        String annotationValue = element.getAnnotation(Parameter.class).name();

        // 配合： t.age = t.getIntent().getBooleanExtra("age", t.age ==  9);
        // 判断注解的值为空的情况下的处理（注解中有name值就用注解值）
        annotationValue = ProcessorUtils.isEmpty(annotationValue) ? fieldName : annotationValue;

        String value = "t." + fieldName;
        String methodContent = value + " = t.getIntent().";

        if (type == TypeKind.INT.ordinal()) {
            // t.s = t.getIntent().getIntExtra("age", t.age);
            methodContent += "getIntExtra($S, " + value + ")";//默认值
            method.addStatement(methodContent, annotationValue);
        } else if (type == TypeKind.BOOLEAN.ordinal()) {
            // t.s = t.getIntent().getBooleanExtra("isSuccess", t.age);
            methodContent += "getBooleanExtra($S, " + value + ")";//默认值
            method.addStatement(methodContent, annotationValue);
        } else {//String 类型没有序列号提供
            // t.s = t.getIntent.getStringExtra("s");
            // typeMirror.toString() java.lang.String
            if (typeMirror.toString().equalsIgnoreCase(ProcessorConfig.STRING)) {
                methodContent += "getStringExtra($S)";//没有默认值
                method.addStatement(methodContent, annotationValue);
            } else if (typeUtils.isSubtype(typeMirror, drawableMirror)) {//ASDrawable接口
                // t.orderDrawable = (OrderDrawable) RouterManager.getInstance().build("/order/getDrawable").navigation(t);
                methodContent = value + " = ($T) $T.getInstance().build($S).navigation(t)";
                method.addStatement(methodContent,
                        TypeName.get(typeMirror),
                        ClassName.get(ProcessorConfig.AROUTER_API_PACKAGE, ProcessorConfig.ROUTER_MANAGER),
                        annotationValue);
            } else if (typeMirror.toString().equalsIgnoreCase(ProcessorConfig.SERIALIZABLE)) {
                methodContent = "t.getIntent().getSerializableExtra($S)";
                method.addStatement(methodContent, annotationValue);
            }
        }

//        if (!methodContent.endsWith(")")) {
//            messager.printMessage(Diagnostic.Kind.ERROR, "目前暂支持String, int boolean传参");
//        } else {
//            method.addStatement(methodContent, annotationValue);
//        }
    }

    public MethodSpec build() {
        return method.build();
    }

    private ParameterFactory(Builder builder) {
        this.messager = builder.messager;
        this.className = builder.className;
        this.typeUtils = builder.typeUtils;

        method = MethodSpec.methodBuilder(ProcessorConfig.PARAMETER_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.parameterSpec);

        drawableMirror = builder.elementUtils.getTypeElement(ProcessorConfig.AS_DRAWABLE).asType();
    }

    public static class Builder {
        private Messager messager;

        private ClassName className;

        private ParameterSpec parameterSpec;

        private Elements elementUtils;

        private Types typeUtils;

        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }

        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }

        public Builder setElementUtils(Elements elementUtils) {
            this.elementUtils = elementUtils;
            return this;
        }

        public Builder setTypeUtils(Types typeUtils) {
            this.typeUtils = typeUtils;
            return this;
        }

        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }

        public ParameterFactory build() {
            if (parameterSpec == null) {
                throw new IllegalArgumentException("ParameterFactory >>>>> parameterSpec为空");
            }

            if (className == null) {
                throw new IllegalArgumentException("ParameterFactory >>>>> className为空");
            }

            if (messager == null) {
                throw new IllegalArgumentException("ParameterFactory >>>>> messager为空");
            }

            return new ParameterFactory(this);
        }

    }

}
