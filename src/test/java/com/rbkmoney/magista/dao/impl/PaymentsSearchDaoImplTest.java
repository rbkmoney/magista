package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.PaymentTerminal;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.*;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class PaymentsSearchDaoImplTest {

    @Autowired
    private SearchDaoImpl searchDao;

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testPayments() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getCommonSearchQueryParams().setShopIds(List.of("SHOP_ID"));
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(3, payments.size());
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testPaymentAdditionalInfoWithInvoiceSearch() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams()
                .setPaymentRrn("43253")
                .setPaymentApprovalCode("5324");
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());
    }


    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testPaymentExcludeField() {
        PaymentSearchQuery searchQuery = new PaymentSearchQuery()
                .setCommonSearchQueryParams(new CommonSearchQueryParams()
                        .setPartyId("b9c3bf7f-f62a-4675-8489-2da7775024bb")
                        .setFromTime("2016-10-25T15:45:20Z")
                        .setToTime("3018-10-25T18:10:10Z"))
                .setPaymentParams(new PaymentParams())
                .setExcludedShopIds(List.of("6789"));
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_amount_from_to.sql")
    public void testPaymentFromTo() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams()
                .setPaymentAmountFrom(10000)
                .setPaymentAmountTo(30000);
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(3, payments.size());
    }

    @Test
    @Sql("classpath:data/sql/search/payment_with_provider_and_terminal_id.sql")
    public void testPaymentSearchByTerminalId() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams()
                .setPaymentTerminalId("120");
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());

        searchQuery.getPaymentParams()
                .setPaymentTerminalId("111");
        payments = searchDao.getPayments(searchQuery);
        Assertions.assertTrue(payments.isEmpty());
    }

    @Test
    @Sql("classpath:data/sql/search/payment_with_provider_and_terminal_id.sql")
    public void testPaymentSearchByProviderId() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams()
                .setPaymentProviderId("115");
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());

        searchQuery.getPaymentParams()
                .setPaymentProviderId("111");
        payments = searchDao.getPayments(searchQuery);
        Assertions.assertTrue(payments.isEmpty());
    }

    @Test
    @Sql("classpath:data/sql/search/payment_with_provider_and_terminal_id.sql")
    public void testPaymentSearchByProviderAndTerminalId() {

        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams()
                .setPaymentTerminalId("120")
                .setPaymentProviderId("115");
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());

        searchQuery.getPaymentParams()
                .setPaymentTerminalId("120")
                .setPaymentProviderId("111");
        payments = searchDao.getPayments(searchQuery);
        Assertions.assertTrue(payments.isEmpty());

        searchQuery.getPaymentParams()
                .setPaymentTerminalId("111")
                .setPaymentProviderId("115");
        payments = searchDao.getPayments(searchQuery);
        Assertions.assertTrue(payments.isEmpty());
    }


    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testOrderByEventIdPayments() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        var payments = searchDao.getPayments(searchQuery);
        Instant instant = Instant.MAX;
        for (com.rbkmoney.magista.StatPayment statPayment : payments) {
            Instant statInstant = Instant.from(TypeUtil.stringToTemporal(statPayment.getCreatedAt()));
            Assertions.assertTrue(statInstant.isBefore(instant));
            instant = statInstant;
        }
    }

    @Test
    @Sql("classpath:data/sql/search/payment_operation_timeout_search_data.sql")
    public void testOperationTimeout() {

        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams()
                .setPaymentStatus(com.rbkmoney.magista.InvoicePaymentStatus.failed);
        searchQuery.getCommonSearchQueryParams().setLimit(1);
        var payments = searchDao.getPayments(searchQuery);

        Assertions.assertTrue(payments.stream().allMatch(
                payment -> payment.getStatus().isSetFailed()
                        && payment.getStatus().getFailed().getFailure().isSetOperationTimeout()
        ));
    }

    @Test
    @Sql("classpath:data/sql/search/payment_with_holds_search_data.sql")
    public void testHoldAndInstantFlow() {

        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getCommonSearchQueryParams().setShopIds(List.of("SHOP_ID"));
        searchQuery.getPaymentParams()
                .setPaymentId("PAYMENT_ID_1")
                .setPaymentFlow(InvoicePaymentFlowType.hold);

        var payments = searchDao.getPayments(searchQuery);
        Assertions.assertTrue(payments.stream()
                .allMatch(payment -> payment.getFlow().isSetHold()
                        && payment.getFlow().getHold().getOnHoldExpiration()
                        .equals(com.rbkmoney.magista.OnHoldExpiration.capture)
                )
        );

        searchQuery.getPaymentParams()
                .setPaymentId("PAYMENT_ID_2")
                .setPaymentFlow(InvoicePaymentFlowType.hold);

        payments = searchDao.getPayments(searchQuery);
        Assertions.assertTrue(payments.stream()
                .allMatch(payment -> payment.getFlow().isSetHold()
                        && payment.getFlow().getHold().getOnHoldExpiration()
                        .equals(com.rbkmoney.magista.OnHoldExpiration.cancel)
                )
        );

        searchQuery.getPaymentParams()
                .setPaymentId("PAYMENT_ID_3")
                .setPaymentFlow(InvoicePaymentFlowType.instant);

        payments = searchDao.getPayments(searchQuery);
        Assertions.assertTrue(payments.stream()
                .allMatch(
                        payment -> payment.getFlow().isSetInstant()
                )
        );
    }


    @Test
    @Sql("classpath:data/sql/search/payment_terminal_provider_search_data.sql")
    public void testFindByPaymentMethodAndTerminalProvider() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams()
                .setPaymentTool(PaymentToolType.payment_terminal)
                .setPaymentTerminalProvider(LegacyTerminalPaymentProvider.euroset);
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());
        assertEquals(LegacyTerminalPaymentProvider.euroset,
                payments.get(0).getPayer().getPaymentResource().getResource().getPaymentTool()
                        .getPaymentTerminal().getTerminalTypeDeprecated());
    }

    @Test
    @Sql("classpath:data/sql/search/payment_customer_id_search_data.sql")
    public void testFindByCustomerId() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams().setPaymentCustomerId("test");
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());
        assertEquals("test", payments.get(0).getPayer().getCustomer().getCustomerId());
    }

    @Test
    @Sql("classpath:data/sql/search/payment_external_failure_search_data.sql")
    public void testExternalFailure() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams().setPaymentStatus(com.rbkmoney.magista.InvoicePaymentStatus.failed);
        searchQuery.getCommonSearchQueryParams().setLimit(1);
        var payments = searchDao.getPayments(searchQuery);
        Assertions.assertTrue(payments.stream().allMatch(
                payment -> payment.getStatus().isSetFailed()
                        && payment.getStatus().getFailed().getFailure().isSetFailure()
        ));
    }

    @Test
    @Sql("classpath:data/sql/search/payment_with_domain_revision_search_data.sql")
    public void testSearchByPaymentDomainRevision() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams().setPaymentDomainRevision(2);
        searchQuery.getCommonSearchQueryParams().setLimit(1);
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());

        searchQuery.getPaymentParams().setPaymentDomainRevision(1);
        payments = searchDao.getPayments(searchQuery);
        Assertions.assertTrue(payments.isEmpty());

        searchQuery.getPaymentParams().unsetPaymentDomainRevision();
        searchQuery.getPaymentParams().setFromPaymentDomainRevision(1);
        payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());

        searchQuery.getPaymentParams().unsetFromPaymentDomainRevision();
        searchQuery.getPaymentParams().setToPaymentDomainRevision(1);
        payments = searchDao.getPayments(searchQuery);
        assertEquals(0, payments.size());

        searchQuery.getPaymentParams().setFromPaymentDomainRevision(1);
        searchQuery.getPaymentParams().setToPaymentDomainRevision(2);
        payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());
    }

    @Test
    @Sql("classpath:data/sql/search/recurrent_payments_search_data.sql")
    public void testRecurrentPayments() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.setInvoiceIds(List.of("INVOICE_ID_1"));
        searchQuery.getPaymentParams().setPaymentId("PAYMENT_ID_1");
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());

        var statPayment = payments.get(0);
        Assertions.assertTrue(statPayment.isMakeRecurrent());
        Assertions.assertTrue(statPayment.isSetPayer());
        Assertions.assertTrue(statPayment.getPayer().isSetRecurrent());
        var recurrentPayer = statPayment.getPayer().getRecurrent();
        assertEquals("PARENT_INVOICE_ID_1", recurrentPayer.getRecurrentParent().getInvoiceId());
        assertEquals("PARENT_PAYMENT_ID_1", recurrentPayer.getRecurrentParent().getPaymentId());
    }


    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testSearchByInvoiceIds() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.setInvoiceIds(List.of("INVOICE_ID_1", "INVOICE_ID_2"));
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(2, payments.size());
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testSearchByBankCardTokenProvider() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams().setPaymentTokenProvider(LegacyBankCardTokenProvider.applepay);
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());
    }

    @Test
    @Sql("classpath:data/sql/search/invoice_and_payment_search_data.sql")
    public void testSearchByBankCardPaymentSystem() {
        PaymentSearchQuery searchQuery = buildSearchQuery();
        searchQuery.getPaymentParams().setPaymentSystem(LegacyBankCardPaymentSystem.mastercard);
        var payments = searchDao.getPayments(searchQuery);
        assertEquals(1, payments.size());
    }

    private PaymentSearchQuery buildSearchQuery() {
        return new PaymentSearchQuery()
                .setCommonSearchQueryParams(new CommonSearchQueryParams()
                        .setPartyId("db79ad6c-a507-43ed-9ecf-3bbd88475b32")
                        .setFromTime("2016-10-25T15:45:20Z")
                        .setToTime("3018-10-25T18:10:10Z"))
                .setPaymentParams(new PaymentParams());
    }
}
