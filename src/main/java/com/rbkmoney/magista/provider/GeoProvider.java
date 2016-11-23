package com.rbkmoney.magista.provider;

import com.rbkmoney.damsel.geo_ip.LocationInfo;

/**
 * Created by tolkonepiu on 08.08.16.
 */
public interface GeoProvider {
    LocationInfo getLocationInfo(String ip) throws ProviderException;
}
