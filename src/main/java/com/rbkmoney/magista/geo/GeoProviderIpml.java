package com.rbkmoney.magista.geo;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.provider.ProviderException;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by alexeysemenkov on 08.08.16.
 */
@Component
public class GeoProviderIpml implements GeoProvider {

    @Value("${columbus.url}")
    private String columbusUrl;

    private GeoIpServiceSrv.Iface columbusClient;

    @PostConstruct
    public void init() throws URISyntaxException {
        THSpawnClientBuilder clientBuilder = new THSpawnClientBuilder()
                .withAddress(new URI(columbusUrl));
        this.columbusClient = clientBuilder.build(GeoIpServiceSrv.Iface.class);
    }

    @Override
    public LocationInfo getLocationInfo(String ip) throws ProviderException {
        try {
            return columbusClient.getLocation(ip);
        } catch (TException e) {
            throw new ProviderException(e);
        }
    }
}
