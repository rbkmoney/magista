package com.rbkmoney.magista.config;

import com.rbkmoney.damsel.payment_processing.InvoiceTemplatingSrv;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class ApplicationConfig {

    @Bean
    public PartyManagementSrv.Iface partyManagementClient(
            @Value("${service.partyManagement.url}") Resource resource,
            @Value("${service.partyManagement.networkTimeout}") int networkTimeout
    ) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(PartyManagementSrv.Iface.class);
    }

    @Bean
    public InvoiceTemplatingSrv.Iface invoiceTemplatingClient(
            @Value("${service.invoiceTemplating.url}") Resource resource,
            @Value("${service.invoiceTemplating.networkTimeout}") int networkTimeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(InvoiceTemplatingSrv.Iface.class);
    }
}
