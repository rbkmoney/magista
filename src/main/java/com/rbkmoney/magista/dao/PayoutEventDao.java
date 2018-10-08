package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.DaoException;

public interface PayoutEventDao {

    PayoutEventStat findPayoutById(String payoutId) throws DaoException;

    void insert(PayoutEventStat payoutEvent) throws DaoException;

}
