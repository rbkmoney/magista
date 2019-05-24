package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.impl.InvoicesFunction;
import com.rbkmoney.magista.query.impl.PaymentsFunction;
import com.rbkmoney.magista.query.impl.PayoutsFunction;
import com.rbkmoney.magista.query.impl.RefundsFunction;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface SearchDao {

    Collection<Map.Entry<Long, StatInvoice>> getInvoices(
            InvoicesFunction.InvoicesParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            int limit
    ) throws DaoException;

    Collection<Map.Entry<Long, StatPayment>> getPayments(
            PaymentsFunction.PaymentsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            int limit
    ) throws DaoException;

    Collection<Map.Entry<Long, StatRefund>> getRefunds(
            RefundsFunction.RefundsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            Optional<Integer> offset,
            int limit
    ) throws DaoException;

    Collection<Map.Entry<Long, StatPayout>> getPayouts(
            PayoutsFunction.PayoutsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            Optional<Integer> offset,
            int limit
    ) throws DaoException;


    Collection<Map.Entry<Long, EnrichedStatInvoice>> getEnrichedInvoices(
            RefundsFunction.RefundsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> fromId,
            int limit
    ) throws DaoException;

    Collection<Map.Entry<Long, EnrichedStatInvoice>> getEnrichedInvoices(
            PaymentsFunction.PaymentsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            int limit
    ) throws DaoException;

}
