package com.rbkmoney.magista.dao.impl.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class ColumnStringMapRowMapper implements RowMapper<Map<String, String>> {

    @Override
    public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Map<String, String> mapOfColumnValues = createColumnMap(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            String column = JdbcUtils.lookupColumnName(rsmd, i);
            mapOfColumnValues.putIfAbsent(getColumnKey(column), getColumnValue(rs, i));
        }
        return mapOfColumnValues;
    }

    protected Map<String, String> createColumnMap(int columnCount) {
        return new HashMap<>(columnCount);
    }

    protected String getColumnKey(String columnName) {
        return columnName;
    }

    @Nullable
    protected String getColumnValue(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

}
