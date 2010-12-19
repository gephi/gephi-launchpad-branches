package net.phreakocious.httpgraph;

import com.predic8.membrane.core.Configuration;
import com.predic8.membrane.core.HttpRouter;
import com.predic8.membrane.core.rules.ProxyRule;
import com.predic8.membrane.core.rules.ProxyRuleKey;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.phreakocious.httpgraph.SnarfData.SDEdge;
import net.phreakocious.httpgraph.SnarfData.SDNode;
import org.gephi.data.attributes.api.AttributeColumn;
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
import org.gephi.layout.api.LayoutController;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author phreakocious
 */
@ServiceProvider(service = Generator.class)
public class HttpGraph implements Generator {

    public static HttpGraph INSTANCE;

    private static final Logger log;
    private static final Workspace workspace;
    private static final ImportController importController;
    private static final HttpRouter router;
    private static final int colordiv = 32;
    private static ArrayList<Color> colors;
    private static HashMap<String, Color> colormap;
    private static HashMap<String, AttributeType> attribmap;
    protected ProgressTicket progress;
    protected boolean cancel = false;
    protected int proxyport = 8088;
    protected String chainproxy = "";
    protected int chainproxyport = 0;

    static {
        INSTANCE = new HttpGraph();
        log = Logger.getLogger(HttpGraph.class.getName());
        workspace = Lookup.getDefault().lookup(ProjectController.class).getCurrentWorkspace();
        importController = Lookup.getDefault().lookup(ImportController.class);
        router = new HttpRouter();
        colormap = new HashMap<String, Color>();
        attribmap = new HashMap<String, AttributeType>();

        setAttributeMap();
        generateColors(colordiv);
    }

    private ContainerLoader hgContainerLoader(Container container) {
        container.setAutoScale(false);
        container.setAllowParallelEdge(true);
        container.setAllowSelfLoop(true);
        container.setSource("Snarfing your world wide web!");
        ContainerLoader cldr = container.getLoader();
        return cldr;
    }

    public void graphupdate(SnarfData data) {
        Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
        ContainerLoader cldr = hgContainerLoader(container);

        AttributeTable nodeTable = cldr.getAttributeModel().getNodeTable();
        HashMap<String, AttributeColumn> colmap = new HashMap();
        HashMap<String,String> nodeattrmap = data.getNodeAttributeList();

        for (String attrib : nodeattrmap.keySet()) {
            AttributeType attrtype = attribmap.get(nodeattrmap.get(attrib));
            colmap.put(attrib, nodeTable.addColumn(attrib, attrtype, AttributeOrigin.DATA));
        }

        NodeDraft nd, nd1, nd2;

        for (SDNode n : data.getNodes()) {
            String nl = n.label;
            String domain = n.domain;

            if (!cldr.nodeExists(nl)) {
                //log.log(Level.INFO, "node doesn't exist!  {0}", nl);
                nd = cldr.factory().newNodeDraft();
                
                if ( ! colormap.containsKey(domain) && ! colors.isEmpty())
                        colormap.put(domain, colors.remove(0));

                nd.setId(nl);
                cldr.addNode(nd);
                nd.setColor(colormap.get(domain));

                for (String attrib : n.attributes.keySet()) {
                    Object value = n.attributes.get(attrib);
                    AttributeColumn col = colmap.get(attrib);
                    nd.addAttributeValue(col, value);
                }

                nd.setLabel(nl);
                nd.setLabelVisible(n.labelvisible);
                nd.setSize(n.size);
            }
        }

        for (SDEdge e : data.getEdges()) {
            SDNode n1 = e.src;
            SDNode n2 = e.dst;
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

        //log.log(Level.INFO, "graphupdate!  {0}", cldr.toString());

        importController.process(container, new AppendProcessor(), workspace);
    }

    @Override
    public void generate(ContainerLoader c) {

        c = null;
        Configuration config = new Configuration();

        try {
            if (! chainproxy.isEmpty()) {
                config.setProxyHost(chainproxy);
                config.setProxyPort(Integer.toString(chainproxyport));
                config.setUseProxy(true);
                router.getConfigurationManager().setConfiguration(config);
                router.getConfigurationManager().setSecuritySystemProperties();
            }
            
            router.getRuleManager().addRuleIfNew(new ProxyRule(new ProxyRuleKey(proxyport)));
            router.getTransport().getInterceptors().add(new HttpGraphProxyInterceptor());
            
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage());
        }

        LayoutBuilder lb = Lookup.getDefault().lookup(HGForceAtlas.class);
        LayoutController lc = Lookup.getDefault().lookup(LayoutController.class);
        lc.setLayout(lb.buildLayout());
        lc.executeLayout();
        progress.start();
    }

    private static void generateColors(int n) {
        colors = new ArrayList<Color>(colordiv);
        for (int i = 0; i < n; i++) {
            //colors.add(Color.getHSBColor((float) i / (float) n, 0.6f, 0.75f));
            colors.add(Color.getHSBColor((float) i / (float) n, 0.85f, 1f));
            //colors.add(Color.getHSBColor((float) i / (float) n, 0.65f, 0.8f));
        }
    }

    private static void setAttributeMap() {
        attribmap.put("java.lang.String", AttributeType.STRING);
        attribmap.put("java.lang.Integer", AttributeType.INT);
    }
// <editor-fold defaultstate="collapsed" desc="comment">
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

    public String getChainProxy() {
        return chainproxy;
    }

    public void setChainProxy(String proxy) {
        this.chainproxy = proxy;
    }

    public int getChainProxyPort() {
        return chainproxyport;
    }
    
    public void setChainProxyPort(int port) {
        this.chainproxyport = port;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }// </editor-fold>
}