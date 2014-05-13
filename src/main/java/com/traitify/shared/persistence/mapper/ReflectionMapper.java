package com.traitify.shared.persistence.mapper;

import com.traitify.shared.persistence.mapper.util.ReflectionUtil;
import com.traitify.shared.persistence.tables.BaseTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionMapper<T> extends AbstractColumnMapper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionMapper.class);

    private Map<String, Method> methodMap = new HashMap<>();
    private List<ObjectMapper<T>> associtedEntityMappers = new ArrayList<>();


    public ReflectionMapper(String tableAlias, BaseTable table, Class<T> typeClass){
        super(tableAlias, table, typeClass);
    }

    @Override
    protected T mapAssociatedEntities(T instance, ResultSet resultSet) {
        for(ObjectMapper<T> objectMapper : associtedEntityMappers){
            if(objectMapper != null){
                instance = objectMapper.map(instance, resultSet);
            }
        }

        return instance;
    }

    @Override
    protected boolean mapColumn(T instance, String column, ResultSet resultSet) {
        boolean hasValue = false;
        Method setter = getSetterMethod(instance.getClass(), column);

        try {
            Object colVal = resultSet.getObject(getFullColumnName(column));
            invokeMethod(instance, setter, colVal);

            if(colVal != null){
                hasValue = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return hasValue;
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
        associtedEntityMappers.add(objectMapper);
    }
}
