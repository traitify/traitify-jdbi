package com.traitify.jdbi.mapper;

import com.traitify.jdbi.mapper.util.TableUtil;
import com.traitify.jdbi.tables.BaseTable;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractObjectMapper<T> implements ResultSetMapper<T>, ObjectMapper<T> {

    private String tableAlias;
    private BaseTable table;
    private Class<T> typeClass;
    private Map<String, T> idToInstanceMap = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractObjectMapper.class);

    protected abstract String getId(ResultSet resultSet);
    protected abstract T mapAssociatedEntities(T instance, ResultSet resultSet);


    public AbstractObjectMapper(String tableAlias, BaseTable table, Class<T> typeClass){
        this.tableAlias = tableAlias;
        this.table = table;
        this.typeClass = typeClass;
    }

    public T mapObject(ResultSet resultSet){
        boolean isNewObject = isNewObject(resultSet);
        T instance = map(getInstance(resultSet), resultSet);

        if(instance == null){
            return null;
        }

        mapAssociatedEntities(getInstance(resultSet), resultSet);
        postMap(getInstance(resultSet), resultSet);

        // Returning an existing instance causes duplicates in the returned results
        if(isNewObject){
            return instance;
        }

        // Return NULL for existing instances
        return null;
    }

    public T map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException{
        return mapObject(resultSet);
    }

    protected String getFullColumnName(String baseColumnName){
        return TableUtil.getFullColumnName(tableAlias, baseColumnName);
    }

    protected BaseTable getTable(){
        return table;
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

    public T postMap(T instance, ResultSet resultSet){
        return instance; // Override to do something
    }
}
