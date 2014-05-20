package com.traitify.jdbi.mapper;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractObjectMapper<T> implements ResultSetMapper<T>, ObjectMapper<T>{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractObjectMapper.class);

    private Class<T> typeClass;
    private Map<String, T> idToInstanceMap = new HashMap<>();

    protected abstract String getId(ResultSet resultSet);
    protected abstract T mapAssociatedEntities(T instance, ResultSet resultSet);
    protected abstract T mapAdditionalColumns(T instance, ResultSet resultSet);

    public AbstractObjectMapper(Class<T> typeClass){
        this.typeClass = typeClass;
    }

    protected T mapObject(ResultSet resultSet){
        boolean isNewObject = isNewObject(resultSet);
        T instance = map(getInstance(resultSet), resultSet);

        if(instance == null){
            return null;
        }

        mapAdditionalColumns(getInstance(resultSet), resultSet);
        mapAssociatedEntities(getInstance(resultSet), resultSet);
        postMap(getInstance(resultSet), resultSet);

        // Returning an existing instance causes duplicates in the returned results
        if(isNewObject){
            return instance;
        }

        // Return NULL for existing instances
        return null;
    }

    @Override
    public T map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {
        return mapObject(resultSet);
    }

    protected Class<T> getTypeClass(){
        return typeClass;
    }

    protected boolean isNewObject(ResultSet resultSet){
        return !idToInstanceMap.containsKey(getId(resultSet));
    }

    protected T getInstance(ResultSet resultSet){
        String id = getId(resultSet);

        if(idToInstanceMap.containsKey(id)){
            return idToInstanceMap.get(id);
        }

        T instance = null;
        try {
            instance = getTypeClass().newInstance();
            idToInstanceMap.put(id, instance);
        } catch (InstantiationException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return instance;
    }

    protected T postMap(T instance, ResultSet resultSet){
        return instance; // Override to do something
    }
}
