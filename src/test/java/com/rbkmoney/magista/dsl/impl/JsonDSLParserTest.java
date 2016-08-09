package com.rbkmoney.magista.dsl.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class JsonDSLParserTest {
    JsonDSLParser parser = new JsonDSLParser() {
        @Override
        protected ObjectMapper getMapper() {
            ObjectMapper mapper = super.getMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            return mapper;
        }
    };
    @Test
    public void testParse() throws Exception {
        String json = "{'query': {'payments_geo_stat': {}}}";
        try {
            parser.parse(json);
        } catch (Exception e) {
            assertEquals("No such function", "Function not found: payments_geo_stat", e.getMessage());
        }
        json = "{'query': {'invoices': {}}}";
        try {
            parser.parse(json);
        } catch (Exception e) {
            assertTrue("Properties expected", e.getMessage().endsWith("not found"));
        }


    }

    @Test
    public  void  testInvoicesParse() throws Exception {
        String json = "{'query': {'invoices': {'merchant_id': '1','shop_id': '2','invoice_id':'A','invoice_status':'paid','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z', 'from':'1', 'size':'2'}}}";
        InvoicesFunction query = (InvoicesFunction) parser.parse(json);
        assertEquals("1", query.getMerchantId());
        assertEquals("2", query.getShopId());
        assertEquals("A", query.getInvoiceId());
        assertEquals("paid", query.getInvocieStatus());
        assertEquals("2016-03-22T00:12:00Z", TemporalConverter.temporalToString(query.getFromTime()));
        assertEquals("2016-03-22T01:12:00Z", TemporalConverter.temporalToString(query.getToTIme()));
        assertEquals((Integer)1, query.getFrom());
        assertEquals(2, query.getSize().intValue());
    }

    @Test
    public  void  testPaymentsParse() throws Exception {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        PaymentsFunction query = (PaymentsFunction) parser.parse(json);
        assertEquals("1", query.getMerchantId());
        assertEquals("2", query.getShopId());
        assertEquals("A", query.getInvoiceId());
        assertEquals("B", query.getPaymentId());
        assertEquals("12**12", query.getPanMask());
        assertEquals("2016-03-22T00:12:00Z", TemporalConverter.temporalToString(query.getFromTime()));
        assertEquals("2016-03-22T01:12:00Z", TemporalConverter.temporalToString(query.getToTIme()));
    }
}
