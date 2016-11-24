package com.rbkmoney.magista.geo;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.provider.ProviderException;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexeysemenkov on 08.08.16.
 */
@Component
public class GeoProviderIpml implements GeoProvider {

    @Autowired
    private GeoIpServiceSrv.Iface columbusClient;

    @Override
    public LocationInfo getLocationInfo(String ip) throws ProviderException {
        try {
            return columbusClient.getLocation(ip);
        } catch (TException e) {
            throw new ProviderException(e);
        }
    }
}
