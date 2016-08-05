package com.rbkmoney.magista.repository;

import com.rbkmoney.magista.model.Payment;
import org.jooq.exception.DataAccessException;

/**
 * Created by tolkonepiu on 04.08.16.
 */
public interface PaymentRepository {

    void save(Payment payment) throws DaoException;

}
