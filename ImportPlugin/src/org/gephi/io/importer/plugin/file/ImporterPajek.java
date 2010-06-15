/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.io.importer.plugin.file;

import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.StringTokenizer;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.ImportUtils;
import org.gephi.io.importer.api.Issue;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.spi.FileImporter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class ImporterPajek implements FileImporter, LongTask {

    //Architecture
    private Reader reader;
    private LineNumberReader lineReader;
    private ContainerLoader container;
    private Report report;
    private ProgressTicket progressTicket;
    private boolean cancel = false;
    //Node data
    private NodeDraft[] verticesArray;

    public boolean execute(ContainerLoader container) {
        this.container = container;
        this.report = new Report();
        lineReader = ImportUtils.getTextReader(reader);
        try {
            importData(lineReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return !cancel;
    }

    private void importData(LineNumberReader reader) throws Exception {
        Progress.start(progressTicket);        //Progress

        try {
            // ignore everything until we see '*Vertices'
            String curLine = skip(reader, "*vertices");

            if (curLine == null) // no vertices in the graph; return empty graph
            {
                report.logIssue(new Issue(NbBundle.getMessage(ImporterPajek.class, "importerNET_error_dataformat1"), Issue.Level.CRITICAL));
            }

            // create appropriate number of vertices
            StringTokenizer stringTokenizer = new StringTokenizer(curLine);
            stringTokenizer.nextToken(); // skip past "*vertices";
            int num_vertices = Integer.parseInt(stringTokenizer.nextToken());

            Progress.switchToDeterminate(progressTicket, num_vertices);        //Progress

            verticesArray = new NodeDraft[num_vertices];
            for (int i = 0; i < num_vertices; i++) {
                NodeDraft node = container.factory().newNodeDraft();
                String label = "" + (i + 1);
                node.setId(label);
                node.setLabel(label);
                verticesArray[i] = node;
            }

            curLine = null;
            while (reader.ready()) {
                if (cancel) {
                    reader.close();
                    return;
                }
                curLine = reader.readLine();
                if (curLine == null || curLine.startsWith("*")) {
                    break;
                }
                if (curLine.isEmpty()) { // skip blank lines
                    report.logIssue(new Issue(NbBundle.getMessage(ImporterPajek.class, "importerNET_error_dataformat2", reader.getLineNumber()), Issue.Level.WARNING));
                    continue;
                }

                try {
                    readVertex(curLine, num_vertices);
                } catch (IllegalArgumentException iae) {
                    reader.close();
                    throw iae;
                }

                Progress.progress(progressTicket);
            }

            //Append nodes
            for (NodeDraft node : verticesArray) {
                container.addNode(node);
            }

            //Get arcs
            curLine = readArcsOrEdges(curLine, reader);

            //Get edges
            readArcsOrEdges(curLine, reader);

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        Progress.finish(progressTicket);
    }

    private void readVertex(String curLine, int num_vertices) throws Exception {
        String[] parts = null;
        int firstParts = -1;     // index of first coordinate in parts; -1 indicates no coordinates found
        String index;
        String label = null;
        // if there are quote marks on this line, split on them; label is surrounded by them
        if (curLine.indexOf('"') != -1) {
            String[] initial_split = curLine.trim().split("\"");
            // if there are any quote marks, there should be exactly 2
            if (initial_split.length < 1 || initial_split.length > 3) {
                report.logIssue(new Issue(NbBundle.getMessage(ImporterPajek.class, "importerNET_error_dataformat3", lineReader.getLineNumber()), Issue.Level.SEVERE));
            }
            index = initial_split[0].trim();
            if (initial_split.length > 1) {
                label = initial_split[1].trim();
            }

            if (initial_split.length == 3) {
                parts = initial_split[2].trim().split("\\s+", -1);
            }
            firstParts = 0;
        } else // no quote marks, but are there coordinates?
        {
            parts = curLine.trim().split("\\s+", -1);
            index = parts[0];
            switch (parts.length) {
                case 1:         // just the ID; nothing to do, continue
                    break;
                case 2:         // just the ID and a label
                    label = parts[1];
                    break;
                case 3:         // ID, no label, coordinates
                    firstParts = 1;
                    break;
                case 4:         // ID, label, (x,y) coordinates
                    firstParts = 2;
                    break;
            }
        }
        int v_id = Integer.parseInt(index) - 1; // go from 1-based to 0-based index
        if (v_id >= num_vertices || v_id < 0) {
            report.logIssue(new Issue(NbBundle.getMessage(ImporterPajek.class, "importerNET_error_dataformat4", v_id, num_vertices), Issue.Level.SEVERE));
        }

        NodeDraft node = verticesArray[v_id];

        // only attach the label if there's one to attach
        if (label != null && label.length() > 0) {
            node.setLabel(label);
        }

        // parse the rest of the line
        if (firstParts != -1 && parts != null && parts.length >= firstParts + 2) {
            for (int i = firstParts; i < parts.length; i++) {
                //Coordinates
                if (i < parts.length - 1) {
                    try {
                        float x = Float.parseFloat(parts[i]);
                        float y = Float.parseFloat(parts[i + 1]);

                        node.setX(x);
                        node.setY(y);

                        i++;
                    } catch (Exception e) {
                        report.logIssue(new Issue(NbBundle.getMessage(ImporterPajek.class, "importerNET_error_dataformat5", lineReader.getLineNumber()), Issue.Level.WARNING));
                    }
                }

                if (parts[i].equals("ic")) {
                }
            }
        }
    }

    private String readArcsOrEdges(String curLine, BufferedReader br) throws Exception {
        String nextLine = curLine;

        boolean reading_arcs = false;
        boolean reading_edges = false;

        if (nextLine.toLowerCase().startsWith("*arcs")) {
            reading_arcs = true;
        } else if (nextLine.toLowerCase().startsWith("*edges")) {
            reading_edges = true;
        }

        if (!(reading_arcs || reading_edges)) {
            return nextLine;
        }

        boolean is_list = false;
        if (nextLine.toLowerCase().endsWith("list")) {
            is_list = true;
        }

        while (br.ready()) {
            if (cancel) {
                return nextLine;
            }
            nextLine = br.readLine();
            if (nextLine == null || nextLine.startsWith("*")) {
                break;
            }
            if (nextLine.equals("")) { // skip blank lines
                report.logIssue(new Issue(NbBundle.getMessage(ImporterPajek.class, "importerNET_error_dataformat2", lineReader.getLineNumber()), Issue.Level.WARNING));
                continue;
            }

            StringTokenizer st = new StringTokenizer(nextLine.trim());

            int vid1 = Integer.parseInt(st.nextToken()) - 1;
            NodeDraft nodeFrom = verticesArray[vid1];

            if (is_list) // one source, multiple destinations
            {
                do {
                    int vid2 = Integer.parseInt(st.nextToken()) - 1;
                    NodeDraft nodeTo = verticesArray[vid2];
                    EdgeDraft edge = container.factory().newEdgeDraft();
                    edge.setSource(nodeFrom);
                    edge.setTarget(nodeTo);
                    container.addEdge(edge);
                } while (st.hasMoreTokens());
            } else // one source, one destination, at most one weight
            {
                int vid2 = Integer.parseInt(st.nextToken()) - 1;
                NodeDraft nodeTo = verticesArray[vid2];
                EdgeDraft edge = container.factory().newEdgeDraft();
                edge.setSource(nodeFrom);
                edge.setTarget(nodeTo);

                // get the edge weight if we care
                if (st.hasMoreTokens()) {
                    float edgeWeight = 1f;
                    try {
                        edgeWeight = new Float(st.nextToken());
                    } catch (Exception e) {
                        report.logIssue(new Issue(NbBundle.getMessage(ImporterPajek.class, "importerNET_error_dataformat5", lineReader.getLineNumber()), Issue.Level.WARNING));
                    }

                    edge.setWeight(edgeWeight);
                }

                container.addEdge(edge);
            }
        }
        return nextLine;
    }

    private String skip(BufferedReader br, String str) throws Exception {
        while (br.ready()) {
            String curLine = br.readLine();
            if (curLine == null) {
                break;
            }
            curLine = curLine.trim();
            if (curLine.toLowerCase().startsWith(str)) {
                return curLine;
            }
        }
        return null;
    }

    public boolean cancel() {
        cancel = true;
        return true;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public ContainerLoader getContainer() {
        return container;
    }

    public Report getReport() {
        return report;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
