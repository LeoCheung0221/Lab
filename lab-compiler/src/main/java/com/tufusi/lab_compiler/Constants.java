package com.tufusi.lab_compiler;

import com.squareup.javapoet.ClassName;

public class Constants {

    // Java type
    private static final String LANG = "java.lang";
    static final String BYTE = LANG + ".Byte";
    static final String SHORT = LANG + ".Short";
    static final String INTEGER = LANG + ".Integer";
    static final String LONG = LANG + ".Long";
    static final String FLOAT = LANG + ".Float";
    static final String DOUBLE = LANG + ".Double";
    static final String BOOLEAN = LANG + ".Boolean";
    static final String STRING = LANG + ".String";

    //method name
    final static  String METHOD_GETAPIS = "getApis";
    final static  String METHOD_GETAPI_FIELD = "sameImplClass";
    final static  String FIELD_PARAMNAME = "paramNames";
    final static  String METHOD_GET_ACTIVITY_FIELD = "targetActivity";
    final static  String METHOD_INJECT = "inject";
    final static  String IMPL_INSTANCE = "sImplInstance";

    //file name feature
    final static  String ACTIVITY_HELPER_SUFFIX = "Helper";
    final static  String IMPL_HELPER_SUFFIX = "ImplHelper";
    final static  String CLASS_NAME_SEPARATOR = "_";

    static final ClassName SETCLS = ClassName.get("java.util", "Set");
    static final ClassName LISTCLS = ClassName.get("java.util", "List");

}
