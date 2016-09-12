package com.rbkmoney.magista.geo;

import com.rbkmoney.magista.geo.dao.CityLocationsDao;
import com.rbkmoney.magista.geo.dao.MaxMindDbDao;
import com.rbkmoney.magista.geo.dto.CityLocation;
import com.rbkmoney.magista.geo.dto.Lang;
import com.rbkmoney.magista.geo.dto.LocationInfo;
import com.rbkmoney.magista.service.InvoiceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GeoServiceTest {

    @Autowired
    MaxMindDbDao maxMindDbDao;

    @Autowired
    CityLocationsDao cityLocationsDao;

    @Autowired
    GeoEnrichmentService geoEnrichmentService;


    @Test
    public void getLocationByGeoId(){
        CityLocation byGeoId = cityLocationsDao.getByGeoId(71137, Lang.RU);
        System.out.println(byGeoId.getCityName());
    }

    @Test
    public void getGeoDataBYIdTest(){
        geoEnrichmentService.enrich();
    }


    @Test
    public void testMoscow() {
        LocationInfo locationInfoByIp = maxMindDbDao.getLocationInfoByIp("94.159.54.234");
        assertEquals("Москва", locationInfoByIp.getCity().getNames().get(Lang.RU));
    }

    @Test
    public void testWrongIp(){
        LocationInfo undefinedLocation = maxMindDbDao.getLocationInfoByIp("null");
        assertEquals("Неизвестно",undefinedLocation.getCity().getNames().get(Lang.RU));
    }

}
