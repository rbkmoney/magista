package com.rbkmoney.magista.service;

import com.rbkmoney.magista.*;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.okko.EnrichedStatInvoice;
import com.rbkmoney.magista.okko.StatEnrichedStatInvoiceResponse;
import com.rbkmoney.magista.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantStatisticsService {

    private final SearchDao searchDao;
    private final TokenGenService tokenGenService;

    public StatInvoiceResponse getInvoices(InvoiceSearchQuery searchQuery) {
        InvoiceSearchQuery queryCopyWithNullToken = new InvoiceSearchQuery(searchQuery);
        queryCopyWithNullToken.getCommonSearchQueryParams().setContinuationToken(null);
        tokenGenService.validateToken(queryCopyWithNullToken,
                searchQuery.getCommonSearchQueryParams().getContinuationToken());
        List<StatInvoice> invoices = searchDao.getInvoices(searchQuery);
        return new StatInvoiceResponse()
                .setInvoices(invoices)
                .setContinuationToken(
                        tokenGenService.generateToken(
                                queryCopyWithNullToken,
                                searchQuery.getCommonSearchQueryParams(),
                                invoices,
                                TokenUtil::getLastElement,
                                StatInvoice::getCreatedAt));
    }

    public StatPaymentResponse getPayments(PaymentSearchQuery searchQuery) {
        PaymentSearchQuery queryCopyWithNullToken = new PaymentSearchQuery(searchQuery);
        queryCopyWithNullToken.getCommonSearchQueryParams().setContinuationToken(null);
        tokenGenService.validateToken(queryCopyWithNullToken,
                searchQuery.getCommonSearchQueryParams().getContinuationToken());
        List<StatPayment> payments = searchDao.getPayments(searchQuery);
        return new StatPaymentResponse()
                .setPayments(payments)
                .setContinuationToken(
                        tokenGenService.generateToken(
                                queryCopyWithNullToken,
                                searchQuery.getCommonSearchQueryParams(),
                                payments,
                                TokenUtil::getLastElement,
                                StatPayment::getCreatedAt));
    }

    public StatRefundResponse getRefunds(RefundSearchQuery searchQuery) {
        RefundSearchQuery queryCopyWithNullToken = new RefundSearchQuery(searchQuery);
        queryCopyWithNullToken.getCommonSearchQueryParams().setContinuationToken(null);
        tokenGenService.validateToken(queryCopyWithNullToken,
                searchQuery.getCommonSearchQueryParams().getContinuationToken());
        List<StatRefund> refunds = searchDao.getRefunds(searchQuery);
        return new StatRefundResponse()
                .setRefunds(refunds)
                .setContinuationToken(
                        tokenGenService.generateToken(
                                queryCopyWithNullToken,
                                searchQuery.getCommonSearchQueryParams(),
                                refunds,
                                TokenUtil::getLastElement,
                                StatRefund::getCreatedAt));
    }

    public StatPayoutResponse getPayouts(PayoutSearchQuery searchQuery) {
        PayoutSearchQuery queryCopyWithNullToken = new PayoutSearchQuery(searchQuery);
        queryCopyWithNullToken.getCommonSearchQueryParams().setContinuationToken(null);
        tokenGenService.validateToken(queryCopyWithNullToken,
                searchQuery.getCommonSearchQueryParams().getContinuationToken());
        List<StatPayout> payouts = searchDao.getPayouts(searchQuery);
        return new StatPayoutResponse()
                .setPayouts(payouts)
                .setContinuationToken(
                        tokenGenService.generateToken(
                                queryCopyWithNullToken,
                                searchQuery.getCommonSearchQueryParams(),
                                payouts,
                                TokenUtil::getLastElement,
                                StatPayout::getCreatedAt));
    }

    public StatChargebackResponse getChargebacks(ChargebackSearchQuery searchQuery) {
        ChargebackSearchQuery queryCopyWithNullToken = new ChargebackSearchQuery(searchQuery);
        queryCopyWithNullToken.getCommonSearchQueryParams().setContinuationToken(null);
        tokenGenService.validateToken(queryCopyWithNullToken,
                searchQuery.getCommonSearchQueryParams().getContinuationToken());
        List<StatChargeback> chargebacks = searchDao.getChargebacks(searchQuery);
        return new StatChargebackResponse()
                .setChargebacks(chargebacks)
                .setContinuationToken(
                        tokenGenService.generateToken(
                                queryCopyWithNullToken,
                                searchQuery.getCommonSearchQueryParams(),
                                chargebacks,
                                TokenUtil::getLastElement,
                                StatChargeback::getCreatedAt)
                );
    }

    public StatEnrichedStatInvoiceResponse getEnrichedPaymentInvoices(
            com.rbkmoney.magista.okko.PaymentSearchQuery searchQuery) {
        var queryCopyWithNullToken = new com.rbkmoney.magista.okko.PaymentSearchQuery(searchQuery);
        queryCopyWithNullToken.getCommonSearchQueryParams().setContinuationToken(null);
        tokenGenService.validateToken(queryCopyWithNullToken,
                searchQuery.getCommonSearchQueryParams().getContinuationToken());
        List<EnrichedStatInvoice> invoices = searchDao.getEnrichedInvoices(searchQuery);
        return new StatEnrichedStatInvoiceResponse()
                .setEnrichedInvoices(invoices)
                .setContinuationToken(
                        tokenGenService.generateToken(
                                queryCopyWithNullToken,
                                searchQuery.getCommonSearchQueryParams(),
                                invoices,
                                TokenUtil::getEnrichedPaymentsDateTime));
    }

    public StatEnrichedStatInvoiceResponse getEnrichedRefundInvoices(
            com.rbkmoney.magista.okko.RefundSearchQuery searchQuery) {
        var queryCopyWithNullToken = new com.rbkmoney.magista.okko.RefundSearchQuery(searchQuery);
        queryCopyWithNullToken.getCommonSearchQueryParams().setContinuationToken(null);
        tokenGenService.validateToken(queryCopyWithNullToken,
                searchQuery.getCommonSearchQueryParams().getContinuationToken());
        List<EnrichedStatInvoice> invoices = searchDao.getEnrichedInvoices(searchQuery);
        return new StatEnrichedStatInvoiceResponse()
                .setEnrichedInvoices(invoices)
                .setContinuationToken(
                        tokenGenService.generateToken(
                                queryCopyWithNullToken,
                                searchQuery.getCommonSearchQueryParams(),
                                invoices,
                                TokenUtil::getEnrichedRefundsDateTime)
                );
    }
}
