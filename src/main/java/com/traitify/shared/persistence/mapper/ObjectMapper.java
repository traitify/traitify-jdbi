package com.traitify.shared.persistence.mapper;

import java.sql.ResultSet;

public interface ObjectMapper<T> {

    public T map(T instance, ResultSet resultSet);
}
