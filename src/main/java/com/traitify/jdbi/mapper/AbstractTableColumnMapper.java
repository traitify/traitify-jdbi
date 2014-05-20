package com.traitify.jdbi.mapper;

import com.traitify.jdbi.tables.BaseTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public abstract class AbstractTableColumnMapper<T> extends AbstractTableObjectMapper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionMapper.class);

    protected abstract boolean mapColumn(T instance, String column, ResultSet resultSet);
    protected abstract String getId(ResultSet resultSet);
    protected abstract T mapAssociatedEntities(T instance, ResultSet resultSet);

    public AbstractTableColumnMapper(String tableAlias, BaseTable table, Class<T> typeClass){
        super(tableAlias, table, typeClass);
    }

    @Override
    public T map(T instance, ResultSet resultSet) {
        String[] columns = getTable().getColumns();

        boolean emptyObject = true;
        for(String column : columns){
            boolean columnHasValue = mapColumn(instance, column, resultSet);

            if(columnHasValue){
                emptyObject = false;
            }
        }

        if(emptyObject){
            return null;
        }

        return instance;
    }
}
