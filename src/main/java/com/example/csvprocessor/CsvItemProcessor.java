package com.example.csvprocessor;

import org.springframework.batch.item.ItemProcessor;

public class CsvItemProcessor implements ItemProcessor<String, String> {

    @Override
    public String process(String item) {
        // Convert line endings from LF to CRLF
        return item.replaceAll("\n", "\r\n");
    }
}
