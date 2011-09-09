/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.exportjs;

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Writer;
import org.gephi.io.exporter.spi.VectorExporter;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.project.api.Workspace;
/**
 *
 * @author raph
 */
public class ExportHtml implements VectorExporter, CharacterExporter {
    
    private Workspace workspace;
    private Writer writer;
   
    @Override 
    public boolean execute() {
        try {
            writeFromJar("part1.txt");
            ExportJsGraph JsonGraph = new ExportJsGraph();
            JsonGraph.BuildGraph(workspace);
            writer.write(JsonGraph.toString());
            writeFromJar("part2.txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }
    

    private void writeFromJar(String source) throws Exception {
        InputStream is = getClass().getResourceAsStream("/org/gephi/plugins/exportjs/resources/" + source);
        char[] buffer = new char[1024];
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int length;
        while ((length = reader.read(buffer)) > 0) {
            writer.write(buffer, 0, length);
        }
        is.close();
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
