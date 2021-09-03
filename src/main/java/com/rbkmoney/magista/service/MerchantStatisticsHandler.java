package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.magista.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantStatisticsHandler implements MerchantStatisticsServiceSrv.Iface {

    private final MerchantStatisticsService merchantStatisticsService;

    @Override
    public StatInvoiceResponse searchInvoices(InvoiceSearchQuery invoiceSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return merchantStatisticsService.getInvoices(invoiceSearchQuery);
    }

    @Override
    public StatPaymentResponse searchPayments(PaymentSearchQuery paymentSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return merchantStatisticsService.getPayments(paymentSearchQuery);
    }

    @Override
    public StatRefundResponse searchRefunds(RefundSearchQuery refundSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return merchantStatisticsService.getRefunds(refundSearchQuery);
    }

    @Override
    public StatChargebackResponse searchChargebacks(ChargebackSearchQuery chargebackSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return merchantStatisticsService.getChargebacks(chargebackSearchQuery);
    }

    @Override
    public StatPayoutResponse searchPayouts(PayoutSearchQuery payoutSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return merchantStatisticsService.getPayouts(payoutSearchQuery);
    }

    @Override
    public StatInvoiceTemplateResponse searchInvoiceTemplates(InvoiceTemplateSearchQuery invoiceTemplateSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return null;
    }
}
