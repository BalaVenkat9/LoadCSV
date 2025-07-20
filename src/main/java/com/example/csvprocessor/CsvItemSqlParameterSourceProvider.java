package com.example.csvprocessor;

import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class CsvItemSqlParameterSourceProvider implements ItemSqlParameterSourceProvider<String> {

    @Override
    public SqlParameterSource createSqlParameterSource(String item) {
        String[] fields = item.split(",");
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        for (int i = 0; i < fields.length; i++) {
            parameterSource.addValue("field" + (i + 1), fields[i]);
        }
        return parameterSource;
    }
}
