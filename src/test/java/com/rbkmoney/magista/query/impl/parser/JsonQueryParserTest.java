package com.rbkmoney.magista.query.impl.parser;

import com.rbkmoney.magista.query2.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.query2.parser.QueryPart;
import org.junit.Test;

import java.util.List;

/**
 * Created by vpankrashkin on 26.08.16.
 */
public class JsonQueryParserTest {
    private JsonQueryParser parser = new JsonQueryParser();
    @Test
    public  void  testPaymentsParse() throws Exception {
        String json = "{'query': {'payments': {'merchant_id': '1','shop_id': '2','invoice_id':'A','payment_id':'B', 'pan_mask':'12**12','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        System.out.println(queryParts);
        /*PaymentsFunction query = (PaymentsFunction) parser.parse(json);
        assertEquals("1", query.getMerchantId());
        assertEquals("2", query.getShopId());
        assertEquals("A", query.getInvoiceId());
        assertEquals("B", query.getPaymentId());
        assertEquals("12**12", query.getPanMask());
        assertEquals("2016-03-22T00:12:00Z", TemporalConverter.temporalToString(query.getFromTime()));
        assertEquals("2016-03-22T01:12:00Z", TemporalConverter.temporalToString(query.getToTime()));*/
    }
}
