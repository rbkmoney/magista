package com.rbkmoney.magista.geo;

import com.rbkmoney.magista.geo.dao.CityLocationsDao;
import com.rbkmoney.magista.geo.dao.CityLocationsDaoImpl;
import com.rbkmoney.magista.geo.dao.MaxMindDbDao;
import com.rbkmoney.magista.geo.dto.CityLocation;
import com.rbkmoney.magista.geo.dto.Lang;
import com.rbkmoney.magista.geo.dto.LocationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeoEnrichmentService {

    @Autowired
    CityLocationsDao cityLocationsDao;

    @Autowired
    MaxMindDbDao maxMindDbDao;

    public void enrich(){
        String ip = "94.159.54.234";
        LocationInfo locationInfoByIp = maxMindDbDao.getLocationInfoByIp(ip);
    }

    public CityLocation getLocationByGeoId(int geoId ){
        return cityLocationsDao.getByGeoId(geoId, Lang.RU);
    }
}
