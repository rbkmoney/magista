package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.DaoException;

public interface PayoutEventDao {

    Long getLastEventId() throws DaoException;

    PayoutEventStat findPayoutById(String payoutId) throws DaoException;

    void insert(PayoutEventStat payoutEvent) throws DaoException;

    void update(PayoutEventStat payoutEvent) throws DaoException;

}
