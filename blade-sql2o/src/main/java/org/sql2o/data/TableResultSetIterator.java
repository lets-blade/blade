package org.sql2o.data;

import org.sql2o.ResultSetIteratorBase;
import org.sql2o.Sql2oException;
import org.sql2o.quirks.Quirks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author aldenquimby@gmail.com
 */
public class TableResultSetIterator extends ResultSetIteratorBase<Row> {
    private Map<String, Integer> columnNameToIdxMap;
    private List<Column> columns;

    public TableResultSetIterator(ResultSet rs, boolean isCaseSensitive, Quirks quirks, LazyTable lt) {
        super(rs, isCaseSensitive, quirks);

        this.columnNameToIdxMap = new HashMap<String, Integer>();
        this.columns = new ArrayList<Column>();

        try {
            lt.setName(meta.getTableName(1));

            for (int colIdx = 1; colIdx <= meta.getColumnCount(); colIdx++){
                String colName = getColumnName(colIdx);
                String colType = meta.getColumnTypeName(colIdx);
                columns.add(new Column(colName, colIdx - 1, colType));

                String colMapName = isCaseSensitive ? colName : colName.toLowerCase();
                columnNameToIdxMap.put(colMapName, colIdx - 1);
            }
        }
        catch (SQLException e) {
            throw new Sql2oException("Error while reading metadata from database", e);
        }

        lt.setColumns(columns);
    }

    @Override
    protected Row readNext() throws SQLException {
        Row row = new Row(columnNameToIdxMap, columns.size(), isCaseSensitive,this.quirks);
        for (Column column : columns) {
            row.addValue(column.getIndex(), quirks.getRSVal(rs, column.getIndex() + 1));
        }
        return row;
    }
}
