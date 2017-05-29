package com.rbkmoney.magista.config;

import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
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
 * Created by tolkonepiu on 29/05/2017.
 */

@Configuration
public class HellgateConfig {

    @Value("${hellgate.url}")
    Resource resource;

    @Bean
    public PartyManagementSrv.Iface partyManagementSrv() throws IOException {
        return new THSpawnClientBuilder()
                .withEventListener(new CompositeClientEventListener(
                        new ClientEventLogListener(),
                        new HttpClientEventLogListener()))
                .withAddress(resource.getURI()).build(PartyManagementSrv.Iface.class);
    }
}
