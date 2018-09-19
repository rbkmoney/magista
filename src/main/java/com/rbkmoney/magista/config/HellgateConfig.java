package com.rbkmoney.magista.config;

import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
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

    @Bean
    public PartyManagementSrv.Iface partyManagementSrv(@Value("${hellgate.url}") Resource resource, @Value("${hellgate.timeout}") int timeout) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(resource.getURI())
                .withNetworkTimeout(timeout)
                .build(PartyManagementSrv.Iface.class);
    }
}
