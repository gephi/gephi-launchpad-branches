package net.phreakocious.httpgraph;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author phreakocious
 */
public class SnarfData {

    private static final Logger log = Logger.getLogger(SnarfData.class.getName());

    public class sdNode {

        String type;
        String domain;
        String label;
        boolean labelvisible;
        float size;

        public sdNode(String t, String d, String l, boolean lv, float sz) {
            type = t;
            domain = d;
            label = l;
            labelvisible = lv;
            size = sz;
        }
    }

    public class sdEdge {

        sdNode node1;
        sdNode node2;

        public sdEdge(sdNode n1, sdNode n2) {
            node1 = n1;
            node2 = n2;
        }
    }
    private String srcaddr = "";
    private String method = "";
    private String uri = "";
    private String host = "";
    private String referer = "";
    private int bytes = 0;
    private String domain = "";
    private String rdomain = "";
    private String rhost = "";
    private sdNode hostnode;
    private sdNode clientnode;
    private sdNode urinode;
    private sdNode servernode;
    private sdNode domainnode;
    private sdNode referernode;
    private sdNode rhostnode;
    private sdNode rdomainnode;
    ;

    public SnarfData() {
    }

    public String setSrcaddr(String rawaddr) {
        srcaddr = rawaddr.replaceFirst("[^\\d]+(\\d+\\.\\d+\\.\\d+\\.\\d+).*", "$1");
        clientnode = new sdNode("client", "client", srcaddr, true, 12f);
        return srcaddr;
    }

    public String setUri(String rawuri) {
        uri = rawuri;
        if (uri == null) {
            log.log(Level.WARNING, "Snarfailure!");
            return null;
        }

        uri = uri.replaceFirst("^https?://", "");
        uri = uri.replaceFirst("\\?.*", "");
        urinode = new sdNode("uri", domain, uri, false, 2);

        return uri;
    }

    public String setHost(String rawhost) {
        host = rawhost;
        if (host == null || host.isEmpty()) {
            if (!uri.isEmpty()) {
                host = uri.replaceFirst("/.*", "");
            }
        }
        hostnode = new sdNode("host", domain, host, true, 6f);
        domain = host.replaceFirst(".*\\.([^.]+\\.[^.]+)$", "$1");
        domainnode = new sdNode("domain", domain, domain, true, 9f);
        return host;
    }

    public String setReferer(String rawreferer) {
        referer = rawreferer;
        if (referer == null) {
            referer = "client";
            rhost = "client";
            rdomain = "client";
        } else {
            referer = referer.replaceFirst("^https?://", "");
            referer = referer.replaceFirst("\\?.*", "");
            rhost = referer.replaceFirst("/.*", "");
            rdomain = rhost.replaceFirst(".*\\.([^.]+\\.[^.]+)", "$1");
        }

        referernode = new sdNode("uri", rdomain, referer, false, 2f);
        rhostnode = new sdNode("host", rdomain, rhost, true, 6f);
        rdomainnode = new sdNode("domain", rdomain, rdomain, true, 9f);
        return referer;
    }

    public int setBytes(int thebytes) {
        bytes = thebytes;
        return bytes;
    }

    public sdNode[] getNodes() {
        sdNode[] nodes = {clientnode, urinode, hostnode, domainnode, referernode, rhostnode, rdomainnode};
        return (nodes);
    }

    public sdNode[] getEdgeNodes(sdEdge e) {
        sdEdge edge = e;
        return new sdNode[]{edge.node1, edge.node2};
    }

    public sdEdge[] getEdges() {
        return new sdEdge[]{
                    new sdEdge(rdomainnode, rhostnode),
                    new sdEdge(rhostnode, referernode),
                    new sdEdge(referernode, urinode),
                    new sdEdge(clientnode, urinode),
                    new sdEdge(hostnode, urinode),
                    new sdEdge(domainnode, hostnode)
                };

    }

    public void graphUpdate() {
     //   dump();
        net.phreakocious.httpgraph.HttpGraph.graphupdate(this);
    }

    public void dump() {
        log.log(Level.INFO, "vars:  srcaddr {0}", srcaddr);
        log.log(Level.INFO, "vars:  bytes {0}", bytes);
        log.log(Level.INFO, "vars:  uri {0}", uri);
        log.log(Level.INFO, "vars:  host {0}", host);
        log.log(Level.INFO, "vars:  domain {0}", domain);
        log.log(Level.INFO, "vars:  referer {0}", referer);
        log.log(Level.INFO, "vars:  rhost {0}", rhost);
        log.log(Level.INFO, "vars:  rdomain {0}", rdomain);
    }
}