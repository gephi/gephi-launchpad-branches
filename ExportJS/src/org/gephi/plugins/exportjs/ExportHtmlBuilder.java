/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.exportjs;

import org.gephi.io.exporter.api.FileType;
import org.gephi.io.exporter.spi.VectorExporter;
import org.gephi.io.exporter.spi.VectorFileExporterBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author raph
 */
@ServiceProvider(service = VectorFileExporterBuilder.class)
public class ExportHtmlBuilder implements VectorFileExporterBuilder {
    
    @Override
    public VectorExporter buildExporter() {
        return new ExportHtml();
    }

    @Override
    public String getName() {
       return "html";
    }

    @Override
    public FileType[] getFileTypes() {
       return new FileType[]{new FileType(".html", "Standalone Web Application")};
    }
}
