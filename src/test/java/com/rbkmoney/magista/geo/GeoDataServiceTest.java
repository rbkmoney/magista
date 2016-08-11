package com.rbkmoney.magista.geo;

import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.provider.ProviderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
            String cityName = geoProvider.getCityName("94.245.156.53");
            assertEquals("Mirny", cityName);
        } catch (ProviderException e) {
            e.printStackTrace();
        }
    }

}
