/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.exportjs;

import java.io.Writer;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.project.api.Workspace;

/**
 *
 * @author raph
 */
public class ExportJson implements GraphExporter, CharacterExporter {
    
    private Workspace workspace;
    private Writer writer;
    private boolean exportVisible = false;
   
    @Override 
    public boolean execute() {
        try {
            ExportJsGraph JsonGraph = new ExportJsGraph();
            JsonGraph.BuildGraph(workspace);
            writer.write(JsonGraph.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }
    
    @Override
    public boolean isExportVisible() {
        return exportVisible;
    }
    
    @Override
    public void setExportVisible(boolean exportVisible) {
        this.exportVisible = exportVisible;
    }
    
    @Override
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}

