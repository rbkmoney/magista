package com.rbkmoney.magista.provider;

/**
 * Created by tolkonepiu on 08.08.16.
 */
public interface GeoProvider {

    String getCityName(String ip) throws ProviderException;

}
