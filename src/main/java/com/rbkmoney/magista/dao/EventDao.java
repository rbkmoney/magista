package com.rbkmoney.magista.dao;

import java.util.Optional;

public interface EventDao {

    Optional<Long> getLastPayoutEventId();

}
