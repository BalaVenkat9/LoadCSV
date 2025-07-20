package com.example.csvprocessor;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

public class CsvItemWriter extends JdbcBatchItemWriter<String> {

    public CsvItemWriter(DataSource dataSource, String tableName, String[] tableFields) {
        super();
        this.setDataSource(dataSource);
        this.setSql("INSERT INTO " + tableName + " (" + String.join(",", tableFields) + ") VALUES (" + createNamedParameters(tableFields) + ")");
        this.setItemSqlParameterSourceProvider(new CsvItemSqlParameterSourceProvider());
    }

    private String createNamedParameters(String[] tableFields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableFields.length; i++) {
            sb.append(":field" + (i + 1));
            if (i < tableFields.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
