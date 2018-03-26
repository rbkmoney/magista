package com.rbkmoney.magista.query.impl.parser;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.query.impl.*;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.query.parser.QueryPart;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by vpankrashkin on 26.08.16.
 */
public class JsonQueryParserTest {

    JsonQueryParser parser = JsonQueryParser.newWeakJsonQueryParser();

    @Test
    public void testNoFunctionParse() throws Exception {
        String json = "{'query': {'payments_geo_stat1': {}}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        assertEquals("root query", 1, queryParts.size());
        assertEquals("root query has 0 parameter - no recognized function names", 0, queryParts.get(0).getChildren().size());

    }

    @Test(expected = QueryParserException.class)
    public void testNoQueryParse() {
        String json = "{'query1': {'invoices': {}}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        fail("no root query, should not reach this point");
    }

    @Test
    public void testPaymentsParse() throws Exception {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'payment_pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        assertEquals("root query", 1, queryParts.size());
        assertEquals("root query has 1 parameter - function name", 1, queryParts.get(0).getParameters().getParametersMap().size());
        assertEquals("child payments function", 1, queryParts.get(0).getChildren().size());
        assertEquals("payments function has no children", 0, queryParts.get(0).getChildren().get(0).getChildren().size());
        assertEquals("payments function has 7 parameters", 7, queryParts.get(0).getChildren().get(0).getParameters().getParametersMap().size());

        assertEquals(RootQuery.RootParser.getMainDescriptor(), queryParts.get(0).getDescriptor());
        assertEquals(queryParts.get(0).getChildren().get(0).getDescriptor(), PaymentsFunction.PaymentsParser.getMainDescriptor());

        PaymentsFunction.PaymentsParameters parameters = (PaymentsFunction.PaymentsParameters) queryParts.get(0).getChildren().get(0).getParameters();
        assertEquals("1", parameters.getMerchantId());
        assertEquals("2", parameters.getShopId());
        assertEquals("A", parameters.getInvoiceId());
        assertEquals("B", parameters.getPaymentId());
        assertEquals("12**12", parameters.getPanMask());
        assertEquals("2016-03-22T00:12:00Z", TypeUtil.temporalToString(parameters.getFromTime()));
        assertEquals("2016-03-22T01:12:00Z", TypeUtil.temporalToString(parameters.getToTime()));
        assertNull(parameters.getSize());
        assertNull(parameters.getFrom());

    }

    @Test
    public void testPaymentsParseWithPagination() throws Exception {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'payment_pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}, 'size':'2', 'from':'1'}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        assertEquals("root query", 1, queryParts.size());
        assertEquals("root query has 3 parameters - function name, pagination", 3, queryParts.get(0).getParameters().getParametersMap().size());
        assertEquals("child payments function", 1, queryParts.get(0).getChildren().size());
        assertEquals("payments function has no children", 0, queryParts.get(0).getChildren().get(0).getChildren().size());
        assertEquals("payments function has 7 parameters", 7, queryParts.get(0).getChildren().get(0).getParameters().getParametersMap().size());

        assertEquals(RootQuery.RootParser.getMainDescriptor(), queryParts.get(0).getDescriptor());
        assertEquals(queryParts.get(0).getChildren().get(0).getDescriptor(), PaymentsFunction.PaymentsParser.getMainDescriptor());

        PaymentsFunction.PaymentsParameters parameters = (PaymentsFunction.PaymentsParameters) queryParts.get(0).getChildren().get(0).getParameters();
        assertEquals("1", parameters.getMerchantId());
        assertEquals("2", parameters.getShopId());
        assertEquals("A", parameters.getInvoiceId());
        assertEquals("B", parameters.getPaymentId());
        assertEquals("12**12", parameters.getPanMask());
        assertEquals("2016-03-22T00:12:00Z", TypeUtil.temporalToString(parameters.getFromTime()));
        assertEquals("2016-03-22T01:12:00Z", TypeUtil.temporalToString(parameters.getToTime()));
        assertEquals(new Integer(2), parameters.getSize());
        assertEquals(new Integer(1), parameters.getFrom());

    }

    @Test(expected = QueryParserException.class)
    public void testPaymentsPanParseError() throws Exception {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'payment_pan_mask':'12**12!','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        parser.parseQuery(json);
    }

    @Test
    public void testWithoutShopAndMerchantId() throws Exception {
        String json = "{'query': {'payments': {'invoice_id':'A','payment_id':'B', 'payment_pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        parser.parseQuery(json);
    }

    @Test(expected = QueryParserException.class)
    public void testWithoutMerchantButWithShopId() throws Exception {
        String json = "{'query': {'payments': {'shop_id':'C','invoice_id':'A','payment_id':'B', 'payment_pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        parser.parseQuery(json);
    }

    @Test
    public void testShopCategoryId() throws Exception {
        String json = "{'query': {'payments': {'shop_category_ids': [1, 2, 3, 4, 5]}}}";
        PaymentsFunction.PaymentsParameters paymentsParameters = (PaymentsFunction.PaymentsParameters) parser.parseQuery(json).get(0).getChildren().get(0).getParameters();
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), paymentsParameters.getShopCategoryIds());
    }

    @Test
    public void testInvoicesParse() throws Exception {
        String json = "{'query': {'invoices': {'merchant_id': '1','shop_id': '2','invoice_id':'A','invoice_status':'paid','from_time': '2016-03-22T00:12:00Z'}}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        assertEquals("root query", 1, queryParts.size());
        assertEquals("root query has 1 parameter - function name", 1, queryParts.get(0).getParameters().getParametersMap().size());
        assertEquals("child payments function", 1, queryParts.get(0).getChildren().size());
        assertEquals("payments function has no children", 0, queryParts.get(0).getChildren().get(0).getChildren().size());
        assertEquals("payments function has 5 parameters", 5, queryParts.get(0).getChildren().get(0).getParameters().getParametersMap().size());

        assertEquals(RootQuery.RootParser.getMainDescriptor(), queryParts.get(0).getDescriptor());
        assertEquals(queryParts.get(0).getChildren().get(0).getDescriptor(), InvoicesFunction.InvoicesParser.getMainDescriptor());

        InvoicesFunction.InvoicesParameters parameters = (InvoicesFunction.InvoicesParameters) queryParts.get(0).getChildren().get(0).getParameters();
        assertEquals("1", parameters.getMerchantId());
        assertEquals("2", parameters.getShopId());
        assertEquals("A", parameters.getInvoiceId());
        assertEquals("paid", parameters.getInvoiceStatus());
        assertEquals("2016-03-22T00:12:00Z", TypeUtil.temporalToString(parameters.getFromTime()));
        assertNull(parameters.getToTime());
        assertNull(parameters.getFrom());
        assertNull(parameters.getSize());
    }

    @Test
    public void testInvoicesParseWithPagination() throws Exception {
        String json = "{'query': {'invoices': {'merchant_id': '1','shop_id': '2','invoice_id':'A','invoice_status':'paid','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z', 'from':'1', 'size':'2'}}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        assertEquals("root query", 1, queryParts.size());
        assertEquals("root query has 1 parameter - function name", 1, queryParts.get(0).getParameters().getParametersMap().size());
        assertEquals("child payments function", 1, queryParts.get(0).getChildren().size());
        assertEquals("payments function has no children", 0, queryParts.get(0).getChildren().get(0).getChildren().size());
        assertEquals("payments function has 8 parameters", 8, queryParts.get(0).getChildren().get(0).getParameters().getParametersMap().size());

        assertEquals(RootQuery.RootParser.getMainDescriptor(), queryParts.get(0).getDescriptor());
        assertEquals(queryParts.get(0).getChildren().get(0).getDescriptor(), InvoicesFunction.InvoicesParser.getMainDescriptor());

        InvoicesFunction.InvoicesParameters parameters = (InvoicesFunction.InvoicesParameters) queryParts.get(0).getChildren().get(0).getParameters();
        assertEquals("1", parameters.getMerchantId());
        assertEquals("2", parameters.getShopId());
        assertEquals("A", parameters.getInvoiceId());
        assertEquals("paid", parameters.getInvoiceStatus());
        assertEquals(1, parameters.getFrom().intValue());
        assertEquals(2, parameters.getSize().intValue());
        assertEquals("2016-03-22T00:12:00Z", TypeUtil.temporalToString(parameters.getFromTime()));
        assertEquals("2016-03-22T01:12:00Z", TypeUtil.temporalToString(parameters.getToTime()));
        assertEquals(new Integer(2), parameters.getSize());
        assertEquals(new Integer(1), parameters.getFrom());

    }

    @Test(expected = QueryParserException.class)
    public void testInvoicesTimeParseError() throws Exception {
        String json = "{'query': {'invoices': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T00:00:00Z'}}}";
        parser.parseQuery(json);
    }

    @Test
    public void testCustomersRateStatParse() throws Exception {
        String json = "{'query': {'customers_rate_stat': {'merchant_id': '1','shop_id': '2', 'split_interval':'1','from_time': '2016-03-22T00:12:00Z', 'to_time': '2016-03-22T01:00:00Z'}}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        assertEquals("root query", 1, queryParts.size());
        assertEquals("root query has 1 parameter - function name", 1, queryParts.get(0).getParameters().getParametersMap().size());
        assertEquals("child payments function", 1, queryParts.get(0).getChildren().size());
        assertEquals("payments function has no children", 0, queryParts.get(0).getChildren().get(0).getChildren().size());
        assertEquals("payments function has 5 parameters", 5, queryParts.get(0).getChildren().get(0).getParameters().getParametersMap().size());

        assertEquals(RootQuery.RootParser.getMainDescriptor(), queryParts.get(0).getDescriptor());
        assertEquals(queryParts.get(0).getChildren().get(0).getDescriptor(), CustomersRateStatFunction.CustomersRateStatParser.getMainDescriptor());

        StatBaseFunction.StatBaseParameters parameters = (StatBaseFunction.StatBaseParameters) queryParts.get(0).getChildren().get(0).getParameters();
        assertEquals("1", parameters.getMerchantId());
        assertEquals("2", parameters.getShopId());
        assertEquals((Integer) 1, parameters.getSplitInterval());
        assertEquals("2016-03-22T00:12:00Z", TypeUtil.temporalToString(parameters.getFromTime()));
        assertEquals("2016-03-22T01:00:00Z", TypeUtil.temporalToString(parameters.getToTime()));
    }

    @Test
    public void testStatFunctionsMatch() throws Exception {
        String functionNames[] = {
                CustomersRateStatFunction.CustomersRateStatParser.getMainDescriptor(),
                PaymentsConversionStatFunction.PaymentsConversionStatParser.getMainDescriptor(),
                PaymentsGeoStatFunction.PaymentsGeoStatParser.getMainDescriptor(),
                PaymentsTurnoverStatFunction.PaymentsTurnoverStatParser.getMainDescriptor()

        };
        String json = "{'query': {'%fname%': {'merchant_id': '1','shop_id': '2', 'split_interval':'1','from_time': '2016-03-22T00:12:00Z', 'to_time': '2016-03-22T01:00:00Z'}}}";

        for (String name : functionNames) {
            List<QueryPart> queryParts = parser.parseQuery(json.replaceAll("%fname%", name));
            assertEquals(RootQuery.RootParser.getMainDescriptor(), queryParts.get(0).getDescriptor());
            assertEquals(queryParts.get(0).getChildren().get(0).getDescriptor(), name);

        }
    }
}
