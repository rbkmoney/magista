package com.rbkmoney.magista.geo.dao;

import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.geo.dto.CityLocation;
import com.rbkmoney.magista.geo.dto.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;

public class CityLocationsDaoImpl extends NamedParameterJdbcDaoSupport implements CityLocationsDao {



    @Override
    public CityLocation getByGeoId(int geoId, Lang lang) throws DaoException {
        CityLocation cityLocation = null;
        if (lang.equals(Lang.RU)) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            String sql = "SELECT " +
                    "geoname_id, locale_code, continent_code, continent_name, country_iso_code, " +
                    "country_name, subdivision_1_iso_code, subdivision_1_name, subdivision_2_iso_code, " +
                    "subdivision_2_name, city_name, metro_code, time_zone " +
                    "FROM mst.city_locations_ru " +
                    "WHERE geoname_id = :geoname_id ";

            MapSqlParameterSource source = new MapSqlParameterSource("geoname_id", geoId);
            log.trace("SQL: {}, Params: {}", sql, source.getValues());
            cityLocation = getNamedParameterJdbcTemplate().queryForObject(
                    sql,
                    source,
                    getRowMapper()
            );

        }
        return cityLocation;
    }

    public CityLocationsDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    public static RowMapper<CityLocation> getRowMapper() {
        return BeanPropertyRowMapper.newInstance(CityLocation.class);
    }

}
