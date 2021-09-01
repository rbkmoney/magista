package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.magista.okko.BadContinuationToken;
import com.rbkmoney.magista.okko.LimitExceeded;
import com.rbkmoney.magista.okko.OkkoMerchantStatisticsServiceSrv;
import com.rbkmoney.magista.okko.PaymentSearchQuery;
import com.rbkmoney.magista.okko.RefundSearchQuery;
import com.rbkmoney.magista.okko.StatEnrichedStatInvoiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OkkoMerchantStatisticsHandler implements OkkoMerchantStatisticsServiceSrv.Iface {

    private final MerchantStatisticsService merchantStatisticsService;

    @Override
    public StatEnrichedStatInvoiceResponse searchInvoicesByPaymentSearchQuery(PaymentSearchQuery paymentSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return merchantStatisticsService.getEnrichedPaymentInvoices(paymentSearchQuery);
    }

    @Override
    public StatEnrichedStatInvoiceResponse searchInvoicesByRefundSearchQuery(RefundSearchQuery refundSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return merchantStatisticsService.getEnrichedRefundInvoices(refundSearchQuery);
    }
}
