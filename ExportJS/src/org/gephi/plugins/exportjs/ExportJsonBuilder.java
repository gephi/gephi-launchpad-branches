/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.exportjs;

import org.gephi.io.exporter.api.FileType;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.exporter.spi.GraphFileExporterBuilder;
import org.openide.util.lookup.ServiceProvider;
/**
 *
 * @author raph
 */
@ServiceProvider(service = GraphFileExporterBuilder.class)
public class ExportJsonBuilder implements GraphFileExporterBuilder {
    
    @Override
    public GraphExporter buildExporter() {
        return new ExportJson();
    }

    @Override
    public String getName() {
       return "json";
    }

    @Override
    public FileType[] getFileTypes() {
       return new FileType[]{new FileType(".json", "JSON File")};
    }
}
