package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;

public interface ChargebackDao {

    ChargebackData get(String invoiceId, String paymentId, String chargebackId);

    void save(ChargebackData chargebackData);

}
