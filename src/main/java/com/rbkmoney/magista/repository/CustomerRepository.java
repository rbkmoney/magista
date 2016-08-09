package com.rbkmoney.magista.repository;

import com.rbkmoney.magista.model.Customer;

/**
 * Created by tolkonepiu on 09.08.16.
 */
public interface CustomerRepository {

    Customer findByIds(String id, String shopId, String merchantId) throws DaoException;

    void save(Customer customer) throws DaoException;

}
