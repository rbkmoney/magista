package com.rbkmoney.magista.query.impl.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista.query2.impl.*;
import com.rbkmoney.magista.query2.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.query2.parser.QueryParserException;
import com.rbkmoney.magista.query2.parser.QueryPart;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by vpankrashkin on 26.08.16.
 */
public class JsonQueryParserTest {

    JsonQueryParser parser = new JsonQueryParser() {
        @Override
        protected ObjectMapper getMapper() {
            ObjectMapper mapper = super.getMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            return mapper;
        }
    };

    @Test
    public void testNoFunctionParse() throws Exception {
        String json = "{'query': {'payments_geo_stat1': {}}}";
            List<QueryPart> queryParts = parser.parseQuery(json);
        assertEquals("root query", 1, queryParts.size());
        assertEquals("root query has 0 parameter - no recognized function names", 0, queryParts.get(0).getChildren().size());

    }

    @Test(expected = QueryParserException.class)
    public void testNoQueryParse () {
        String json = "{'query1': {'invoices': {}}}";
        List<QueryPart>  queryParts = parser.parseQuery(json);
        fail("no root query, should not reach this point");
    }

    @Test
    public  void  testPaymentsParse() throws Exception {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
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
        assertEquals("2016-03-22T00:12:00Z", TemporalConverter.temporalToString(parameters.getFromTime()));
        assertEquals("2016-03-22T01:12:00Z", TemporalConverter.temporalToString(parameters.getToTime()));
        assertNull(parameters.getSize());
        assertNull(parameters.getFrom());

    }

    @Test
    public  void  testPaymentsParseWithPagination() throws Exception {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}, 'size':'2', 'from':'1'}}";
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
        assertEquals("2016-03-22T00:12:00Z", TemporalConverter.temporalToString(parameters.getFromTime()));
        assertEquals("2016-03-22T01:12:00Z", TemporalConverter.temporalToString(parameters.getToTime()));
        assertEquals(new Integer(2), parameters.getSize());
        assertEquals(new Integer(1), parameters.getFrom());

    }

    @Test(expected = QueryParserException.class)
    public  void  testPaymentsPanParseError() throws Exception {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'pan_mask':'12**12!','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        try {
            List<QueryPart> queryParts = parser.parseQuery(json);
        } catch (QueryParserException e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Test(expected = QueryParserException.class)
    public  void  testPaymentsShopParseError() throws Exception {
        String json = "{'query': {'payments': {'merchant_id': '1','invoice_id':'A','payment_id':'B', 'pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        try {
            List<QueryPart> queryParts = parser.parseQuery(json);
        } catch (QueryParserException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public  void  testInvoicesParse() throws Exception {
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
        assertEquals("2016-03-22T00:12:00Z", TemporalConverter.temporalToString(parameters.getFromTime()));
        assertNull(parameters.getToTime());
        assertNull(parameters.getFrom());
        assertNull(parameters.getSize());
    }

    @Test
    public  void  testInvoicesParseWithPagination() throws Exception {
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
        assertEquals("2016-03-22T00:12:00Z", TemporalConverter.temporalToString(parameters.getFromTime()));
        assertEquals("2016-03-22T01:12:00Z", TemporalConverter.temporalToString(parameters.getToTime()));
        assertEquals(new Integer(2), parameters.getSize());
        assertEquals(new Integer(1), parameters.getFrom());

    }

    @Test(expected = QueryParserException.class)
    public  void  testInvoicesTimeParseError() throws Exception {
        String json = "{'query': {'invoices': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T00:00:00Z'}}}";
        try {
            List<QueryPart> queryParts = parser.parseQuery(json);
        } catch (QueryParserException e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Test(expected = QueryParserException.class)
    public  void  testInvoicesMerchantParseError() throws Exception {
        String json = "{'query': {'invoices': {'shop_id': '1','invoice_id':'A','payment_id':'B', 'pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        try {
            List<QueryPart> queryParts = parser.parseQuery(json);
        } catch (QueryParserException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public  void  testCustomersRateStatParse() throws Exception {
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
        assertEquals((Integer)1, parameters.getSplitInterval());
        assertEquals("2016-03-22T00:12:00Z", TemporalConverter.temporalToString(parameters.getFromTime()));
        assertEquals("2016-03-22T01:00:00Z", TemporalConverter.temporalToString(parameters.getToTime()));
    }

    @Test
    public  void  testStatFunctionsMatch() throws Exception {
        String functionNames[] = {
                CustomersRateStatFunction.CustomersRateStatParser.getMainDescriptor(),
                PaymentsConversionStatFunction.PaymentsConversionStatParser.getMainDescriptor(),
                PaymentsGeoStatFunction.PaymentsGeoStatParser.getMainDescriptor(),
                PaymentsTurnoverStatFunction.PaymentsTurnoverStatParser.getMainDescriptor()

        };
        String json =  "{'query': {'%fname%': {'merchant_id': '1','shop_id': '2', 'split_interval':'1','from_time': '2016-03-22T00:12:00Z', 'to_time': '2016-03-22T01:00:00Z'}}}";

        for (String name: functionNames) {
            List<QueryPart> queryParts = parser.parseQuery(json.replaceAll("%fname%", name));
            assertEquals(RootQuery.RootParser.getMainDescriptor(), queryParts.get(0).getDescriptor());
            assertEquals(queryParts.get(0).getChildren().get(0).getDescriptor(), name);

        }
    }
}
