package com.traitify.jdbi.mapper.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ReflectionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

    public static Object invokeMethod(Object instance, Method method, Object...args){
        if(method != null){
            try {
                return method.invoke(instance, args);
            } catch (IllegalAccessException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return null;
    }

    public static Method getSetterMethod(Class objClass, String columnName){
        return getSetterMethod(objClass, columnName, null);
    }

    public static Method getSetterMethod(Class objClass, String columnName, Map<String, Method> methodMap){
        return getMethod(objClass, getSetterName(columnName), methodMap);
    }

    public static Method getGetterMethod(Class objClass, String columnName){
        return getGetterMethod(objClass, columnName, null);
    }

    public static Method getGetterMethod(Class objClass, String columnName, Map<String, Method> methodMap){
        return getMethod(objClass, getGetterName(columnName), methodMap);
    }

    public static Method getMethod(Class objClass, String methodName, Map<String, Method> methodMap){
        if(methodMap != null && methodMap.containsKey(methodName)){
            return methodMap.get(methodName);
        }

        for(Method method : objClass.getDeclaredMethods()){
            if(method.getName().equals(methodName)){
                if(methodMap != null){
                    methodMap.put(methodName, method);
                }

                return method;
            }
        }

        return null;
    }

    public static String getSetterName(String beanName){
        return "set" + StringUtils.capitalize(beanName);
    }

    public static String getGetterName(String beanName){
        return "get" + StringUtils.capitalize(beanName);
    }    
}
