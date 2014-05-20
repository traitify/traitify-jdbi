package com.traitify.jdbi.mapper;

import com.traitify.jdbi.mapper.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DependencyHandler<T>{

    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyHandler.class);

    private List<ObjectMapper<T>> associatedEntityMappers = new ArrayList<>();
    private Map<String, String> additionalColumnToBeanMap = new HashMap<>();
    private Map<String, Method> methodMap = new HashMap<>();

    public DependencyHandler(){
    }

    public DependencyHandler(Map<String, Method> methodMap){
        this.methodMap = methodMap;
    }

    protected T mapAssociatedEntities(T instance, ResultSet resultSet){
        for(ObjectMapper<T> objectMapper : associatedEntityMappers){
            if(objectMapper != null){
                instance = objectMapper.map(instance, resultSet);
            }
        }

        return instance;
    }

    protected T mapAdditionalColumns(T instance, ResultSet resultSet){
        for(String column : additionalColumnToBeanMap.keySet()){
            Method setter = ReflectionUtil.getSetterMethod(instance.getClass(), additionalColumnToBeanMap.get(column), methodMap);

            try {
                Object colVal = resultSet.getObject(column);

                if(colVal != null || !setter.getParameterTypes()[0].isPrimitive()){
                    ReflectionUtil.invokeMethod(instance, setter, colVal);
                }

            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return instance;
    }

    public void addAssociatedEntityMapper(ObjectMapper<T> objectMapper){
        associatedEntityMappers.add(objectMapper);
    }

    public void addAdditionColumn(String columnName, String beanName){
        additionalColumnToBeanMap.put(columnName, beanName);
    }

    public void addAdditionColumn(String columnName){
        additionalColumnToBeanMap.put(columnName, columnName);
    }
}
