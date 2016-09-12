package com.rbkmoney.magista.geo.dao;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.rbkmoney.magista.geo.dto.GeoNameIdInfo;
import com.rbkmoney.magista.geo.dto.Lang;
import com.rbkmoney.magista.geo.dto.LocationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


/**
 * Read information from .mmdb file
 */
@Component
public class MaxMindDbDao {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${geo.db.file.path}")
    private String geoDbFilePath;

    private DatabaseReader reader;

    @PostConstruct
    public void init() throws IOException {
        // A File object pointing to your GeoIP2 or GeoLite2 database
        File database = new File(geoDbFilePath);
        reader = new DatabaseReader.Builder(database).build();
    }

    public LocationInfo getLocationInfoByIp(String ip) {
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            log.error("Cant parse ip address", e);
            return buildUndefinedLocation();
        }

        try {
            CityResponse response = reader.city(ipAddress);
            response.getCity();
            LocationInfo locationInfo = buildLocationInfo(response);
            return locationInfo;
        } catch (IOException e) {
            log.error("DB file access error", e);
        } catch (GeoIp2Exception e) {
            log.error("GEO DB error", e);
        }
        return buildUndefinedLocation();
    }


    private LocationInfo buildLocationInfo(CityResponse cityResponse) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setCity(new GeoNameIdInfo(
                cityResponse.getCity().getGeoNameId(),
                mapNames(cityResponse.getCity().getNames()),
                GeoNameIdInfo.GeoNameType.CITY
        ));
        locationInfo.setSubdivision(new GeoNameIdInfo(
                cityResponse.getLeastSpecificSubdivision().getGeoNameId(),
                mapNames(cityResponse.getLeastSpecificSubdivision().getNames()),
                GeoNameIdInfo.GeoNameType.SUBDIVISION
        ));
        locationInfo.setCountry(new GeoNameIdInfo(
                cityResponse.getCountry().getGeoNameId(),
                mapNames(cityResponse.getCountry().getNames()),
                GeoNameIdInfo.GeoNameType.COUNTRY
        ));
        locationInfo.setLatitude(cityResponse.getLocation().getLatitude());
        locationInfo.setLongitude(cityResponse.getLocation().getLongitude());
        locationInfo.setConfidence(cityResponse.getCity().getConfidence());
        locationInfo.setTimeZone(cityResponse.getLocation().getTimeZone());
        return locationInfo;
    }

    private Map<Lang, String> mapNames(Map<String, String> names) {
        HashMap<Lang, String> result = new HashMap<>();
        names.entrySet().stream().forEach(entry -> {
            result.put(Lang.getByAbbreviation(entry.getKey()), entry.getValue());
        });
        return result;
    }

    private LocationInfo buildUndefinedLocation() {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setCountry(GeoNameIdInfo.buildUndefined());
        locationInfo.setSubdivision(GeoNameIdInfo.buildUndefined());
        locationInfo.setCity(GeoNameIdInfo.buildUndefined());
        locationInfo.setTimeZone("UNDEFINED");
        return locationInfo;
    }
}
