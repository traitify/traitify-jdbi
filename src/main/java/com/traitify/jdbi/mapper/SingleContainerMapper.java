package com.traitify.jdbi.mapper;

import java.sql.ResultSet;
import java.util.UUID;

public class SingleContainerMapper<T> extends ContainerMapper<T>{

    private String id = UUID.randomUUID().toString();

    public SingleContainerMapper(Class<T> typeClass) {
        super(typeClass);
    }

    @Override
    protected String getId(ResultSet resultSet, String parentId) {
        return id;
    }
}
