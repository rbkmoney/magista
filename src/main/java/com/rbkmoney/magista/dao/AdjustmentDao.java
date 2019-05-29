package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.exception.DaoException;

import java.util.List;

public interface AdjustmentDao {

    AdjustmentData get(String invoiceId, String paymentId, String adjustmentId) throws DaoException;

    void save(List<AdjustmentData> adjustments) throws DaoException;

}
