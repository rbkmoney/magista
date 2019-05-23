package com.rbkmoney.magista.geo;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.provider.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
public class GeoProviderImpl implements GeoProvider {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final GeoIpServiceSrv.Iface columbusClient;

    @Autowired
    public GeoProviderImpl(GeoIpServiceSrv.Iface columbusClient) {
        this.columbusClient = columbusClient;
    }

    @Override
    public LocationInfo getLocationInfo(String ip) throws ProviderException {
        try {
            return columbusClient.getLocation(ip);
        } catch (Exception ex) {
            log.warn("Failed to get location info, ip='{}'", ip, ex);
            throw new ProviderException(ex);
        }
    }
}
