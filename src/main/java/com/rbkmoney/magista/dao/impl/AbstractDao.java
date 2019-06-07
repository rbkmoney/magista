package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.impl.field.CollectionConditionField;
import com.rbkmoney.magista.dao.impl.field.ConditionField;
import com.rbkmoney.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractDao extends NamedParameterJdbcDaoSupport {

    private final DSLContext dslContext;

    public AbstractDao(DataSource dataSource) {
        setDataSource(dataSource);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES_9_5);
        this.dslContext = DSL.using(configuration);
    }

    protected DSLContext getDslContext() {
        return dslContext;
    }

    public <T> T fetchOne(Query query, Class<T> type) throws DaoException {
        return fetchOne(query, type, getNamedParameterJdbcTemplate());
    }

    public <T> T fetchOne(Query query, Class<T> type, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetchOne(query, new SingleColumnRowMapper<>(type), namedParameterJdbcTemplate);
    }

    public <T> T fetchOne(Query query, RowMapper<T> rowMapper) throws DaoException {
        return fetchOne(query, rowMapper, getNamedParameterJdbcTemplate());
    }

    public <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException {
        return fetchOne(namedSql, parameterSource, rowMapper, getNamedParameterJdbcTemplate());
    }

    public <T> T fetchOne(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetchOne(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), rowMapper, namedParameterJdbcTemplate);
    }

    public <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    namedSql,
                    parameterSource,
                    rowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    public <T> List<T> fetch(Query query, RowMapper<T> rowMapper) throws DaoException {
        return fetch(query, rowMapper, getNamedParameterJdbcTemplate());
    }

    public <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException {
        return fetch(namedSql, parameterSource, rowMapper, getNamedParameterJdbcTemplate());
    }

    public <T> List<T> fetch(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetch(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), rowMapper, namedParameterJdbcTemplate);
    }

    public <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            return namedParameterJdbcTemplate.query(
                    namedSql,
                    parameterSource,
                    rowMapper
            );
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    public void executeOne(Query query) throws DaoException {
        execute(query, 1);
    }

    public void execute(Query query) throws DaoException {
        execute(query, -1);
    }

    public void execute(Query query, int expectedRowsAffected) throws DaoException {
        execute(query, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    public void execute(Query query, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), expectedRowsAffected, namedParameterJdbcTemplate);
    }

    public void executeOne(String namedSql, SqlParameterSource parameterSource) throws DaoException {
        execute(namedSql, parameterSource, 1);
    }

    public void execute(String namedSql, SqlParameterSource parameterSource) throws DaoException {
        execute(namedSql, parameterSource, -1);
    }

    public void execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected) throws DaoException {
        execute(namedSql, parameterSource, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    public void execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(
                    namedSql,
                    parameterSource);

            if (expectedRowsAffected != -1 && rowsAffected != expectedRowsAffected) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, expectedRowsAffected, rowsAffected);
            }
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    public void batchExecute(List<Query> queries) throws DaoException {
        batchExecute(queries, -1);
    }

    public void batchExecute(List<Query> queries, int expectedRowsPerQueryAffected) throws DaoException {
        batchExecute(queries, expectedRowsPerQueryAffected, getNamedParameterJdbcTemplate());
    }

    public void batchExecute(List<Query> queries, int expectedRowsPerQueryAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        queries.stream()
                .collect(
                        Collectors.groupingBy(
                                query -> query.getSQL(ParamType.NAMED),
                                LinkedHashMap::new,
                                Collectors.mapping(query -> toSqlParameterSource(query.getParams()), Collectors.toList())
                        )
                )
                .forEach(
                        (namedSql, parameterSources) -> batchExecute(
                                namedSql,
                                parameterSources,
                                expectedRowsPerQueryAffected,
                                namedParameterJdbcTemplate
                        )
                );
    }

    public void batchExecute(String namedSql, List<SqlParameterSource> parameterSources) throws DaoException {
        batchExecute(namedSql, parameterSources, -1);
    }

    public void batchExecute(String namedSql, List<SqlParameterSource> parameterSources, int expectedRowsPerQueryAffected) throws DaoException {
        batchExecute(namedSql, parameterSources, expectedRowsPerQueryAffected, getNamedParameterJdbcTemplate());
    }

    public void batchExecute(String namedSql, List<SqlParameterSource> parameterSources, int expectedRowsPerQueryAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            int[] rowsPerBatchAffected = namedParameterJdbcTemplate.batchUpdate(namedSql, parameterSources.toArray(new SqlParameterSource[0]));

            if (rowsPerBatchAffected.length != parameterSources.size()) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, parameterSources.size(), rowsPerBatchAffected.length);
            }

            for (int rowsAffected : rowsPerBatchAffected) {
                if (expectedRowsPerQueryAffected != -1 && rowsAffected != expectedRowsPerQueryAffected) {
                    throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, expectedRowsPerQueryAffected, rowsAffected);
                }
            }
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    protected Condition appendConditions(Condition condition, Operator operator, ConditionParameterSource conditionParameterSource) {
        for (ConditionField field : conditionParameterSource.getConditionFields()) {
            if (field.getValue() != null) {
                condition = DSL.condition(operator, condition, buildCondition(field));
            }
        }
        return condition;
    }

    private Condition buildCondition(ConditionField field) {
        if (field.getComparator() == Comparator.IN) {
            if (field instanceof CollectionConditionField) {
                return field.getField().in(((CollectionConditionField) field).getValue());
            }
        }
        return field.getField().compare(
                field.getComparator(),
                field.getValue()
        );
    }

    protected Condition appendDateTimeRangeConditions(Condition condition,
                                                      Field<LocalDateTime> field,
                                                      Optional<LocalDateTime> fromTime,
                                                      Optional<LocalDateTime> toTime) {
        if (fromTime.isPresent()) {
            condition = condition.and(field.ge(fromTime.get()));
        }

        if (toTime.isPresent()) {
            condition = condition.and(field.lt(toTime.get()));
        }
        return condition;
    }

    protected SqlParameterSource toSqlParameterSource(Map<String, Param<?>> params) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        for (Map.Entry<String, Param<?>> entry : params.entrySet()) {
            Param<?> param = entry.getValue();
            Class<?> type = param.getDataType().getType();
            if (String.class.isAssignableFrom(type)) {
                String value = Optional.ofNullable(param.getValue())
                        .map(stringValue -> ((String) stringValue).replace("\u0000", "\\u0000"))
                        .orElse(null);
                sqlParameterSource.addValue(entry.getKey(), value);
            } else if (EnumType.class.isAssignableFrom(type)) {
                sqlParameterSource.addValue(entry.getKey(), param.getValue(), Types.OTHER);
            } else {
                sqlParameterSource.addValue(entry.getKey(), param.getValue());
            }
        }
        return sqlParameterSource;
    }

}
