package com.traitify.jdbi.mapper;

import com.traitify.jdbi.mapper.util.ReflectionUtil;
import com.traitify.jdbi.tables.BaseTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReflectionMapper<T> extends AbstractTableColumnMapper<T>{

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionMapper.class);

    private Map<String, Method> methodMap = new HashMap<>();
    private DependencyHandler<T> dependencyHandler;


    public ReflectionMapper(String tableAlias, BaseTable table, Class<T> typeClass){
        super(tableAlias, table, typeClass);
        dependencyHandler = new DependencyHandler<T>(methodMap);
    }

    @Override
    protected T mapAssociatedEntities(T instance, ResultSet resultSet) {
        return dependencyHandler.mapAssociatedEntities(instance, resultSet);
    }

    @Override
    protected T mapAdditionalColumns(T instance, ResultSet resultSet) {
        return dependencyHandler.mapAdditionalColumns(instance, resultSet);
    }

    @Override
    protected boolean mapColumn(T instance, String column, ResultSet resultSet) {
        boolean hasValue = false;
        Method setter = getSetterMethod(instance.getClass(), column);
        Class setterParamType = getSetterClass(setter);

        try {
            Object colVal = resultSet.getObject(getFullColumnName(column));

            if(colVal != null || !setter.getParameterTypes()[0].isPrimitive()){
                try{
                    Object val = colVal;
                    if(colVal instanceof Number){
                        Number num = (Number)colVal;

                        if(isInteger(setterParamType)){
                            val = num.intValue();
                        }else if(isLong(setterParamType)){
                            val = num.longValue();
                        }else if(setterParamType.isAssignableFrom(BigDecimal.class)){
                            val = new BigDecimal(num.doubleValue());
                        }else if(isFloat(setterParamType)){
                            val = num.floatValue();
                        }else if(isDouble(setterParamType)){
                            val = num.doubleValue();
                        }else if(isShort(setterParamType)){
                            val = num.shortValue();
                        }
                    }

                    invokeMethod(instance, setter, val);
                }catch (Throwable t){
                    LOGGER.error("Column: " + column + " - " + t.getMessage(), t);
                    throw t;
                }
            }

            if(colVal != null){
                hasValue = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return hasValue;
    }

    private Class getSetterClass(Method setter){
        return setter.getParameterTypes()[0];
    }


    private boolean isLong(Class type){
        if(type.isAssignableFrom(Long.class)){
            return true;
        }else if(type.isPrimitive() && type.getName().equals("long")){
            return true;
        }else {
            return false;
        }
    }

    private boolean isInteger(Class type){
        if(type.isAssignableFrom(Integer.class)){
            return true;
        }else if(type.isPrimitive() && type.getName().equals("int")){
            return true;
        }else {
            return false;
        }
    }

    private boolean isFloat(Class type){
        if(type.isAssignableFrom(Float.class)){
            return true;
        }else if(type.isPrimitive() && type.getName().equals("float")){
            return true;
        }else {
            return false;
        }
    }

    private boolean isDouble(Class type){
        if(type.isAssignableFrom(Double.class)){
            return true;
        }else if(type.isPrimitive() && type.getName().equals("double")){
            return true;
        }else {
            return false;
        }
    }

    private boolean isShort(Class type){
        if(type.isAssignableFrom(Short.class)){
            return true;
        }else if(type.isPrimitive() && type.getName().equals("short")){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected String getId(ResultSet resultSet) {
        try {
            return resultSet.getString(getFullColumnName("id"));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    protected Object invokeMethod(T instance, Method method, Object...args){
        return ReflectionUtil.invokeMethod(instance, method, args);
    }

    protected Method getSetterMethod(Class objClass, String columnName){
        return ReflectionUtil.getMethod(objClass, getSetterName(columnName), methodMap);
    }

    protected Method getGetterMethod(Class objClass, String columnName){
        return ReflectionUtil.getMethod(objClass, getGetterName(columnName), methodMap);
    }

    protected Method getMethod(Class objClass, String methodName){
        return ReflectionUtil.getMethod(objClass, methodName, methodMap);
    }

    protected String getSetterName(String beanName){
        return ReflectionUtil.getSetterName(beanName);
    }

    protected String getGetterName(String beanName){
        return ReflectionUtil.getGetterName(beanName);
    }

    public void addAssociatedEntityMapper(ObjectMapper<T> objectMapper){
        dependencyHandler.addAssociatedEntityMapper(objectMapper);
    }

    public void addAdditionColumn(String columnName, String beanName){
        dependencyHandler.addAdditionColumn(columnName, beanName);
    }

    public void addAdditionColumn(String columnName){
        dependencyHandler.addAdditionColumn(columnName);
    }
}
