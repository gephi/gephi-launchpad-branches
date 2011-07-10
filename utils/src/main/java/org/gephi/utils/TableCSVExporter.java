/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.utils;

import com.csvreader.CsvWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class TableCSVExporter {

    private static final Character DEFAULT_SEPARATOR = ',';

    /**
     * <p>Export a JTable to the specified file.</p>
     * @param table Table to export
     * @param file File to write
     * @param separator Separator to use for separating values of a row in the CSV file. If null ',' will be used.
     * @param charset Charset encoding for the file
     * @param columnsToExport Indicates the indexes of the columns to export. All columns will be exported if null
     * @throws IOException When an error happens while writing the file
     */
    public static void writeCSVFile(JTable table, File file, Character separator, Charset charset, Integer[] columnsToExport) throws IOException {
        TableModel model = table.getModel();
        FileOutputStream out = new FileOutputStream(file);
        if (separator == null) {
            separator = DEFAULT_SEPARATOR;
        }

        if (columnsToExport == null) {
            columnsToExport = new Integer[model.getColumnCount()];
            for (int i = 0; i < columnsToExport.length; i++) {
                columnsToExport[i] = i;
            }
        }

        CsvWriter writer = new CsvWriter(out, separator, charset);

        //Write column headers:
        for (int column = 0; column < columnsToExport.length; column++) {
            writer.write(model.getColumnName(columnsToExport[column]), true);
        }
        writer.endRecord();

        //Write rows:
        Object value;
        String text;
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int column = 0; column < columnsToExport.length; column++) {
                value = model.getValueAt(table.convertRowIndexToModel(row), columnsToExport[column]);
                if (value != null) {
                    text = value.toString();
                } else {
                    text = "";
                }
                writer.write(text, true);
            }
            writer.endRecord();
        }
        writer.close();
    }
}
