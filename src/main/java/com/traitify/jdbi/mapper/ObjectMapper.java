package com.traitify.jdbi.mapper;

import java.sql.ResultSet;

public interface ObjectMapper<T> {

    public T map(T instance, String instanceId, ResultSet resultSet);
}
