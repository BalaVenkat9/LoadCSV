package com.example.csvprocessor;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class CsvItemReader extends MultiResourceItemReader<String> {

    public CsvItemReader() {
        super();
        this.setDelegate(new CsvFileItemReader());
    }

    private static class CsvFileItemReader extends FlatFileItemReader<String> {
        public CsvFileItemReader() {
            super();
            this.setLineMapper(new DefaultLineMapper<String>() {{
                setLineTokenizer(new DelimitedLineTokenizer());
            }});
        }
    }
}
