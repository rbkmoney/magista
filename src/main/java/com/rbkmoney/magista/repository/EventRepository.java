package com.rbkmoney.magista.repository;

/**
 * Created by tolkonepiu on 10.08.16.
 */
public interface EventRepository {

    Long getLastEventId() throws DaoException;

}
