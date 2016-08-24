package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.model.Customer;
import org.springframework.dao.DataAccessException;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public interface CustomerDao {

    Customer findByIds(String customerId, String shopId, String merchantId) throws DataAccessException;

    void insert(Customer customer) throws DataAccessException;

}
