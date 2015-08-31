package org.sql2o.data;

/**
 * Represents a result set column
 */
public class Column {
    
    private String name;
    private Integer index;
    private String type;

    public Column(String name, Integer index, String type) {
        this.name = name;
        this.index = index;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Integer getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return getName() + " (" + getType() + ")";
    }
}
