package com.rbkmoney.magista.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by tolkonepiu on 10.08.16.
 */
@Repository
public class EnvetRepositoryImpl implements EventRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public EnvetRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @DependsOn("dbInitializer")
    public Long getLastEventId() throws DaoException {
        return jdbcTemplate.queryForObject("select max(event_id) from (select event_id from mst.invoice union all select event_id from mst.payment) as event_ids", Long.class);
    }
}
