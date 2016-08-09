package com.rbkmoney.magista.geo;

import com.rbkmoney.magista.MagistaApplication;
import com.rbkmoney.magista.geo.dto.FreeGeoIpResponse;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.provider.ProviderException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Created by alexeysemenkov on 08.08.16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GeoDataServiceTest {


    @Autowired
    GeoProvider geoProvider;

    @Test
    public void getInfoFromService() {
        try {
            String cityName = geoProvider.getCityName("2001:470:df50:1065:9034:1741:6551:f71d");
            assertEquals("Moscow", cityName);
        } catch (ProviderException e) {
            e.printStackTrace();
        }
    }

}
