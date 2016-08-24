package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Customer;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public interface CustomerDao {

    Customer findByIds(String customerId, String shopId, String merchantId) throws DaoException;

    void insert(Customer customer) throws DaoException;

}
