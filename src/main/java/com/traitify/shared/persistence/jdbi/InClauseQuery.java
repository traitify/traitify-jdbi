package com.traitify.shared.persistence.jdbi;

import org.apache.commons.lang.StringUtils;
import org.skife.jdbi.v2.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InClauseQuery<T>{

    private Handle h;
    private String sql;
    private Map<String, T> bindMap;

    public InClauseQuery(Handle h, String sql){
        this.h = h;
        this.sql = sql;
        this.bindMap = new HashMap<>();
    }

    public InClauseQuery(Handle h, String sql, Map<String, T> bindMap){
        this.h = h;
        this.sql = sql;
        this.bindMap = bindMap;
    }

    public Query bindIn(String name, Collection<T> collection){
        StringBuilder sbud = new StringBuilder();

        if(collection != null){
            int i = 0;
            for(T obj : collection){
                String currentParamName = name + "_" + i;
                bindMap.put(currentParamName, obj);

                if(i > 0){
                    sbud.append(",");
                }
                sbud.append(":").append(currentParamName);
                i++;
            }
        }

       return h.createQuery(StringUtils.replace(sql, ":" + name, sbud.toString())).bindFromMap(bindMap);
    }
}
