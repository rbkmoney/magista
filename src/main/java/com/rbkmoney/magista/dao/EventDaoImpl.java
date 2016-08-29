package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;

/**
 * Created by tolkonepiu on 24.08.16.
 */
public class EventDaoImpl extends NamedParameterJdbcDaoSupport implements EventDao {

    public EventDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        try {
            return getJdbcTemplate().queryForObject("select max(event_id) from (select event_id from mst.invoice union all select event_id from mst.payment) as event_ids", Long.class);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }
}
