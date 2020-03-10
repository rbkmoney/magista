package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.PayoutData;

public interface PayoutDao {

    PayoutData get(String payoutId);

    void save(PayoutData payoutData);

}
