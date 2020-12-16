package com.xinrenlei.arouter_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.xinrenlei.arouter_annotation.ARouter;
import com.xinrenlei.arouter_annotation.RouterBean;
import com.xinrenlei.arouter_compiler.utils.ProcessorConfig;
import com.xinrenlei.arouter_compiler.utils.ProcessorUtils;

import java.io.IOException;
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
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

//模仿阿里的ARouter

// AutoService则是固定的写法，加个注解即可
// 通过auto-service中的@AutoService可以自动生成AutoService注解处理器，用来注册
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
//注解类型，让注解处理器处理
@SupportedAnnotationTypes({ProcessorConfig.AROUTER_PACKAGE})
//指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//注解处理器接收的参数
@SupportedOptions({ProcessorConfig.MODULE_NAME, ProcessorConfig.AROUTER_GENERATED_PATH})
public class ARouterProcessor extends AbstractProcessor {

    //操作Element的工具类(类、函数、属性其实都是Element)
    private Elements elementTool;

    //type（类信息）的工具类，包含用于操作TypeMirror的工具方法
    private Types typeTool;

    //打印日志相关信息
    private Messager messager;

    //文件生成器，类、资源等，就是最终要生成的文件，是需要Filer来完成的
    private Filer filer;

    private String moduleName;

    private String aRouterGeneratedPath;

    //eg: Map<"client", List<RouterBean>>
    private Map<String, List<RouterBean>> mAllPathMap = new HashMap<>();

    //eg: Map<"client", "ARouter$$Path$$personal.class">
    private Map<String, String> mAllGroupMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementTool = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        typeTool = processingEnvironment.getTypeUtils();

        moduleName = processingEnvironment.getOptions().get(ProcessorConfig.MODULE_NAME);
        aRouterGeneratedPath = processingEnvironment.getOptions().get(ProcessorConfig.AROUTER_GENERATED_PATH);

        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>>>> moduleName：" + moduleName);
        if (moduleName != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>> APT环境搭建完成");
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>> APT环境搭建有问题，请检查moduleName的值");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "没有发现被@ARouter注解标注的地方");
            return false;
        }

        //获取所有被@ARouter注解的元素结合
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);

        //通过Element工具类，获取Activity,Callback类型
        TypeElement activityType = elementTool.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE);
        //显示类信息（获取被注解的节点，类节点）
        TypeMirror activityMirror = activityType.asType();

        TypeElement drawableType = elementTool.getTypeElement(ProcessorConfig.AS_DRAWABLE);
        TypeMirror drawableMirror = drawableType.asType();

        //遍历所有的类节点
        for (Element element : elements) {
            //获取包节点
            String packageName = elementTool.getPackageOf(element).getQualifiedName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>> packageName : " + packageName);
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>> 被@ARouter注解的类 : " + className);

            //JavaPoet 练习，倒序封装思路生成文件源码   https://github.com/square/javapoet
            /**
             * package com.example.helloworld;
             *
             * public final class HelloWorld {
             *   public static void main(String[] args) {
             *     System.out.println("Hello, JavaPoet!");
             *   }
             * }
             */
            /*//1、方法
            MethodSpec mainMethod = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(String[].class, "args")
                    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                    .build();

            //2、类
            TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(mainMethod)
                    .build();

            //3、包
            JavaFile packageFile = JavaFile.builder(packageName, helloWorld).build();

            //生成文件
            try {
                packageFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.ERROR, ">>>>>>>>>>> 文件生成失败，请检查代码");
            }*/


            //拿到注解
            ARouter aRouter = element.getAnnotation(ARouter.class);

            //在循环里面对路由对象进行封装
            RouterBean routerBean = new RouterBean.Builder()
                    .addGroup(aRouter.group())
                    .addPath(aRouter.path())
                    .addElement(element)
                    .build();

            TypeMirror elementMirror = element.asType();
            //是否有android.app.Activity 的子类
            if (typeTool.isSubtype(elementMirror, activityMirror)) {
                routerBean.setTypeEnum(RouterBean.TypeEnum.ACTIVITY);
            }  else if (typeTool.isSubtype(elementMirror, drawableMirror)) {
                routerBean.setTypeEnum(RouterBean.TypeEnum.DRAWABLE);
            } else {
                throw new RuntimeException("@ARouter注解目前只能用在Activity类上面");
            }

            if (checkRouterPath(routerBean)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean info: " + routerBean.toString());

                List<RouterBean> routerBeans = mAllPathMap.get(routerBean.getGroup());
                if (ProcessorUtils.isEmpty(routerBeans)) {
                    routerBeans = new ArrayList<>();
                    routerBeans.add(routerBean);
                    mAllPathMap.put(routerBean.getGroup(), routerBeans);
                } else {
                    routerBeans.add(routerBean);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置。如/app/MainActivity");
            }

            //定义生成类实现的接口（Group Path）
            TypeElement pathType = elementTool.getTypeElement(ProcessorConfig.AROUTER_API_PATH);
            if (pathType == null) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Path接口路径: " + ProcessorConfig.AROUTER_API_PATH);
                messager.printMessage(Diagnostic.Kind.NOTE, "pathType 为空");
            }

            TypeElement groupType = elementTool.getTypeElement(ProcessorConfig.AROUTER_API_GROUP);
            if (groupType == null) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Group接口路径: " + ProcessorConfig.AROUTER_API_GROUP);
                messager.printMessage(Diagnostic.Kind.NOTE, "groupType 为空");
            }

            //第一步,Path文件
            try {
                createPathFile(pathType);
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE, "生成PATH模板是异常：" + e.getMessage());
            }

            //第二部，Group文件
            try {
                createGroupFile(groupType, pathType);
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE, "生成GROUP模板是异常：" + e.getMessage());
            }
        }

        //true 表示后续处理器不会再处理（已经处理完成）
        return true;
    }

    private void createGroupFile(TypeElement groupType, TypeElement pathType) throws IOException {
        if (ProcessorUtils.isEmpty(mAllGroupMap) || ProcessorUtils.isEmpty(mAllPathMap)) {
            return;
        }

        // 返回值 这一段 Map<String, Class<? extends ARouterPath>>
        TypeName methodReturn = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                //Class<? extends ARouterPath>
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))//? extends ARouterPath
                )
        );

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.GROUP_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturn);

        // Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();
        methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))
                ),
                ProcessorConfig.GROUP_VAR1,
                ClassName.get(HashMap.class)
                );

        //groupMap.put("order", ARouter$$Path$$order.class);
        for (Map.Entry<String, String> entry : mAllGroupMap.entrySet()) {
            methodBuilder.addStatement("$N.put($S, $T.class)",
                        ProcessorConfig.GROUP_VAR1,
                    entry.getKey(),
                    ClassName.get(aRouterGeneratedPath, entry.getValue())
                    );
        }

        methodBuilder.addStatement("return $N", ProcessorConfig.GROUP_VAR1);

        String finalClassName = ProcessorConfig.GROUP_FILE_NAME + moduleName;
        messager.printMessage(Diagnostic.Kind.NOTE, "APT生成的路由Group类文件：" + aRouterGeneratedPath + "." + finalClassName);

        //生成类文件ARouter$$Group$$client
        JavaFile.builder(aRouterGeneratedPath,
                TypeSpec.classBuilder(finalClassName)
                .addSuperinterface(ClassName.get(groupType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build())
                .build()
                .writeTo(filer);


    }

    private void createPathFile(TypeElement pathType) throws IOException {
        //判断map中是否有需要生成的文件
        if (ProcessorUtils.isEmpty(mAllPathMap)) {
            return;
        }

        TypeName methodReturn = ParameterizedTypeName.get(
                ClassName.get(Map.class),//Map
                ClassName.get(String.class),//Map<String,
                ClassName.get(RouterBean.class)//Map<String, RouterBean>
        );

        for (Map.Entry<String, List<RouterBean>> entry : mAllPathMap.entrySet()) {
            //1、方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.PATH_METHOD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturn);

            // Map<String, RouterBean> pathMap = new HashMap<>(); // $N == 变量 为什么是这个，因为变量有引用 所以是$N
            methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    ProcessorConfig.PATH_VAR1,
                    ClassName.get(HashMap.class)
            );

            //循环，会有多个
            // pathMap.put("/personal/Personal_MainActivity", RouterBean.create(RouterBean.TypeEnum.ACTIVITY));
            /**
             $N == 变量 变量有引用 所以 N
             $L == TypeEnum.ACTIVITY
             */
            List<RouterBean> pathList = entry.getValue();
            for (RouterBean routerBean : pathList) {
                methodBuilder.addStatement("$N.put($S, $T.create($T.$L, $T.class, $S, $S))",
                        ProcessorConfig.PATH_VAR1,
                        routerBean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.TypeEnum.class),
                        routerBean.getTypeEnum(),
                        ClassName.get((TypeElement) routerBean.getElement()),// MainActivity.class Main2Activity.class
                        routerBean.getPath(),
                        routerBean.getGroup()
                        );
            }

            methodBuilder.addStatement("return $N", ProcessorConfig.PATH_VAR1);

            // TODO 注意：不能像以前一样，1.方法，2.类  3.包， 因为这里面有implements ，所以 方法和类要合为一体生成才行，这是特殊情况

            //最终生成的类文件名，ARouter$$Path$$moduleName
            String finalClassName = ProcessorConfig.PATH_FILE_NAME + entry.getKey();

            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由Path类文件：" + aRouterGeneratedPath + "." + finalClassName);

            //生成类文件
            JavaFile.builder(aRouterGeneratedPath,//包名
                    TypeSpec.classBuilder(finalClassName)// 类名
                    .addSuperinterface(ClassName.get(pathType))// 实现ARouterLoadPath接口  implements ARouterPath==pathType
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build())
                    .build().writeTo(filer);

            mAllGroupMap.put(entry.getKey(), finalClassName);
        }

    }

    /**
     * 效验@ARouter注解的值，然后做自动补齐，例如group没有填的话就从path中截取
     * @param routerBean
     * @return
     */
    private boolean checkRouterPath(RouterBean routerBean) {
        String group = routerBean.getGroup();
        String path = routerBean.getPath();

        if (ProcessorUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中path的值必须以/开头");
            return false;
        }

        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
            return false;
        }


        if (!ProcessorUtils.isEmpty(group) && !group.equals(moduleName)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中group的值必须和子模块的模块名一样");
            return false;
        } else {
            routerBean.setGroup(group);
        }

        // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app,order,personal 作为group
        String tempGroup = path.substring(1, path.indexOf("/", 1));
        if (!ProcessorUtils.isEmpty(tempGroup) && !tempGroup.equals(moduleName)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中group的值必须和子模块的模块名一样");
            return false;
        } else {
            routerBean.setGroup(tempGroup);
        }

        return true;
    }
}