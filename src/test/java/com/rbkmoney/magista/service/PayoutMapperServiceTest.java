package com.rbkmoney.magista.service;

import com.rbkmoney.magista.event.mapper.impl.PayoutCreatedMapper;
import com.rbkmoney.magista.event.mapper.impl.PayoutStatusChangedMapper;
import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.PayoutChange;
import com.rbkmoney.payout.manager.PayoutCreated;
import com.rbkmoney.payout.manager.PayoutStatusChanged;
import com.rbkmoney.testcontainers.annotations.DefaultSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DefaultSpringBootTest
public class PayoutMapperServiceTest {

    @MockBean
    private PayoutCreatedMapper payoutCreatedMapper;

    @MockBean
    private PayoutStatusChangedMapper payoutStatusChangedMapper;

    @Autowired
    private PayoutMapperService payoutMapperService;

    @BeforeEach
    public void setup() {
        given(payoutCreatedMapper.accept(any())).willCallRealMethod();
        given(payoutStatusChangedMapper.accept(any())).willCallRealMethod();
    }

    @Test
    public void test() {
        payoutMapperService.handleEvents(List.of(
                new Event().setPayoutChange(PayoutChange.created(new PayoutCreated())),
                new Event().setPayoutChange(PayoutChange.status_changed(new PayoutStatusChanged()))));
        verify(payoutCreatedMapper, times(2)).accept(any());
        verify(payoutCreatedMapper, times(1)).map(any(), any());
        verify(payoutStatusChangedMapper, times(2)).accept(any());
        verify(payoutStatusChangedMapper, times(1)).map(any(), any());
    }

}
