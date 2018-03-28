package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
import com.rbkmoney.magista.exception.DaoException;

public interface AdjustmentDao {

    Adjustment get(String invoiceId, String paymentId, String adjustmentId) throws DaoException;

    void save(Adjustment adjustment) throws DaoException;

}
