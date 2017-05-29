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

    public static <T> T fetchOne(Query query, Class<T> type, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetchOne(query, new SingleColumnRowMapper<>(type), namedParameterJdbcTemplate);
    }

    public static <T> T fetchOne(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    query.getSQL(ParamType.NAMED),
                    toSqlParameterSource(query.getParams()),
                    rowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    public static void execute(Query query, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(
                    query.getSQL(ParamType.NAMED),
                    toSqlParameterSource(query.getParams()));

            if (rowsAffected != 1) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(query.toString(), 1, rowsAffected);
            }
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    public static SqlParameterSource toSqlParameterSource(Map<String, Param<?>> params) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        for (Map.Entry<String, Param<?>> entry : params.entrySet()) {
            Param<?> param = entry.getValue();
            if (param.getValue() instanceof LocalDateTime || param.getValue() instanceof EnumType) {
                sqlParameterSource.addValue(entry.getKey(), param.getValue(), Types.OTHER);
            } else {
                sqlParameterSource.addValue(entry.getKey(), param.getValue());
            }
        }
        return sqlParameterSource;
    }
}
