package com.rbkmoney.magista.util;

import com.rbkmoney.magista.exception.DaoException;
import org.jooq.EnumType;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.conf.ParamType;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Map;

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
