package com.rbkmoney.magista.repository;

import com.rbkmoney.magista.model.Customer;
import com.rbkmoney.magista.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tolkonepiu on 09.08.16.
 */
@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public CustomerRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Customer findByIds(String id, String shopId, String merchantId) throws DaoException {
        Customer customer;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("shop_id", shopId);
            params.put("merchant_id", merchantId);
            customer = namedParameterJdbcTemplate.queryForObject(
                    "SELECT id, merchant_id, shop_id, created_at from mst.consumer where id = :id and shop_id = :shop_id and merchant_id = :merchant_id",
                    params,
                    BeanPropertyRowMapper.newInstance(Customer.class)
            );
        } catch (NestedRuntimeException ex) {
            String message = String.format("Failed to find consumer by fingerprint '%s'", id);
            throw new DaoException(message, ex);
        }
        return customer;
    }

    @Override
    public void save(Customer customer) throws DaoException {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(customer);
        try {
            namedParameterJdbcTemplate.update(
                    "insert into mst.customer (id, shop_id, merchant_id, created_at) " +
                            "values (:id, :shop_id, :merchant_id, :created_at)",
                    parameterSource);
        } catch (NestedRuntimeException ex) {
            throw new DaoException("Failed to save customer", ex);
        }
    }
}
