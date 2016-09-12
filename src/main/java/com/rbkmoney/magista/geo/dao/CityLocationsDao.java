package com.rbkmoney.magista.geo.dao;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.geo.dto.CityLocation;
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


public interface CityLocationsDao {

    CityLocation getByGeoId(int geoId, Lang lang) throws DaoException;

}

