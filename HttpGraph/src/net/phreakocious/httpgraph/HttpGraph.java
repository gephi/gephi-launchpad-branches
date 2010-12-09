package net.phreakocious.httpgraph;

import java.awt.Color;
import java.util.HashMap;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.processor.plugin.AppendProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.utils.progress.ProgressTicket;
import org.littleshoot.proxy.HttpProxyServer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author phreakocious
 */
@ServiceProvider(service = Generator.class)
public class HttpGraph implements Generator {

    private static Workspace workspace;
    private static Container container;
    private static AttributeColumn type;
    private static AttributeColumn domain;

    private static ImportController importController;
    private static final Logger log =
            LoggerFactory.getLogger(HttpGraph.class);
    protected ProgressTicket progress;
    protected boolean cancel = false;
    protected int proxyport = 8088;
    private static int colorcount = 0;
    private static Color[] colors;
    private static HashMap colormap = new HashMap();

    public static void graphupdate(SnarfData data, ContainerLoader cldr) {
        //ContainerLoader cldr = container.getLoader();
        //data.dump();

        NodeDraft nd;
        NodeDraft nd1;
        NodeDraft nd2;

        for (SnarfData.sdNode n : data.getNodes()) {
            String nl = n.label;
            if (!cldr.nodeExists(nl)) {
                // log.warn("node doesn't exist!  {}", nl);
                nd = cldr.factory().newNodeDraft();

                if (! colormap.containsKey(n.domain)) {
                    colormap.put(n.domain, colors[colorcount]);
                    colorcount++;
                }

                nd.setId(nl);
                cldr.addNode(nd);
                nd.setColor((Color) colormap.get(n.domain));
                nd.addAttributeValue(type, n.type);
                nd.addAttributeValue(domain, n.domain);

                nd.setLabel(nl);
                nd.setLabelVisible(n.labelvisible);
                nd.setSize(n.size);

            }
        }

        for (SnarfData.sdEdge e : data.getEdges()) {
            SnarfData.sdNode n1 = e.node1;
            SnarfData.sdNode n2 = e.node2;
            String n1l = n1.label;
            String n2l = n2.label;
            String el = n1l + "--" + n2l;
            nd1 = cldr.getNode(n1l);
            nd2 = cldr.getNode(n2l);

            if (!cldr.edgeExists(el)) {
                EdgeDraft ed = cldr.factory().newEdgeDraft();
                ed.setSource(nd1);
                ed.setTarget(nd2);
                ed.setId(el);
                cldr.addEdge(ed);
            }
        }

        //log.warn("graphupdate!  {}", cldr.toString());
        importController.process(container, new AppendProcessor(), workspace);
    }

    @Override
    public void generate(ContainerLoader cldr) {
        cldr = null;
        colors = generateColors(36);
        importController = Lookup.getDefault().lookup(ImportController.class);
        container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
        container.setAutoScale(false);
        container.setAllowParallelEdge(true);
        container.setSource("Snarfing your world wide web!");

        cldr = container.getLoader();
        AttributeModel attributemodel = cldr.getAttributeModel();
        AttributeTable nodeTable = attributemodel.getNodeTable();

        type  = nodeTable.addColumn("type", AttributeType.STRING, AttributeOrigin.DATA);
        domain = nodeTable.addColumn("domain", AttributeType.STRING,AttributeOrigin.DATA);

        //Get Current workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        workspace = pc.getCurrentWorkspace();

        
        final HttpProxyServer server = new HttpGraphHttpProxyServer(proxyport);
        ((HttpGraphHttpProxyServer) server).setContainer(cldr);
        server.start();
        
        progress.start();
    }

    public Color[] generateColors(int n) {
        Color[] cols = new Color[n];
        for (int i = 0; i < n; i++) {
            cols[i] = Color.getHSBColor((float) i / (float) n, 0.85f, 1.0f);
        }
        return cols;
    }

    @Override
    public String getName() {
        return "HTTP Graph";
    }

    @Override
    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(HttpGraphUI.class);
    }

    public int getProxyPort() {
        return proxyport;
    }

    public void setProxyPort(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("TCP ports must be between 1 and 65535.  Preferably higher than 1024.");
        }
        this.proxyport = port;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }
}
