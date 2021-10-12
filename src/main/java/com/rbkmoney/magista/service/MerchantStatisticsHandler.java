package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.magista.*;
import com.rbkmoney.magista.constant.SearchConstant;
import com.rbkmoney.magista.exception.BadTokenException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantStatisticsHandler implements MerchantStatisticsServiceSrv.Iface {

    private final MerchantStatisticsService merchantStatisticsService;

    @Override
    public StatInvoiceResponse searchInvoices(InvoiceSearchQuery invoiceSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return handleSearchQuery(
                () -> merchantStatisticsService.getInvoices(invoiceSearchQuery),
                invoiceSearchQuery.getCommonSearchQueryParams());
    }

    @Override
    public StatPaymentResponse searchPayments(PaymentSearchQuery paymentSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return handleSearchQuery(
                () -> merchantStatisticsService.getPayments(paymentSearchQuery),
                paymentSearchQuery.getCommonSearchQueryParams());
    }

    @Override
    public StatRefundResponse searchRefunds(RefundSearchQuery refundSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return handleSearchQuery(
                () -> merchantStatisticsService.getRefunds(refundSearchQuery),
                refundSearchQuery.getCommonSearchQueryParams());
    }

    @Override
    public StatChargebackResponse searchChargebacks(ChargebackSearchQuery chargebackSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return handleSearchQuery(
                () -> merchantStatisticsService.getChargebacks(chargebackSearchQuery),
                chargebackSearchQuery.getCommonSearchQueryParams());
    }

    @Override
    public StatPayoutResponse searchPayouts(PayoutSearchQuery payoutSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return handleSearchQuery(
                () -> merchantStatisticsService.getPayouts(payoutSearchQuery),
                payoutSearchQuery.getCommonSearchQueryParams());
    }

    @Override
    public StatInvoiceTemplateResponse searchInvoiceTemplates(InvoiceTemplateSearchQuery invoiceTemplateSearchQuery)
            throws BadContinuationToken, LimitExceeded, InvalidRequest, TException {
        return handleSearchQuery(
                () -> merchantStatisticsService.getInvoiceTemplates(invoiceTemplateSearchQuery),
                invoiceTemplateSearchQuery.getCommonSearchQueryParams());
    }

    @SneakyThrows
    private <T> T handleSearchQuery(
            Supplier<T> merchantStatisticsService,
            CommonSearchQueryParams commonSearchQueryParams) {
        if (commonSearchQueryParams.getLimit() > SearchConstant.LIMIT) {
            throw new LimitExceeded();
        }
        try {
            return merchantStatisticsService.get();
        } catch (BadTokenException ex) {
            throw new BadContinuationToken(ex.getMessage());
        } catch (Exception e) {
            log.error("Failed to process search request", e);
            throw new InvalidRequest(List.of(e.getMessage()));
        }
    }
}
