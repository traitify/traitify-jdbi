package com.traitify.shared.persistence.mapper.util;

import org.apache.commons.lang.StringUtils;

public class TableUtil {


    public static String getFullColumnName(String tableAlias, String baseColumnName){
        if(StringUtils.isBlank(tableAlias)){
            return  baseColumnName;
        }

        return tableAlias + "." + baseColumnName;
    }
}
