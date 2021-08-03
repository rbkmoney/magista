package com.rbkmoney.magista.service;

import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.event.mapper.impl.PayoutCreatedHandler;
import com.rbkmoney.magista.event.mapper.impl.PayoutStatusChangedHandler;
import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.PayoutChange;
import com.rbkmoney.payout.manager.PayoutCreated;
import com.rbkmoney.payout.manager.PayoutStatusChanged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@PostgresqlSpringBootITest
public class PayoutHandlerServiceTest {

    @MockBean
    private PayoutCreatedHandler payoutCreatedMapper;

    @MockBean
    private PayoutStatusChangedHandler payoutStatusChangedMapper;

    @Autowired
    private PayoutHandlerService payoutHandlerService;

    @BeforeEach
    public void setup() {
        given(payoutCreatedMapper.accept(any())).willCallRealMethod();
        given(payoutStatusChangedMapper.accept(any())).willCallRealMethod();
    }

    @Test
    public void test() {
        payoutHandlerService.handleEvents(List.of(
                new Event().setPayoutChange(PayoutChange.created(new PayoutCreated())),
                new Event().setPayoutChange(PayoutChange.status_changed(new PayoutStatusChanged()))));
        verify(payoutCreatedMapper, times(2)).accept(any());
        verify(payoutCreatedMapper, times(1)).handle(any(), any());
        verify(payoutStatusChangedMapper, times(2)).accept(any());
        verify(payoutStatusChangedMapper, times(1)).handle(any(), any());
    }

}
