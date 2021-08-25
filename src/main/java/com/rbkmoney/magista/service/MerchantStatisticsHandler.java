package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.magista.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

@Slf4j
public class MerchantStatisticsHandler implements MerchantStatisticsServiceSrv.Iface{

    @Override
    public StatInvoiceResponse searchInvoices(InvoiceSearchQuery invoiceSearchQuery) throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return null;
    }

    @Override
    public StatPaymentResponse searchPayments(PaymentSearchQuery paymentSearchQuery) throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return null;
    }

    @Override
    public StatRefundResponse searchRefunds(RefundSearchQuery refundSearchQuery) throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return null;
    }

    @Override
    public StatChargebackResponse searchChargebacks(ChargebackSearchQuery chargebackSearchQuery) throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return null;
    }

    @Override
    public StatPayoutResponse searchPayouts(PayoutSearchQuery payoutSearchQuery) throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return null;
    }

    @Override
    public StatInvoiceTemplateResponse searchInvoiceTemplates(InvoiceTemplateSearchQuery invoiceTemplateSearchQuery) throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return null;
    }
}
