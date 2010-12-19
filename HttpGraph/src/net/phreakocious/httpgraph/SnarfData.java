package net.phreakocious.httpgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phreakocious
 */
public class SnarfData {

    private static final Logger log = Logger.getLogger(SnarfData.class.getName());
    private static HashMap<String, String> attribtypes = new HashMap();

    public class SDNode {

        final String type, domain, label;
        final float size;
        boolean labelvisible;
        HashMap<String, Object> attributes;

        public SDNode(String t, String d, String l, boolean lv, float sz) {
            type = t;
            label = l;
            domain = d;
            labelvisible = lv;
            size = sz;
            attributes = new HashMap();
            setAttrib("type", type);
            setAttrib("domain", domain);
        }

        public final void setAttrib(String name, Object value) {
            String attribclass = value.getClass().getName();
            String result;

            result = attribtypes.put(name, attribclass);

            if (result != null && !result.equals(attribclass)) {
                log.log(Level.WARNING, "Redefined attribute type: {} is now {} !!", new String[]{result, attribclass});
            }
            attributes.put(name, value);
        }
    }

    public class SDEdge {

        SDNode src, dst;

        public SDEdge(SDNode source, SDNode destination) {
            src = source;
            dst = destination;
        }
    }
    private String clientaddr, domain, uri;
    private int bytes = 0;
    private HashMap<String, SDNode> nodes = new HashMap<String, SDNode>();
    private ArrayList<SDEdge> edges = new ArrayList<SDEdge>();

    public SnarfData(String xsrcaddr, String xuri, String xmethod, String xhost, String xreferer) {
        setClient(xsrcaddr);
        setHost(xhost);
        setUri(xuri);
        setReferer(xreferer);
        String method = xmethod;

        for (String n : new String[]{"referer", "rhost", "rdomain", "host", "domain", "client"}) {
            //nodes.get(n).attributes.put("bytes", xx);
            nodes.get(n).attributes.put("content-type", "");
        }

        addEdge("rdomain", "rhost");
        addEdge("rhost", "referer");
        addEdge("referer", "uri");
        addEdge("client", "uri");
        addEdge("host", "uri");
        addEdge("domain", "host");
        //dump();
    }

    private void setClient(String rawaddr) {
        clientaddr = rawaddr.replaceFirst("[^\\d]+(\\d+\\.\\d+\\.\\d+\\.\\d+).*", "$1");
        nodes.put("client", new SDNode("client", "local", clientaddr, true, 12f));
    }

    private void setUri(String rawuri) {
        if (rawuri == null || rawuri.isEmpty()) {
            log.log(Level.SEVERE, "Snarfailure!");
            return;
        }
        uri = parseUri(rawuri);
        nodes.put("uri", new SDNode("uri", domain, uri, false, 2));
    }

    private void setReferer(String referer) {
        String rdomain, rhost;
        if (referer == null || referer.isEmpty()) {
            referer = clientaddr;
            rhost = clientaddr;
            rdomain = "local";
        } else {
            referer = parseUri(referer);
            rhost = referer.replaceFirst(":.*$", "");
            rdomain = parseDomain(rhost);
        }

        nodes.put("referer", new SDNode("uri", rdomain, referer, false, 2f));
        nodes.put("rhost", new SDNode("host", rdomain, rhost, true, 6f));
        nodes.put("rdomain", new SDNode("domain", rdomain, rdomain, true, 9f));

    }

    private String parseUri(String uri) {
        uri = uri.replaceFirst("^https?://", "");
        uri = uri.replaceFirst("\\?.*", "");
        return uri;
    }

    private void setHost(String rawhost) {
        String host = rawhost;
        host = host.replaceFirst(":.*$", "");
        domain = parseDomain(host);

        nodes.put("host", new SDNode("host", domain, host, true, 6f));
        nodes.put("domain", new SDNode("domain", domain, domain, true, 9f));
    }

    private String parseDomain(String domain) {
        domain = domain.replaceFirst("/.*", "");

        if (domain.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
            //
        } else if (domain.matches(".*\\.[^.]{2}\\.[^.]{2}$")) {
            domain = domain.replaceFirst(".*\\.([^.]+\\.[^.]{2}\\.[^.]{2})$", "$1");

        } else {
            domain = domain.replaceFirst(".*\\.([^.]+\\.[^.]+)$", "$1");
        }

        // Eliminates naming conflicts between host and
        // domain for those with no high level qualifier ;)

        domain = domain.concat(".");

        if (domain.equals(".")) {
            domain = "local";
        }
        return domain;
    }

    public void setBytes(int thebytes) {
        bytes = thebytes;
    }

    public SDNode getNode(String nodetype) {
        return (nodes.get(nodetype));
    }

    public SDNode[] getNodes() {
        return nodes.values().toArray(new SDNode[nodes.size()]);
    }

    public HashMap<String, Object> getAttributes(SDNode node) {
        return node.attributes;
    }

    public HashMap<String, String> getNodeAttributeList() {
        return attribtypes;
    }

    public SDNode[] getEdgeNodes(SDEdge e) {
        SDEdge edge = e;
        return new SDNode[]{edge.src, edge.dst};
    }

    private boolean addEdge(String n1, String n2) {
        SDNode src = nodes.get(n1);
        SDNode dst = nodes.get(n2);
        if (src.label == null || dst.label == null) {
            log.log(Level.WARNING, "something is null!  src.label = {}  dst.label = {}", new String[]{src.label, dst.label});
            return false;
        }
        if (src.label.equals(dst.label)) {
            log.log(Level.WARNING, "labels are equal!  label = {}  src.type = {}  dst.type = {}", new String[]{src.label, src.type, dst.type});
            return false;
        }
        edges.add(new SDEdge(src, dst));
        return true;
    }

    public SDEdge[] getEdges() {
        return edges.toArray(new SDEdge[edges.size()]);
    }

    public void nullCheck() {
        for (SDNode n : getNodes()) {
            for (String attrib : n.attributes.keySet()) {
                String value = (String) n.attributes.get(attrib).toString();
                if (value == null) {
                    log.log(Level.WARNING, "node {0} attrib {0} is null!", new String[]{n.label, attrib});
                }
            }
        }
    }

    public void graphUpdate() {
        //   dump();
        net.phreakocious.httpgraph.HttpGraph.INSTANCE.graphupdate(this);
    }

    private void dump() {
        log.log(Level.INFO, "vars:  srcaddr {0}", clientaddr);
        log.log(Level.INFO, "vars:  bytes {0}", bytes);
        log.log(Level.INFO, "vars:  uri {0}", nodes.get("uri").label);
        log.log(Level.INFO, "vars:  host {0}", nodes.get("host").label);
        log.log(Level.INFO, "vars:  domain {0}", nodes.get("domain").label);
        log.log(Level.INFO, "vars:  referer {0}", nodes.get("referer").label);
        log.log(Level.INFO, "vars:  rhost {0}", nodes.get("rhost").label);
        log.log(Level.INFO, "vars:  rdomain {0}", nodes.get("rdomain").label);
    }
}
