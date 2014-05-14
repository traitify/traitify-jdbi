package com.traitify.jdbi.tables;

import java.util.ArrayList;
import java.util.List;

public class Sql {

    private StringBuilder sql = new StringBuilder();

    public  Sql SELECT(Table...tables){
        List<String> columns = new ArrayList<>();

        sql.append("SELECT ");
        boolean first = true;
        for(Table table : tables){
            if(first){
                first = false;
            }else {
                sql.append(", ");
            }
            sql.append(table.getTable().fullyFormatColumns());
        }
        sql.append(" ");

        return this;
    }

    public Sql FROM(Table table){
        sql.append(table.getTable().from());
        return this;
    }

    public Sql LEFT_OUTER_JOIN(Table table1, String column1, Table table2, String column2){
        sql.append(table1.getTable().leftOuterJoin(null, table1.getTable().getTableName(), column1, table2.getTable().getTableName(), column2));
        return this;
    }

    public Sql INNER_JOIN(Table t1, String c1, Table t2, String c2){
        sql.append(t1.getTable().innerJoin(null, t1.getTable().getTableName(), c1, t2.getTable().getTableName(), c2));
        return this;
    }

    public Sql WHERE(Table t1, String c1){
        return WHERE(t1, c1, "=");
    }

    public Sql WHERE(Table t1, String c1, String operator){
        sql.append(" WHERE ").append(t1.getTable().getTableName()).append(".").append(c1).append(" ").append(operator).append(" :").append(t1.getTable().getTableName()).append(".").append(c1).append(" ");
        return this;
    }

    public Sql ORDER_BY(Table t1, String c1, String dir){
        sql.append("ORDER BY ").append(t1.getTable().getTableName()).append(".").append(c1).append(" ").append(dir);
        return this;
    }

    public Sql THEN_ORDER_BY(Table t1, String c1, String dir){
        sql.append(", ").append(t1.getTable().getTableName()).append(".").append(c1).append(" ").append(dir);
        return this;
    }

    public String build(){
        return sql.toString();
    }
}
