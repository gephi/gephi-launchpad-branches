/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.io.importer.plugin.file;

import java.awt.Color;
import java.io.Reader;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
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
public class ImporterGEXF2 implements FileImporter, LongTask {

    //GEXF
    private static final String NODE = "node";
    private static final String NODE_ID = "id";
    private static final String NODE_LABEL = "label";
    private static final String NODE_START = "start";
    private static final String NODE_END = "end";
    private static final String NODE_PID = "pid";
    private static final String NODE_POSITION = "viz:position";
    private static final String NODE_COLOR = "viz:color";
    private static final String NODE_SIZE = "viz:size";
    private static final String EDGE = "edge";
    private static final String EDGE_ID = "id";
    private static final String EDGE_SOURCE = "source";
    private static final String EDGE_TARGET = "target";
    private static final String EDGE_LABEL = "label";
    private static final String EDGE_TYPE = "type";
    private static final String EDGE_WEIGHT = "weight";
    private static final String EDGE_START = "start";
    private static final String EDGE_COLOR = "viz:color";
    private static final String EDGE_END = "end";
    private static final String ATTRIBUTE = "attribute";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_TITLE = "title";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_DEFAULT = "default";
    private static final String ATTRIBUTES = "attributes";
    private static final String ATTVALUE = "attvalue";
    //Architecture
    private Reader reader;
    private ContainerLoader container;
    private boolean cancel;
    private Report report;
    private ProgressTicket progress;
    private XMLStreamReader xmlReader;

    public boolean execute(ContainerLoader container) {
        this.container = container;
        this.report = new Report();
        Progress.start(progress);
        try {

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            if (inputFactory.isPropertySupported("javax.xml.stream.isValidating")) {
                inputFactory.setProperty("javax.xml.stream.isValidating", Boolean.FALSE);
            }
            inputFactory.setXMLReporter(new XMLReporter() {

                @Override
                public void report(String message, String errorType, Object relatedInformation, Location location) throws XMLStreamException {
                    System.out.println("Error:" + errorType + ", message : " + message);
                }
            });
            xmlReader = inputFactory.createXMLStreamReader(reader);

            while (xmlReader.hasNext()) {

                Integer eventType = xmlReader.next();
                if (eventType.equals(XMLEvent.START_ELEMENT)) {
                    if (NODE.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readNode(xmlReader);
                    } else if (EDGE.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readEdge(xmlReader);
                    } else if (ATTRIBUTES.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readAttributes(xmlReader);
                    }
                }
            }
            xmlReader.close();

        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
        Progress.finish(progress);
        return !cancel;
    }

    private void readNode(XMLStreamReader reader) throws Exception {
        String id = "";
        String label = "";
        String startDate = "";
        String endDate = "";

        //Attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attName = reader.getAttributeName(i).getLocalPart();
            if (NODE_ID.equalsIgnoreCase(attName)) {
                id = reader.getAttributeValue(i);
            } else if (NODE_LABEL.equalsIgnoreCase(attName)) {
                label = reader.getAttributeValue(i);
            } else if (NODE_START.equalsIgnoreCase(attName)) {
                startDate = reader.getAttributeValue(i);
            } else if (NODE_END.equalsIgnoreCase(attName)) {
                endDate = reader.getAttributeValue(i);
            }
        }

        if (id.isEmpty()) {
            report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_nodeid"), Issue.Level.SEVERE));
            return;
        }

        NodeDraft node = container.factory().newNodeDraft();
        node.setId(id);

        node.setLabel(label);

        container.addNode(node);

        boolean end = false;
        while (reader.hasNext() && !end) {
            int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    if (ATTVALUE.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readNodeAttValue(reader, node);
                    } else if (NODE_POSITION.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readNodePosition(reader, node);
                    } else if (NODE_COLOR.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readNodeColor(reader, node);
                    } else if (NODE_SIZE.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readNodeSize(reader, node);
                    }
                    break;

                case XMLStreamReader.END_ELEMENT:
                    if (NODE.equalsIgnoreCase(xmlReader.getLocalName())) {
                        end = true;
                    }
                    break;
            }
        }
    }

    private void readNodeAttValue(XMLStreamReader reader, NodeDraft node) {
        String fore = "";
        String value = "";
        String startDate = "";
        String endDate = "";

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attName = reader.getAttributeName(i).getLocalPart();
            if ("for".equalsIgnoreCase(attName) || "id".equalsIgnoreCase(attName)) {
                fore = reader.getAttributeValue(i);
            } else if ("value".equalsIgnoreCase(attName)) {
                value = reader.getAttributeValue(i);
            } else if ("start".equalsIgnoreCase(attName)) {
                startDate = reader.getAttributeValue(i);
            } else if ("end".equalsIgnoreCase(attName)) {
                endDate = reader.getAttributeValue(i);
            }
        }

        if (fore.isEmpty()) {
            report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_datakey", node), Issue.Level.SEVERE));
            return;
        }

        if (!value.isEmpty()) {
            //Data attribute value
            AttributeColumn column = container.getAttributeModel().getEdgeTable().getColumn(fore);
            if (column != null) {
                try {
                    Object val = column.getType().parse(value);
                    node.addAttributeValue(column, val);
                } catch (Exception e) {
                    report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_datavalue", fore, node, column.getTitle()), Issue.Level.SEVERE));
                }
            }
        }
    }

    private void readNodeColor(XMLStreamReader reader, NodeDraft node) throws Exception {
        String rStr = "";
        String gStr = "";
        String bStr = "";

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attName = reader.getAttributeName(i).getLocalPart();
            if ("r".equalsIgnoreCase(attName)) {
                rStr = reader.getAttributeValue(i);
            } else if ("g".equalsIgnoreCase(attName)) {
                gStr = reader.getAttributeValue(i);
            } else if ("b".equalsIgnoreCase(attName)) {
                bStr = reader.getAttributeValue(i);
            }
        }

        int r = (rStr.isEmpty()) ? 0 : Integer.parseInt(rStr);
        int g = (gStr.isEmpty()) ? 0 : Integer.parseInt(gStr);
        int b = (bStr.isEmpty()) ? 0 : Integer.parseInt(bStr);

        node.setColor(new Color(r, g, b));
    }

    private void readNodePosition(XMLStreamReader reader, NodeDraft node) throws Exception {
        String xStr = "";
        String yStr = "";
        String zStr = "";

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attName = reader.getAttributeName(i).getLocalPart();
            if ("x".equalsIgnoreCase(attName)) {
                xStr = reader.getAttributeValue(i);
            } else if ("y".equalsIgnoreCase(attName)) {
                yStr = reader.getAttributeValue(i);
            } else if ("z".equalsIgnoreCase(attName)) {
                zStr = reader.getAttributeValue(i);
            }
        }

        if (!xStr.isEmpty()) {
            try {
                float x = Float.parseFloat(xStr);
                node.setX(x);
            } catch (NumberFormatException e) {
                report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_nodeposition", node, "X"), Issue.Level.WARNING));
            }
        }
        if (!yStr.isEmpty()) {
            try {
                float y = Float.parseFloat(yStr);
                node.setY(y);
            } catch (NumberFormatException e) {
                report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_nodeposition", node, "Y"), Issue.Level.WARNING));
            }
        }
        if (!zStr.isEmpty()) {
            try {
                float z = Float.parseFloat(zStr);
                node.setZ(z);
            } catch (NumberFormatException e) {
                report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_nodeposition", node, "Z"), Issue.Level.WARNING));
            }
        }
    }

    private void readNodeSize(XMLStreamReader reader, NodeDraft node) throws Exception {
        String attName = reader.getAttributeName(0).getLocalPart();
        if ("size".equalsIgnoreCase(attName)) {
            String sizeStr = reader.getAttributeValue(0);
            if (!sizeStr.isEmpty()) {
                try {
                    float size = Float.parseFloat(sizeStr);
                    node.setSize(size);
                } catch (NumberFormatException e) {
                    report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_nodesize", node), Issue.Level.WARNING));
                }
            }
        }
    }

    private void readEdge(XMLStreamReader reader) throws Exception {
        String id = "";
        String label = "";
        String source = "";
        String target = "";
        String weight = "";
        String edgeType = "";
        String startDate = "";
        String endDate = "";

        //Attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attName = reader.getAttributeName(i).getLocalPart();
            if (EDGE_SOURCE.equalsIgnoreCase(attName)) {
                source = reader.getAttributeValue(i);
            } else if (EDGE_TARGET.equalsIgnoreCase(attName)) {
                target = reader.getAttributeValue(i);
            } else if (EDGE_WEIGHT.equalsIgnoreCase(attName)) {
                weight = reader.getAttributeValue(i);
            } else if (EDGE_ID.equalsIgnoreCase(attName)) {
                id = reader.getAttributeValue(i);
            } else if (EDGE_TYPE.equalsIgnoreCase(attName)) {
                edgeType = reader.getAttributeValue(i);
            } else if (EDGE_LABEL.equalsIgnoreCase(attName)) {
                label = reader.getAttributeValue(i);
            } else if (EDGE_START.equalsIgnoreCase(attName)) {
                startDate = reader.getAttributeValue(i);
            } else if (EDGE_END.equalsIgnoreCase(attName)) {
                endDate = reader.getAttributeValue(i);
            }
        }

        EdgeDraft edge = container.factory().newEdgeDraft();

        NodeDraft nodeSource = container.getNode(source);
        NodeDraft nodeTarget = container.getNode(target);
        edge.setSource(nodeSource);
        edge.setTarget(nodeTarget);

        //Type
        if (!edgeType.isEmpty()) {
            if (edgeType.equalsIgnoreCase("undirected")) {
                edge.setType(EdgeDraft.EdgeType.UNDIRECTED);
            } else if (edgeType.equalsIgnoreCase("directed")) {
                edge.setType(EdgeDraft.EdgeType.DIRECTED);
            } else if (edgeType.equalsIgnoreCase("mutual")) {
                edge.setType(EdgeDraft.EdgeType.MUTUAL);
            } else {
                report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_edgetype", edgeType, edge), Issue.Level.SEVERE));
            }
        }

        //Id
        if (!id.isEmpty()) {
            edge.setId(id);
        }

        //Weight
        if (!weight.isEmpty()) {
            try {
                float weightNumber = Float.parseFloat(weight);
                edge.setWeight(weightNumber);
            } catch (NumberFormatException e) {
                report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_edgeweight", edge), Issue.Level.WARNING));
            }
        }

        //Label
        if (!label.isEmpty()) {
            edge.setLabel(label);
        }

        //Dynamic
        if (startDate != null || endDate != null) {
            edge.addTimeSlice(startDate, endDate);
        }

        container.addEdge(edge);

        boolean end = false;
        while (reader.hasNext() && !end) {
            int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    if (ATTVALUE.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readEdgeAttValue(reader, edge);
                    } else if (EDGE_COLOR.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readEdgeColor(reader, edge);
                    }
                    break;

                case XMLStreamReader.END_ELEMENT:
                    if (EDGE.equalsIgnoreCase(xmlReader.getLocalName())) {
                        end = true;
                    }
                    break;
            }
        }
    }

    private void readEdgeAttValue(XMLStreamReader reader, EdgeDraft edge) {
        String fore = "";
        String value = "";
        String startDate = "";
        String endDate = "";

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attName = reader.getAttributeName(i).getLocalPart();
            if ("for".equalsIgnoreCase(attName) || "id".equalsIgnoreCase(attName)) {
                fore = reader.getAttributeValue(i);
            } else if ("value".equalsIgnoreCase(attName)) {
                value = reader.getAttributeValue(i);
            } else if ("start".equalsIgnoreCase(attName)) {
                startDate = reader.getAttributeValue(i);
            } else if ("end".equalsIgnoreCase(attName)) {
                endDate = reader.getAttributeValue(i);
            }
        }

        if (fore.isEmpty()) {
            report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_datakey", edge), Issue.Level.SEVERE));
            return;
        }

        if (!value.isEmpty()) {
            //Data attribute value
            AttributeColumn column = container.getAttributeModel().getEdgeTable().getColumn(fore);
            if (column != null) {
                try {
                    Object val = column.getType().parse(value);
                    edge.addAttributeValue(column, val);
                } catch (Exception e) {
                    report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_datavalue", fore, edge, column.getTitle()), Issue.Level.SEVERE));
                }
            }
        }
    }

    private void readEdgeColor(XMLStreamReader reader, EdgeDraft edge) throws Exception {
        String rStr = "";
        String gStr = "";
        String bStr = "";

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attName = reader.getAttributeName(i).getLocalPart();
            if ("r".equalsIgnoreCase(attName)) {
                rStr = reader.getAttributeValue(i);
            } else if ("g".equalsIgnoreCase(attName)) {
                gStr = reader.getAttributeValue(i);
            } else if ("b".equalsIgnoreCase(attName)) {
                bStr = reader.getAttributeValue(i);
            }
        }

        int r = (rStr.isEmpty()) ? 0 : Integer.parseInt(rStr);
        int g = (gStr.isEmpty()) ? 0 : Integer.parseInt(gStr);
        int b = (bStr.isEmpty()) ? 0 : Integer.parseInt(bStr);

        edge.setColor(new Color(r, g, b));
    }

    private void readAttributes(XMLStreamReader reader) throws Exception {
        String classAtt = "";
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attName = reader.getAttributeName(i).getLocalPart();
            if ("class".equalsIgnoreCase(attName)) {
                classAtt = reader.getAttributeValue(i);
            }
        }

        boolean end = false;
        while (reader.hasNext() && !end) {
            int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    if (ATTRIBUTE.equalsIgnoreCase(xmlReader.getLocalName())) {
                        readAttribute(reader, classAtt);
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (ATTRIBUTES.equalsIgnoreCase(xmlReader.getLocalName())) {
                        end = true;
                    }
                    break;
            }
        }
    }

    private void readAttribute(XMLStreamReader reader, String classAtt) throws Exception {
        String id = "";
        String type = "";
        String title = "";
        String defaultStr = "";
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attName = reader.getAttributeName(i).getLocalPart();
            if (ATTRIBUTE_ID.equalsIgnoreCase(attName)) {
                id = reader.getAttributeValue(i);
            } else if (ATTRIBUTE_TYPE.equalsIgnoreCase(attName)) {
                type = reader.getAttributeValue(i);
            } else if (ATTRIBUTE_TITLE.equalsIgnoreCase(attName)) {
                title = reader.getAttributeValue(i);
            }
        }

        if (title.isEmpty()) {
            title = id;
        }


        if (!id.isEmpty() && !type.isEmpty()) {
            //Class type
            if (classAtt.isEmpty() || !(classAtt.equals("node") || classAtt.equals("edge"))) {
                report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_attributeclass", title), Issue.Level.SEVERE));
                return;
            }

            //Default?
            boolean end = false;
            boolean defaultFlag = false;
            while (reader.hasNext() && !end) {
                int xmltype = reader.next();

                switch (xmltype) {
                    case XMLStreamReader.START_ELEMENT:
                        if (ATTRIBUTE_DEFAULT.equalsIgnoreCase(xmlReader.getLocalName())) {
                            defaultFlag = true;
                        }
                        break;
                    case XMLStreamReader.CHARACTERS:
                        if (defaultFlag && !xmlReader.isWhiteSpace()) {
                            defaultStr = xmlReader.getText();
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        if (ATTRIBUTE.equalsIgnoreCase(xmlReader.getLocalName())) {
                            end = true;
                        }
                        break;
                }
            }

            //Type
            AttributeType attributeType = AttributeType.STRING;
            if (type.equals("boolean")) {
                attributeType = AttributeType.BOOLEAN;
            } else if (type.equals("integer")) {
                attributeType = AttributeType.INT;
            } else if (type.equals("long")) {
                attributeType = AttributeType.LONG;
            } else if (type.equals("float")) {
                attributeType = AttributeType.FLOAT;
            } else if (type.equals("double")) {
                attributeType = AttributeType.DOUBLE;
            } else if (type.equals("string")) {
                attributeType = AttributeType.STRING;
            } else {
                return;
            }

            //Default Object
            Object defaultValue = null;
            if (!defaultStr.isEmpty()) {
                try {
                    defaultValue = attributeType.parse(defaultStr);
                    report.log(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_log_default", defaultStr, title));
                } catch (Exception e) {
                    report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_attributedefault", title, attributeType.getTypeString()), Issue.Level.SEVERE));
                }
            }

            //Add to model
            if ("node".equalsIgnoreCase(classAtt)) {
                container.getAttributeModel().getNodeTable().addColumn(id, title, attributeType, AttributeOrigin.DATA, defaultValue);
                report.log(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_log_nodeattribute", title, attributeType.getTypeString()));
            } else if ("edge".equalsIgnoreCase(classAtt)) {
                container.getAttributeModel().getEdgeTable().addColumn(id, title, attributeType, AttributeOrigin.DATA, defaultValue);
                report.log(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_log_edgeattribute", title, attributeType.getTypeString()));
            }
        } else {
            report.logIssue(new Issue(NbBundle.getMessage(ImporterGEXF.class, "importerGEXF_error_attributeempty", title), Issue.Level.SEVERE));
        }
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

    public boolean cancel() {
        cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }
}
