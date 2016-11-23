package com.rbkmoney.magista.config;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.woody.api.ClientBuilder;
import com.rbkmoney.woody.api.event.ClientEventListener;
import com.rbkmoney.woody.api.event.CompositeClientEventListener;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import com.rbkmoney.woody.thrift.impl.http.event.ClientEventLogListener;
import com.rbkmoney.woody.thrift.impl.http.event.HttpClientEventLogListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Created by tolkonepiu on 29.06.16.
 */
@Configuration
public class ColumbusConfig {

    @Value("${columbus.url}")
    Resource resource;

    @Bean
    public GeoIpServiceSrv.Iface columbusClient() throws IOException {
        return clientBuilder()
                .withEventListener(eventListener())
                .withAddress(resource.getURI()).build(GeoIpServiceSrv.Iface.class);
    }

    @Bean
    public ClientEventListener eventListener() {
        return new CompositeClientEventListener(
                new ClientEventLogListener(),
                new HttpClientEventLogListener());
    }

    @Bean
    public ClientBuilder clientBuilder() {
        return new THSpawnClientBuilder();
    }
}
