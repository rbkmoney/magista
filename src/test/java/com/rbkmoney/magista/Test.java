package com.rbkmoney.magista;

import com.rbkmoney.damsel.merch_stat.MerchantStatisticsSrv;
import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.apache.thrift.TException;
import org.junit.Ignore;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by vpankrashkin on 11.08.16.
 */
@Ignore
public class Test {
    @org.junit.Test
    public void test() throws URISyntaxException, TException {
        THSpawnClientBuilder clientBuilder = (THSpawnClientBuilder) new THSpawnClientBuilder().withAddress(new URI("http://localhost:8082/stat"));
        MerchantStatisticsSrv.Iface client = clientBuilder.build(MerchantStatisticsSrv.Iface.class);
        // Ordering<com.rbkmoney.damsel.domain.Invoice>
        String json = "{'query': {'invoices': {'merchant_id': 'hg_tests_SUITE','shop_id': 'THRIFT-SHOP','from_time': '2016-08-11T00:12:00Z','to_time': '2016-09-11T12:12:00Z', 'from':'1', 'size':'2'}}}";
        //String json = "{'query': {'payments': {'merchant_id': 'hg_tests_SUITE','shop_id': 'THRIFT-SHOP','from_time': '2016-08-11T00:12:00Z','to_time': '2016-09-11T12:12:00Z', 'from':'1', 'size':'2'}}}";
        //String json = "{'query': {'payments_turnover': {'merchant_id': 'hg_tests_SUITE','shop_id': 'THRIFT-SHOP','from_time': '2016-08-11T00:12:00Z','to_time': '2016-09-11T16:12:00Z', 'split_interval':'60', 'from':'1', 'size':'2'}}}";
        //String json =   "{'query': {'payments_geo_stat': {'merchant_id': 'hg_tests_SUITE','shop_id': 'THRIFT-SHOP','from_time': '2016-08-11T00:12:00Z','to_time': '2016-09-11T16:12:00Z', 'split_interval':'60'}}}";
        //String json = "{'query': {'payments_conversion_stat': {'merchant_id': 'hg_tests_SUITE','shop_id': 'THRIFT-SHOP','from_time': '2016-07-11T00:12:00Z','to_time': '2016-09-11T16:12:00Z', 'split_interval':'60'}}}";
        //String json = "{'query': {'customers_rate_stat': {'merchant_id': '42','shop_id': '45','from_time': '2016-07-11T00:12:00Z','to_time': '2016-09-11T17:12:00Z', 'split_interval':'60'}}}";


        StatResponse response = client.getInvoices(new StatRequest(json));
        System.out.println(response);
    }
}
