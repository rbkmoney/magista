package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.Timestamp;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public class CustomerDaoImpl extends NamedParameterJdbcDaoSupport implements CustomerDao {

    Logger log = LoggerFactory.getLogger(this.getClass());

    public CustomerDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    @Override
    public Customer findByIds(String customerId, int shopId, String merchantId) throws DaoException {
        Customer customer;
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("id", customerId)
                .addValue("shop_id", shopId)
                .addValue("merchant_id", merchantId);

        String sql = "SELECT id, merchant_id, shop_id, created_at from mst.customer where id = :id and shop_id = :shop_id and merchant_id = :merchant_id";
        log.trace("SQL: {}, Params: {}", sql, source.getValues());
        try {
            customer = getNamedParameterJdbcTemplate().queryForObject(
                    sql,
                    source,
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
    public void insert(Customer customer) throws DaoException {
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("id", customer.getId())
                .addValue("merchant_id", customer.getMerchantId())
                .addValue("shop_id", customer.getShopId())
                .addValue("created_at", Timestamp.from(customer.getCreatedAt()));

        String updateSql = "insert into mst.customer (id, shop_id, merchant_id, created_at) " +
                "values (:id, :shop_id, :merchant_id, :created_at)";

        try {
            log.trace("SQL: {}, Params: {}", updateSql, source.getValues());
            getNamedParameterJdbcTemplate().update(updateSql, source);

        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    public static RowMapper<Customer> getRowMapper() {
        return BeanPropertyRowMapper.newInstance(Customer.class);
    }
}
