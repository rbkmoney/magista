package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;

/**
 * Created by tolkonepiu on 24.08.16.
 */
public interface EventDao {

    Long getLastEventId() throws DaoException;

}
