package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.PayoutData;
import com.rbkmoney.magista.exception.DaoException;

public interface PayoutDao {

    PayoutData get(String payoutId) throws DaoException;

    void save(PayoutData payoutData) throws DaoException;

}
