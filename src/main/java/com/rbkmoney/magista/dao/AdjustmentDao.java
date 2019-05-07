package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.exception.DaoException;

public interface AdjustmentDao {

    AdjustmentData get(String invoiceId, String paymentId, String adjustmentId) throws DaoException;

    void save(AdjustmentData adjustment) throws DaoException;

}
