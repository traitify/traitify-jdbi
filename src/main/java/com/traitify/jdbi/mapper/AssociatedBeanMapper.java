package com.traitify.jdbi.mapper;

import com.traitify.jdbi.mapper.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;

public class AssociatedBeanMapper<T, B> implements ObjectMapper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssociatedBeanMapper.class);

    private Method setter;
    private String beanName;
    private AbstractObjectMapper<B> beanMapper;


    public AssociatedBeanMapper(String beanName, AbstractObjectMapper<B> beanMapper){
        this.beanName = beanName;
        this.beanMapper = beanMapper;
    }

    @Override
    public T map(T instance, ResultSet resultSet) {
        setBean(instance, beanMapper.mapObject(resultSet));
        return instance;
    }

    private void setBean(T instance, B bean){
        if(bean != null){
            Method setter = getSetter(instance);
            ReflectionUtil.invokeMethod(instance, setter, bean);
        }
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
}
