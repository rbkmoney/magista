package com.rbkmoney.magista.geo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Формат ответа сервиса
 * <p>
 * {
 * "ip": "2001:470:df50:1065:9034:1741:6551:f71d",
 * "country_code": "RU",
 * "country_name": "Russia",
 * "region_code": "MOW",
 * "region_name": "Moscow",
 * "city": "Moscow",
 * "zip_code": "111024",
 * "time_zone": "Europe/Moscow",
 * "latitude": 55.752,
 * "longitude": 37.615,
 * "metro_code": 0
 * }
 */
public class FreeGeoIpResponse {

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("region_code")
    private String regionCode;

    @JsonProperty("region_name")
    private String regionName;

    @JsonProperty("city")
    private String city;

    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("time_zone")
    private String timeZone;

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("longitude")
    private String longitude;

    @JsonProperty("metro_code")
    private String metroCode;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMetroCode() {
        return metroCode;
    }

    public void setMetroCode(String metroCode) {
        this.metroCode = metroCode;
    }
}