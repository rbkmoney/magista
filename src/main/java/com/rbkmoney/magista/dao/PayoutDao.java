package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.Payout;

public interface PayoutDao {

    Payout get(String payoutId);

    void save(Payout payout);

}
