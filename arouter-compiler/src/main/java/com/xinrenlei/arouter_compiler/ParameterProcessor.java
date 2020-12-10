package com.xinrenlei.arouter_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xinrenlei.arouter_annotation.Parameter;
import com.xinrenlei.arouter_compiler.utils.ProcessorConfig;
import com.xinrenlei.arouter_compiler.utils.ProcessorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Auth：yujunyao
 * Since: 2020/12/9 2:00 PM
 * Email：yujunyao@xinrenlei.net
 */

@AutoService(Processor.class)
@SupportedAnnotationTypes({ProcessorConfig.PARAMETER_PACKAGE})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ParameterProcessor extends AbstractProcessor {

    private Elements elementUtils; // 类信息
    private Types typeUtils;  // 具体类型
    private Messager messager; // 日志
    private Filer filer;  // 生成器

    // 临时map存储，用来存放被@Parameter注解的属性集合，生成类文件时遍历
    // key:类节点, value:被@Parameter注解的属性集合
    private Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, "ParameterProcessor>>>进入init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        messager.printMessage(Diagnostic.Kind.NOTE, "ParameterProcessor>>>进入process");

        if (!ProcessorUtils.isEmpty(set)) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Parameter.class);

            for (Element element : elements) {
                // 字段节点的上一个节点 类节点==Key
                // 注解在属性的上面，属性节点父节点 是 类节点
                // enclosingElement == MainActivity == key
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

                if (tempParameterMap.containsKey(enclosingElement)) {
                    tempParameterMap.get(enclosingElement).add(element);
                } else {
                    List<Element> fields = new ArrayList<>();
                    fields.add(element);
                    tempParameterMap.put(enclosingElement, fields);
                }

            }

            if (ProcessorUtils.isEmpty(tempParameterMap)) return true;

            TypeElement activityType = elementUtils.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE);
            TypeElement parameterType = elementUtils.getTypeElement(ProcessorConfig.AROUTER_API_PARAMETER_GET);

            // 生成方法
            // Object targetParameter
            ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, ProcessorConfig.PARAMETER_NAME).build();

            for (Map.Entry<TypeElement, List<Element>> entry : tempParameterMap.entrySet()) {
                // key：   MainActivity
                // value： [name,sex,age]
                TypeElement typeElement = entry.getKey();

                //非Activity直接报错
                if (!typeUtils.isSubtype(typeElement.asType(), activityType.asType())) {
                    throw new RuntimeException("@Parameter注解只能作用在Activity上面");
                }

                //获取类名 == MainActivity
                ClassName className = ClassName.get(typeElement);

                ParameterFactory factory = new ParameterFactory.Builder(parameterSpec)
                        .setClassName(className)
                        .setMessager(messager)
                        .build();

                // MainActivity t = (MainActivity) activity;
                factory.addFirstStatement();

                //可能多行传参
                for (Element element : entry.getValue()) {
                    factory.addStatement(element);
                }

                //最终生成的类文件（类名$$Parameter） 例如：MainActivity$$Parameter
                String finalClassName = typeElement.getSimpleName() + ProcessorConfig.PARAMETER_FILE_NAME;
                messager.printMessage(Diagnostic.Kind.NOTE,
                        "ParameterProcessorAPT生成获取参数类文件：" + className.packageName() + "." + finalClassName);

                //开始生成文件  eg: MainActivity$$Parameter
                try {
                    JavaFile.builder(className.packageName(),
                            TypeSpec.classBuilder(finalClassName)
                    .addSuperinterface(ClassName.get(parameterType))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(factory.build())
                    .build())
                            .build().writeTo(filer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}
