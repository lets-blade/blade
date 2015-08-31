package org.sql2o.data;

import java.util.*;

/**
 * Represents an offline result set with columns and rows and data.
 */
public class Table {
    private String name;
    private List<Row> rows;
    private List<Column> columns;

    public Table(String name, List<Row> rows, List<Column> columns) {
        this.name = name;
        this.rows = rows;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public List<Row> rows() {
        return rows;
    }

    public List<Column> columns() {
        return columns;
    }

    public List<Map<String, Object>> asList()
    {
        return new AbstractList<Map<String, Object>>() {
            @Override
            public Map<String, Object> get(int index) {
                return rows.get(index).asMap();
            }

            @Override
            public int size() {
                return rows.size();
            }
        };
    }
}
