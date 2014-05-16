package com.traitify.jdbi.mapper;

import com.traitify.jdbi.mapper.util.ReflectionUtil;
import com.traitify.jdbi.mapper.util.TableUtil;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AssociatedCollectionMapper<T, I> implements ObjectMapper<T> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AssociatedCollectionMapper.class);

    private String beanName;
    private AbstractObjectMapper<I> itemMapper;
    private String tableAlias;
    private String columnName;


    public AssociatedCollectionMapper(String beanName, AbstractObjectMapper<I> itemMapper){
        this.beanName = beanName;
        this.itemMapper = itemMapper;
    }

    public AssociatedCollectionMapper(String beanName, String columnName){
        this.beanName = beanName;
        this.columnName = columnName;
    }

    public AssociatedCollectionMapper(String beanName, String tableAlias, String columnName){
        this.beanName = beanName;
        this.tableAlias = tableAlias;
        this.columnName = columnName;
    }

    @Override
    public T map(T instance, ResultSet resultSet) {
        Collection collection = getCollection(instance);
        I item = getItem(resultSet);

        if(item != null){
            collection.add(item);
            setCollection(instance, collection);
        }

        return instance;
    }

    private I getItem(ResultSet resultSet){
        if(itemMapper != null){
            return itemMapper.mapObject(resultSet);
        }

        try {
            return (I)resultSet.getObject(TableUtil.getFullColumnName(tableAlias, columnName));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    private Method getGetter(T instance){
        Method getter = ReflectionUtil.getGetterMethod(instance.getClass(), beanName);

        if(getter == null){
            throw new IllegalArgumentException("No getter for [" + beanName + "] in class [" + instance.getClass().getName() + "]");
        }

        return getter;
    }

    private Method getSetter(T instance){
        Method setter = ReflectionUtil.getSetterMethod(instance.getClass(), beanName);

        if(setter == null){
            throw new IllegalArgumentException("No setter for [" + beanName + "] in class [" + instance.getClass().getName() + "]");
        }

        return setter;
    }

    private void setCollection(T instance, Collection collection){
        Method setter = getSetter(instance);
        ReflectionUtil.invokeMethod(instance, setter, collection);
    }

    private Collection getCollection(T instance){
        Method getter = getGetter(instance);
        Object obj = ReflectionUtil.invokeMethod(instance, getter);

        if(getter.getReturnType().isAssignableFrom(List.class)){
            return getList(obj);
        }
        else if(getter.getReturnType().isAssignableFrom(Set.class)){
            return getSet(obj);
        }

        throw new IllegalArgumentException("Unsupported collection type: " + getter.getReturnType().getName());
    }

    private List getList(Object collection){
        if(collection != null){
            return (List)collection;
        }

        return new ArrayList();
    }

    private Set getSet(Object collection){
        if(collection != null){
            return (Set)collection;
        }

        return new HashSet();
    }
}
