package com.tufusi.lab_compiler;

import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description 注解处理器抽象基类
 */
public abstract class BaseLabProcessor {

    //日志相关辅助类
    private Messager mMessager;

    protected ProcessingEnvironment processingEnv;

    BaseLabProcessor(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        mMessager = processingEnv.getMessager();
    }

    protected void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    protected void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

    /**
     * 抽象出处理函数
     *
     * @param labElements 元素集合
     */
    abstract void process(Set<? extends Element> labElements);

}
