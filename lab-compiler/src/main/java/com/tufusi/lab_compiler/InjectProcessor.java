package com.tufusi.lab_compiler;

import com.google.auto.service.AutoService;
import com.tufusi.lab_annotation.LabActivity;
import com.tufusi.lab_annotation.LabInject;
import com.tufusi.lab_annotation.ParamName;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description 注解动态生成器
 * 自定义AbstractProcessor的作用是在编译时生成相关的java源文件
 * 利用注解动态生成代码
 */
// 自动注册
// 编译后AutoService会自动在META-INF文件夹下生成Processor配置信息文件，该文件里就是实现该服务接口的具体实现类。
// 而当外部程序装配这个模块的时候，就能通过该jar包META-INF/services/里的配置文件找到具体的实现类名，并装载实例化，完成模块的注入。
@AutoService(Processor.class)
@SupportedSourceVersion(value = SourceVersion.RELEASE_7)
public class InjectProcessor extends AbstractProcessor {

    private LabImplProcessor mLabImplProcessor;
    private LabActivityProcessor mLabActivityProcessor;

    /**
     * 每一个注解处理器类都必须有一个空的构造函数
     * 这里的init()是比较特殊的，它会被注解处理工具调用，并输入ProcessingEnvironment参数
     *
     * @param processingEnv ProcessingEnvironment提供很多有用的工具类Elements,Types和Filer。
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mLabImplProcessor = new LabImplProcessor(processingEnv);
        mLabActivityProcessor = new LabActivityProcessor(processingEnv);
    }

    /**
     * 这里必须指定：这个注解处理器注册给哪个注解的
     *
     * @return 是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称
     * 也就是说，在这里定义你的注解处理器注册到哪些注解上
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> processAnnotation = new HashSet<>();
        processAnnotation.add(ParamName.class.getCanonicalName());
        processAnnotation.add(LabInject.class.getCanonicalName());
        processAnnotation.add(LabActivity.class.getCanonicalName());
        return processAnnotation;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mLabImplProcessor.process(roundEnv.getElementsAnnotatedWith(LabInject.class));
        mLabActivityProcessor.process(roundEnv.getElementsAnnotatedWith(LabActivity.class));
        mLabActivityProcessor.process(roundEnv.getElementsAnnotatedWith(ParamName.class));

        return false;
    }
}
