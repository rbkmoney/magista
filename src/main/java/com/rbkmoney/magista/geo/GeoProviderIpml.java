package com.rbkmoney.magista.geo;

import com.rbkmoney.magista.geo.dto.FreeGeoIpResponse;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.provider.ProviderException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * Created by alexeysemenkov on 08.08.16.
 */
@Component
public class GeoProviderIpml implements GeoProvider {
    private RestTemplate restTemplate;

    private final String GEO_API_URL = "http://freegeoip.net/json/";

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
    }

    /**
     * Возвращает город по IP
     *
     * @throws ProviderException - ошибка вызова внешнего рест сервиса
     */
    @Override
    public String getCityName(String ip) throws ProviderException {
        try {
            if (!StringUtils.isEmpty(ip)) {
                return "UNKNOWN";
            }

            String uri = GEO_API_URL + ip;
            FreeGeoIpResponse resp = restTemplate.getForObject(uri, FreeGeoIpResponse.class);

            //Default city Mirny
            if (!StringUtils.hasText(resp.getCity())) {
                return "Mirny";
            }
            return resp.getCity();
        } catch (RestClientException e) {
            throw new ProviderException("Error rest client access", e);
        }
    }
}
