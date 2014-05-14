package com.traitify.jdbi.tables;

import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

public class BaseTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTable.class);
    private final String tableName;
    private final String[] columns;

    private String INSERT;
    private String UPDATE_BY_ID;
    private String DELETE_BY_ID;

    public BaseTable(String tableName, String[] columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public Set<String> getBoolean_columns() {
        return new HashSet<>(Arrays.asList("active"));
    }

    public Set<String> getWildcard_columns() {
        return new HashSet<>();
    }

    public String fullyFormatColumns() {
        return formatColumns(formatTableNameAsPrefix(), true);
    }

    public String formatColumns() {
        return formatColumns(formatTableNameAsPrefix(), false);
    }

    public String formatColumns(String prefix, boolean includeAlias) {
        StringBuilder sbud = new StringBuilder();

        boolean first = true;
        for (String column : getColumns()) {

            if (first) {
                first = false;
            } else {
                sbud.append(",");
            }

            sbud.append(formatColumn(prefix, column, includeAlias));
        }

        return sbud.toString();
    }

    public List<String> getFullyFormatedColumns(){
        List<String> cols = new ArrayList<String>();

        for (String column : getColumns()) {
            cols.add(formatColumn(getTableName() + ".", column, true));
        }

        return cols;
    }

    public String formatColumn(String prefix, String column, boolean includeAlias) {
        StringBuilder sbud = new StringBuilder();

        sbud.append(prefix).append(column);

        if (includeAlias) {
            sbud.append(" AS \"").append(prefix).append(column).append("\"");
        }

        return sbud.toString();
    }

    public String formatColumn(String prefix, String column) {
        return formatColumn(prefix, column, false);
    }

    public String formatColumn(String column) {
        return formatColumn(formatTableNameAsPrefix(), column, false);
    }

    public String formatColumnsWithAlias(String prefix) {
        return formatColumns(prefix, true);
    }

    public String formatColumnsWithAlias() {
        return formatColumns(formatTableNameAsPrefix(), true);
    }

    public String formatColumnsWithAlias(String[] columns) {
        return formatColumns("", false);
    }

    public String formatColumns(String prefix, String[] columns) {
        return formatColumns(prefix, false);
    }

    public String formatColumns(String[] columns) {
        return formatColumns("", false);
    }

    public String columnEquals(String column) {
        return column + " = :" + column;
    }

    public String INSERT() {
        if (INSERT == null) {
            INSERT = INSERT(true);
        }
        return INSERT;
    }

    public String INSERT(boolean includeValuePlaceholders) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(getTableName()).append(" (").append(formatColumns(getColumns())).append(") ");

        if (includeValuePlaceholders) {
            sql.append(" VALUES ").append(INSERT_VALUES(true));
        }

        return sql.toString();
    }

    public String INSERT_VALUES(boolean namedPlaceholder) {
        StringBuilder sql = new StringBuilder(" (");

        if (namedPlaceholder) {
            sql.append(formatColumns(":", getColumns()));
        } else {
            List<String> params = new ArrayList<String>();
            for (int x = 0; x < getColumns().length; x++) {
                params.add("?");
            }

            sql.append(StringUtils.join(params, ","));
        }

        sql.append(") ");

        return sql.toString();
    }

    public String UPDATE_BY_ID() {
        if (UPDATE_BY_ID == null) {
            UPDATE_BY_ID = UPDATE(new String[]{"id"});
        }
        return UPDATE_BY_ID;
    }

    public String UPDATE(String[] predicateColumns) {
        return UPDATE(getColumns(), predicateColumns);
    }

    public String UPDATE(String[] updateColumns, String[] predicateColumns) {
        return UPDATE(updateColumns, "AND", predicateColumns);
    }

    public String UPDATE(String[] updateColumns, String operator, String[] predicateColumns) {
        StringBuilder sql = new StringBuilder("UPDATE ").append(getTableName()).append(" SET ");

        boolean first = true;
        for (String column : updateColumns) {

            if (first) {
                first = false;
            } else {
                sql.append(", ");
            }

            sql.append(column).append(" = :").append(column);
        }

        sql.append(WHERE(predicateColumns, operator));

        return sql.toString();
    }

    public String DELETE(String[] predicateColumns) {
        return DELETE("AND", predicateColumns);
    }

    public String DELETE_BY_ID() {
        if (DELETE_BY_ID == null) {
            DELETE_BY_ID = DELETE("AND", "id");
        }
        return DELETE_BY_ID;
    }

    public String DELETE(String operator, String... predicateColumns) {
        StringBuilder sbud = new StringBuilder();

        sbud.append("DELETE FROM ").append(getTableName()).append(WHERE(predicateColumns, operator));

        return sbud.toString();
    }

    public String SELECT() {
        return SELECT(getColumns());
    }

    public String SELECT(String[] selectColumns) {
        StringBuilder sbud = new StringBuilder();

        sbud.append("SELECT ");

        if (selectColumns != null && selectColumns.length > 0) {
            sbud.append(formatColumns(selectColumns));
        } else {
            sbud.append(" * ");
        }

        sbud.append(" FROM ").append(getTableName());
        sbud.append(" WHERE 1=1 ");

        return sbud.toString();
    }

    public String SELECT_BY_COLUMN(String predicateColumn) {
        return SELECT_BY_COLUMN(predicateColumn, "AND");
    }

    public String SELECT_BY_COLUMN(String predicateColumn, String operator) {
        return SELECT_BY_COLUMN(null, new String[]{predicateColumn}, operator);
    }

    public String SELECT_BY_COLUMN(String[] predicateColumn) {
        return SELECT_BY_COLUMN(predicateColumn, "AND");
    }

    public String SELECT_BY_COLUMN(String[] predicateColumn, String operator) {
        return SELECT_BY_COLUMN(null, predicateColumn, operator);
    }

    public String SELECT_BY_COLUMN(String[] selectColumns, String[] predicateColumns, String operator) {
        StringBuilder sbud = new StringBuilder();

        sbud.append("SELECT ");

        if (selectColumns != null && selectColumns.length > 0) {
            sbud.append(formatColumns(selectColumns));
        } else {
            sbud.append(" * ");
        }

        sbud.append(" FROM ").append(getTableName()).append(WHERE(predicateColumns, operator));

        return sbud.toString();
    }

    public String WHERE(String[] predicateColumns, String operator) {
        StringBuilder sbud = new StringBuilder();
        sbud.append(" WHERE ");

        boolean first = true;
        for (String predicateColumn : predicateColumns) {

            String[] columnParts = predicateColumn.split("\\.");
            String baseColumn = predicateColumn;
            if(columnParts.length > 0)
                baseColumn = columnParts[columnParts.length-1];


            if (first) {
                first = false;
            } else {
                sbud.append(" " + operator + " ");
            }

            if(getWildcard_columns().contains(baseColumn)){
                sbud.append(predicateColumn).append(" ILIKE :").append(predicateColumn);
            }else if(getBoolean_columns().contains(baseColumn)){
                sbud.append(predicateColumn).append(" = :").append(predicateColumn);
            }else{
                sbud.append(predicateColumn).append(" ILIKE :").append(predicateColumn);
            }

        }

        if (first) {
            sbud.append(" 1=1 ");
        }

        return sbud.toString();
    }

    public String getCriteriaForVars(MultivaluedMap<String, String> vars, String operator) {
        StringBuilder sbud = new StringBuilder();
        sbud.append(" WHERE ");

        String count = vars.getFirst("count");
        vars.remove("count");

        boolean first = true;

        for(String column:getBoolean_columns()){
            String param = vars.getFirst(column);
            vars.remove(column);
            if (param != null) {
                if (first) {
                    first = false;
                } else {
                    sbud.append(operator).append(" ");
                }

                sbud.append(formatTableNameAsPrefix()).append(column).append(" = ").append(Boolean.valueOf(column)).append(" ");
            }
        }

        for (String key : vars.keySet()) {
            try {
                if (first) {
                    first = false;
                } else {
                    sbud.append(operator).append(" ");
                }

                sbud.append(formatTableNameAsPrefix()).append(key);

                if (key.toLowerCase().contains("id")) {
                    sbud.append(" = ").append(SimpleDBUtils.quoteValue(vars.getFirst(key)));
                } else {
                    sbud.append(" LIKE ").append(SimpleDBUtils.quoteValue("%" + vars.getFirst(key) + "%"));
                }
            } catch (Exception e) {
                LOGGER.warn("A key was used in the admin dao list method that wasn't found -  {" + key + ": \"" + vars.getFirst(key) + "\"}");
            }

            sbud.append(" ");
        }

        if (count != null) {
            sbud.append(" LIMIT ").append(Integer.parseInt(count));
        }

        return sbud.toString();
    }

    public String ORDER_BY(String sql, String column, String direction) {
        return ORDER_BY(sql, new String[]{column}, direction);
    }

    public String leftOuterJoin(String alias, String table1, String column1, String table2, String column2) {
        return join(alias, "LEFT OUTER JOIN", table1, column1, table2, column2);
    }

    public String innerJoin(String alias, String table1, String column1, String table2, String column2) {
        return join(alias, "INNER JOIN", table1, column1, table2, column2);
    }

    public String join(String alias, String joinType, String newTable, String newTableCol, String existingTable, String existingTableCol) {
        StringBuilder sql = new StringBuilder(" ").append(joinType).append(" ").append(newTable);

        if(StringUtils.isNotBlank(alias)){
            sql.append(" AS \"").append(alias).append("\" ");
        }

        return sql.append(" ON ").append(existingTable).append(".").append(existingTableCol).append(" = ").append(newTable).append(".").append(newTableCol).append(" ").toString();
    }

    public String ORDER_BY(String sql, String[] columns, String direction) {
        StringBuilder sbud = new StringBuilder(sql);

        sbud.append(" ORDER BY ");

        boolean first = true;
        for (String column : columns) {

            if (first) {
                first = false;
            } else {
                sbud.append(", ");
            }

            sbud.append(column).append(" ").append(direction);
        }

        return sbud.toString();
    }

    public String from(){
        StringBuilder sbud = new StringBuilder(" FROM ");
        sbud.append(getTableName()).append(" ");
        return sbud.toString();
    }

    public String formatTableNameAsPrefix() {
        return getTableName() + ".";
    }

    public String[] getColumns() {
        return columns;
    }

    public String getTableName() {
        return tableName;
    }
}
