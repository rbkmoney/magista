package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;

import java.util.Optional;

public interface EventDao {

    Optional<Long> getLastInvoiceEventId() throws DaoException;

    Optional<Long> getLastPayoutEventId() throws DaoException;

}
