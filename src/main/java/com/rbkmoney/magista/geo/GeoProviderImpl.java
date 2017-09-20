package com.rbkmoney.magista.geo;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.provider.ProviderException;
import com.rbkmoney.woody.api.flow.error.WRuntimeException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Created by alexeysemenkov on 08.08.16.
 */
@Component
public class GeoProviderImpl implements GeoProvider {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final GeoIpServiceSrv.Iface columbusClient;

    private final RetryTemplate retryTemplate;

    @Autowired
    public GeoProviderImpl(GeoIpServiceSrv.Iface columbusClient) {
        this.columbusClient = columbusClient;

        retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(
                new SimpleRetryPolicy(3, Collections.singletonMap(WRuntimeException.class, true))
        );
    }

    @Override
    public LocationInfo getLocationInfo(String ip) throws ProviderException {
        return retryTemplate.execute(
                context -> {
                    if (context.getLastThrowable() != null) {
                        log.error("Failed to get location info (ip='{}'), retrying ({})...", ip, context.getRetryCount(), context.getLastThrowable());
                    }

                    try {
                        return columbusClient.getLocation(ip);
                    } catch (TException ex) {
                        throw new ProviderException(ex);
                    }
                }
        );
    }
}
