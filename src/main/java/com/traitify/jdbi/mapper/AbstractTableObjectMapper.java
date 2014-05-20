package com.traitify.jdbi.mapper;

import com.traitify.jdbi.mapper.util.TableUtil;
import com.traitify.jdbi.tables.BaseTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTableObjectMapper<T> extends AbstractObjectMapper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTableObjectMapper.class);

    private String tableAlias;
    private BaseTable table;

    public AbstractTableObjectMapper(String tableAlias, BaseTable table, Class<T> typeClass){
        super(typeClass);
        this.tableAlias = tableAlias;
        this.table = table;
    }

    protected String getFullColumnName(String baseColumnName){
        return TableUtil.getFullColumnName(tableAlias, baseColumnName);
    }

    protected BaseTable getTable(){
        return table;
    }
}
