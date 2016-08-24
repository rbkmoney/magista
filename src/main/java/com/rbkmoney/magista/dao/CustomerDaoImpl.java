package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public class CustomerDaoImpl implements CustomerDao {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Customer findByIds(String customerId, String shopId, String merchantId) throws DataAccessException {
        Customer customer;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", customerId);
            params.put("shop_id", shopId);
            params.put("merchant_id", merchantId);
            customer = namedParameterJdbcTemplate.queryForObject(
                    "SELECT id, merchant_id, shop_id, created_at from mst.customer where id = :id and shop_id = :shop_id and merchant_id = :merchant_id",
                    params,
                    getRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
        return customer;
    }

    @Override
    public void insert(Customer customer) throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", customer.getId());
        params.put("merchant_id", customer.getMerchantId());
        params.put("shop_id", customer.getShopId());
        params.put("created_at", Timestamp.from(customer.getCreatedAt()));

        String updateSql = "insert into mst.customer (id, shop_id, merchant_id, created_at) " +
                "values (:id, :shop_id, :merchant_id, :created_at)";

        try {
            log.trace("SQL: {}, Params: {}", updateSql, params);
            int rowsAffected = namedParameterJdbcTemplate.update(updateSql, params);

            if (rowsAffected != 1) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(updateSql, 1, rowsAffected);
            }

        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    public static RowMapper<Customer> getRowMapper() {
        return BeanPropertyRowMapper.newInstance(Customer.class);
    }
}
