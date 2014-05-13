package com.traitify.shared.persistence.jdbi;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.exceptions.TransactionFailedException;
import org.skife.jdbi.v2.tweak.*;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class CloseableHandle implements Handle, AutoCloseable {

    private final Handle handle;

    public CloseableHandle(Handle handle) {
        this.handle = handle;
        handle.registerContainerFactory(new NoNullItemsContainerFactory());
    }

    @Override
    public void close() {
        handle.close();
    }

    @Override
    public void define(String s, Object o) {
        handle.define(s, o);
    }

    @Override
    public Handle begin() {
        return handle.begin();
    }

    @Override
    public Handle commit() {
        return handle.commit();
    }

    @Override
    public Handle rollback() {
        return handle.rollback();
    }

    @Override
    public Handle rollback(String s) {
        return handle.rollback(s);
    }

    @Override
    public boolean isInTransaction() {
        return handle.isInTransaction();
    }

    @Override
    public Query<Map<String, Object>> createQuery(String s) {
        return handle.createQuery(s);
    }

    @Override
    public Update createStatement(String s) {
        return handle.createStatement(s);
    }

    @Override
    public Call createCall(String s) {
        return handle.createCall(s);
    }

    @Override
    public int insert(String s, Object... objects) {
        return handle.insert(s, objects);
    }

    @Override
    public int update(String s, Object... objects) {
        return handle.update(s, objects);
    }

    @Override
    public PreparedBatch prepareBatch(String s) {
        return handle.prepareBatch(s);
    }

    @Override
    public Batch createBatch() {
        return handle.createBatch();
    }

    @Override
    public <ReturnType> ReturnType inTransaction(TransactionCallback<ReturnType> returnTypeTransactionCallback) throws TransactionFailedException {
        return handle.inTransaction(returnTypeTransactionCallback);
    }

    @Override
    public <ReturnType> ReturnType inTransaction(TransactionIsolationLevel transactionIsolationLevel, TransactionCallback<ReturnType> returnTypeTransactionCallback) throws TransactionFailedException {
        return handle.inTransaction(transactionIsolationLevel, returnTypeTransactionCallback);
    }

    @Override
    public List<Map<String, Object>> select(String s, Object... objects) {
        return handle.select(s, objects);
    }

    @Override
    public void setStatementLocator(StatementLocator statementLocator) {
        handle.setStatementLocator(statementLocator);
    }

    @Override
    public void setStatementRewriter(StatementRewriter statementRewriter) {
        handle.setStatementRewriter(statementRewriter);
    }

    @Override
    public Script createScript(String s) {
        return handle.createScript(s);
    }

    @Override
    public void execute(String s, Object... objects) {
        handle.execute(s, objects);
    }

    @Override
    public Handle checkpoint(String s) {
        return handle.checkpoint(s);
    }

    @Override
    public Handle release(String s) {
        return handle.release(s);
    }

    @Override
    public void setStatementBuilder(StatementBuilder statementBuilder) {
        handle.setStatementBuilder(statementBuilder);
    }

    @Override
    public void setSQLLog(SQLLog sqlLog) {
        handle.setSQLLog(sqlLog);
    }

    @Override
    public void setTimingCollector(TimingCollector timingCollector) {
        handle.setTimingCollector(timingCollector);
    }

    @Override
    public void registerMapper(ResultSetMapper resultSetMapper) {
        handle.registerMapper(resultSetMapper);
    }

    @Override
    public void registerMapper(ResultSetMapperFactory resultSetMapperFactory) {
        handle.registerMapper(resultSetMapperFactory);
    }

    @Override
    public <SqlObjectType> SqlObjectType attach(Class<SqlObjectType> sqlObjectTypeClass) {
        return handle.attach(sqlObjectTypeClass);
    }

    @Override
    public void setTransactionIsolation(TransactionIsolationLevel transactionIsolationLevel) {
        handle.setTransactionIsolation(transactionIsolationLevel);
    }

    @Override
    public void setTransactionIsolation(int i) {
        handle.setTransactionIsolation(i);
    }

    @Override
    public TransactionIsolationLevel getTransactionIsolationLevel() {
        return handle.getTransactionIsolationLevel();
    }

    @Override
    public void registerArgumentFactory(ArgumentFactory argumentFactory) {
        handle.registerArgumentFactory(argumentFactory);
    }

    @Override
    public void registerContainerFactory(ContainerFactory<?> containerFactory) {
        handle.registerContainerFactory(containerFactory);
    }

    @Override
    public Connection getConnection() {
        return handle.getConnection();
    }
}
