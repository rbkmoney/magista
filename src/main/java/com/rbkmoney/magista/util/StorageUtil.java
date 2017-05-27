package com.rbkmoney.magista.util;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Created by tolkonepiu on 26/05/2017.
 */
public class StorageUtil {

    public static MapSqlParameterSource validateParams(MapSqlParameterSource sqlParameterSource) {
        sqlParameterSource.getValues().entrySet().stream()
                .filter(t -> t.getValue() instanceof String)
                .forEach(t -> sqlParameterSource.addValue(t.getKey(), t.getValue().toString().replace("\u0000", "\\u0000")));
        return sqlParameterSource;
    }

}
