package com.traitify.jdbi.mapper;


import java.sql.ResultSet;

public abstract class ContainerMapper <T> extends AbstractObjectMapper<T> {

    private DependencyHandler<T> dependencyHandler;

    protected abstract String getId(ResultSet resultSet);

    public ContainerMapper(Class<T> typeClass){
        super(typeClass);
        dependencyHandler = new DependencyHandler<>();
    }

    @Override
    public T map(T instance, ResultSet resultSet) {
        return instance; // Container mapper has nothing to do
    }

    @Override
    protected T mapAssociatedEntities(T instance, ResultSet resultSet) {
        return dependencyHandler.mapAssociatedEntities(instance, resultSet);
    }

    @Override
    protected T mapAdditionalColumns(T instance, ResultSet resultSet) {
        return dependencyHandler.mapAdditionalColumns(instance, resultSet);
    }

    public void addAssociatedEntityMapper(ObjectMapper<T> objectMapper) {
        dependencyHandler.addAssociatedEntityMapper(objectMapper);
    }

    public void addAdditionColumn(String columnName, String beanName) {
        dependencyHandler.addAdditionColumn(columnName, beanName);
    }

    public void addAdditionColumn(String columnName) {
        dependencyHandler.addAdditionColumn(columnName);
    }
}
