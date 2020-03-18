package com.tufusi.lab_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tufusi.lab_annotation.IFindActivity;
import com.tufusi.lab_annotation.LabActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description
 */
public class LabActivityProcessor extends BaseLabProcessor {

    private static final String ASSIGN_TARGET = "assignTarget";
    private static final String ASSIGN_TARGET_DOT = "assignTarget.";

    LabActivityProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    void process(Set<? extends Element> labElements) {
        for (Element annotationElement : labElements) {
            if (annotationElement.getKind() == ElementKind.CLASS) {
                LabActivity labActivity = annotationElement.getAnnotation(LabActivity.class);

                info("LabActivityProcessor process %s", annotationElement);

                try {
                    info("labActivity.methodName() %s", labActivity.methodName());

                    labActivity.activityApi();
                } catch (MirroredTypesException mte) { //抓取注解异常  当应用程序试图访问每个对应于 TypeMirror 的 Class 对象的序列时，抛出此异常。
                    DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirrors().get(0);

                    TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();

                    info("LabImplProcessor process clazz %s", TypeName.get(annotationElement.asType()));
                    try {
                        generateActivityFinder(labActivity.methodName(), classTypeElement, ClassName.get((TypeElement) annotationElement)).writeTo(processingEnv.getFiler());
                    } catch (Exception ex) {
                        info("LabActivityProcessor process error %s", ex);
                        ex.getStackTrace();
                    }
                }
            }
        }
    }

    private JavaFile generateActivityFinder(String path, TypeElement apiElement, ClassName activityClass) throws Exception {
        String qualifiedSuperClzName = apiElement.getQualifiedName().toString();

        TypeName stringList = ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(String.class));

        CodeBlock.Builder staticBlock = CodeBlock.builder()
                .addStatement(Constants.FIELD_PARAMNAME + " = new $T<>()", ArrayList.class);

        ClassName string = ClassName.get("java.lang", "String");
        TypeName listOfString = ParameterizedTypeName.get(Constants.LISTCLS, string);

        MethodSpec.Builder getParamNames = MethodSpec.methodBuilder(Constants.FIELD_PARAMNAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(listOfString)
                .addStatement("return " + Constants.FIELD_PARAMNAME);

        info("LabActivityProcessor  qualifiedSuperClzName  %s ", qualifiedSuperClzName);
        MethodSpec.Builder methodInject = MethodSpec.methodBuilder(Constants.METHOD_INJECT)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, "target")
                .addAnnotation(Override.class);


        methodInject.addStatement("$T " + ASSIGN_TARGET + "= ($T) target", activityClass, activityClass);

        for (Element element : apiElement.getEnclosedElements()) {
            if (element.getSimpleName().equals(element.getSimpleName())) {
                for (VariableElement variableElement : ((ExecutableElement) element).getParameters()) {
                    info("LabActivityProcessor parameter %s", variableElement.getSimpleName());

                    staticBlock.addStatement(Constants.FIELD_PARAMNAME + ".add($S)", variableElement.getSimpleName());
                    String originalValue = ASSIGN_TARGET_DOT + variableElement.getSimpleName();
                    String assignStatement = ASSIGN_TARGET_DOT + variableElement.getSimpleName() +
                            " = " + ASSIGN_TARGET_DOT + "getIntent().";
                    assignStatement = buildStatement(originalValue, assignStatement, variableElement.asType().toString(), variableElement.asType().getKind());

                    if (assignStatement.startsWith("com.tufusi.omphalos.core.LabJsonHelper")) {
                        assignStatement = ASSIGN_TARGET_DOT + variableElement.getSimpleName() + " = " + assignStatement;
                        methodInject.addStatement(assignStatement, variableElement.getSimpleName(), ClassName.get(variableElement.asType()));
                    } else {
                        methodInject.addStatement(assignStatement, variableElement.getSimpleName());
                    }
                }
            }
        }

        MethodSpec.Builder targetActivity = MethodSpec.methodBuilder(Constants.METHOD_GET_ACTIVITY_FIELD)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Class.class)
                .addStatement("return " + activityClass + ".class");

        String packageName = qualifiedSuperClzName.substring(0, qualifiedSuperClzName.lastIndexOf("."));
        String apiSimpleName = qualifiedSuperClzName.substring(qualifiedSuperClzName.lastIndexOf(".") + 1, qualifiedSuperClzName.length());

        TypeSpec impl = TypeSpec.classBuilder(apiSimpleName + Constants.CLASS_NAME_SEPARATOR + path + Constants.CLASS_NAME_SEPARATOR + Constants.ACTIVITY_HELPER_SUFFIX)
                .addSuperinterface(TypeName.get(IFindActivity.class))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(stringList, Constants.FIELD_PARAMNAME, Modifier.STATIC, Modifier.PRIVATE)
                .addStaticBlock(staticBlock.build())
                .addMethod(getParamNames.build())
                .addMethod(methodInject.build())
                .addMethod(targetActivity.build())

                .build();


        info("InjectProcessor process end ");

        return JavaFile.builder(packageName, impl).build();
    }

    private String buildStatement(String originalValue, String assignStatement, String type, TypeKind typeKind) {
        if (Constants.BOOLEAN.equals(type) || typeKind == TypeKind.BOOLEAN) {
            assignStatement += "getBooleanExtra($S, " + originalValue + ")";
        } else if (Constants.BYTE.equals(type) || typeKind == TypeKind.BYTE) {
            assignStatement += "getByteExtra($S, " + originalValue + ")";
        } else if (Constants.SHORT.equals(type) || typeKind == TypeKind.SHORT) {
            assignStatement += "getShortExtra($S, " + originalValue + ")";
        } else if (Constants.INTEGER.equals(type) || typeKind == TypeKind.INT) {
            assignStatement += "getIntExtra($S, " + originalValue + ")";
        } else if (Constants.LONG.equals(type) || typeKind == TypeKind.LONG) {
            assignStatement += "getLongExtra($S, " + originalValue + ")";
        } else if (typeKind == TypeKind.CHAR) {
            assignStatement += "getCharExtra($S, " + originalValue + ")";
        } else if (Constants.FLOAT.equals(type) || typeKind == TypeKind.FLOAT) {
            assignStatement += "getFloatExtra($S, " + originalValue + ")";
        } else if (Constants.DOUBLE.equals(type) || typeKind == TypeKind.DOUBLE) {
            assignStatement += "getDoubleExtra($S, " + originalValue + ")";
        } else if (Constants.STRING.equals(type)) {
            assignStatement += "getStringExtra($S)";
        } else {
            assignStatement = "com.tufusi.omphalos.core.LabJsonHelper.fromJson(" + ASSIGN_TARGET_DOT
                    + "getIntent().getStringExtra($S),new com.tufusi.lab_annotation.TypeHelper<$T>(){}.getType())";
        }

        return assignStatement;
    }
}
