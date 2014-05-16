package com.traitify.jdbi.mapper;

import com.traitify.jdbi.mapper.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;

public class AssociatedBeanMapper<T, B> implements ObjectMapper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssociatedBeanMapper.class);

    private Method setter;
    private Method getter;
    private String beanName;
    private AbstractObjectMapper<B> beanMapper;


    public AssociatedBeanMapper(String beanName, AbstractObjectMapper<B> beanMapper){
        this.beanName = beanName;
        this.beanMapper = beanMapper;
    }

    @Override
    public T map(T instance, ResultSet resultSet) {
        B bean = getBean(instance);

        bean = beanMapper.map(bean, resultSet);
        setBean(instance, bean);

        return instance;
    }

    private B getBean(T instance){
        Method getter = getGetter(instance);
        Object obj = ReflectionUtil.invokeMethod(instance, getter);

        if(obj == null){
            try {
                return (B)getter.getReturnType().newInstance();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        else{
            return (B)obj;
        }

        return null;
    }

    private void setBean(T instance, B bean){
        Method setter = getSetter(instance);
        ReflectionUtil.invokeMethod(instance, setter, bean);
    }

    private Method getSetter(T instance){
        if(setter != null){
            return setter;
        }

        setter = ReflectionUtil.getSetterMethod(instance.getClass(), beanName);

        if(setter == null){
            throw new IllegalArgumentException("No setter for [" + beanName + "] in class [" + instance.getClass().getName() + "]");
        }

        return setter;
    }

    private Method getGetter(T instance){
        if(getter != null){
            return getter;
        }

        getter = ReflectionUtil.getGetterMethod(instance.getClass(), beanName);

        if(getter == null){
            throw new IllegalArgumentException("No getter for [" + beanName + "] in class [" + instance.getClass().getName() + "]");
        }

        return getter;
    }
}
