package com.tufusi.lab_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tufusi.lab_annotation.IFindImplClz;
import com.tufusi.lab_annotation.LabInject;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description
 */
public class LabImplProcessor extends BaseLabProcessor {

    LabImplProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    void process(Set<? extends Element> labElements) {
        for (Element annotationElement : labElements) {
            if (annotationElement.getKind() == ElementKind.CLASS) {
                LabInject labInject = annotationElement.getAnnotation(LabInject.class);

                String qualifiedSuperClassName;
                info("LabImplProcessor process %s", annotationElement);
                try {
                    labInject.api();
                } catch (MirroredTypesException mte) { //抓取注解异常  当应用程序试图访问每个对应于 TypeMirror 的 Class 对象的序列时，抛出此异常。
                    Set<String> apiClassNames = new HashSet<>();
                    //遍历 对应于要访问的类型的类型镜像
                    for (TypeMirror typeMirror : mte.getTypeMirrors()) {
                        DeclaredType classTypeMirror = (DeclaredType) typeMirror;

                        TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                        //返回此类型元素的完全限定名称。更准确地说,返回规范 名称。
                        qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();

                        apiClassNames.add(qualifiedSuperClassName);

                        info("LabImplProcessor process clazz %s", TypeName.get(annotationElement.asType()));
                    }

                    for (String apiClassName : apiClassNames) {
                        try {
                            generateApiFinder(apiClassName, annotationElement.toString(), apiClassNames).writeTo(processingEnv.getFiler());
                        } catch (Exception ex) {
                            info("LabImplProcessor process error %s", ex);
                            ex.getStackTrace();
                        }
                    }
                }
            }
        }
    }

    private JavaFile generateApiFinder(String qualifiedSuperClzName, String implClzName, Set<String> sameImplApiClass) {
        //获取类型参数
        TypeName stringSet = ParameterizedTypeName.get(ClassName.get(Set.class), ClassName.get(Class.class));
        TypeName newStaticInstance = TypeName.get(Object.class);

        //用来描述代码块的内容,包括普通的赋值,if判断,循环判断等
        CodeBlock.Builder staticBlock = CodeBlock.builder()
                .addStatement(Constants.METHOD_GETAPI_FIELD + " = new $T<>()", HashSet.class)
                .addStatement(Constants.IMPL_INSTANCE + " = new " + implClzName + "()");

        for (String api : sameImplApiClass) {
            staticBlock.addStatement(Constants.METHOD_GETAPI_FIELD + ".add(" + api + ".class)");
        }

        ClassName clz = ClassName.get("java.lang", "Class");
        TypeName setOfClass = ParameterizedTypeName.get(Constants.SETCLS, clz);

        MethodSpec.Builder getSameImplApis = MethodSpec.methodBuilder(Constants.METHOD_GETAPIS)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(setOfClass)
                .addStatement("return " + Constants.METHOD_GETAPI_FIELD);

        MethodSpec.Builder newInstance = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Object.class)
                .addStatement("return " + Constants.IMPL_INSTANCE);

        //构建生成整个类
        String packageName = qualifiedSuperClzName.substring(0, qualifiedSuperClzName.lastIndexOf("."));
        String apiSimpleName = qualifiedSuperClzName.substring(qualifiedSuperClzName.lastIndexOf(".") + 1);

        TypeSpec impl = TypeSpec.classBuilder(apiSimpleName + Constants.CLASS_NAME_SEPARATOR + Constants.IMPL_HELPER_SUFFIX)
                .addSuperinterface(TypeName.get(IFindImplClz.class))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(newInstance.build())
                .addField(stringSet, Constants.METHOD_GETAPI_FIELD, Modifier.FINAL, Modifier.STATIC, Modifier.PRIVATE)
                .addField(newStaticInstance, Constants.IMPL_INSTANCE, Modifier.FINAL, Modifier.STATIC, Modifier.PRIVATE)
                .addStaticBlock(staticBlock.build())
                .addMethod(getSameImplApis.build())
                .build();

        return JavaFile.builder(packageName, impl).build();
    }
}
