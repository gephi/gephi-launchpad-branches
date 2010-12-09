package net.phreakocious.httpgraph;

import org.gephi.io.importer.api.ContainerLoader;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author phreakocious
 */
public class SnarfData {

    private final Logger log =
            LoggerFactory.getLogger(HttpGraph.class);
    private ContainerLoader container;

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
    private sdNode[] nodes;
    private sdEdge[] edges;

    public SnarfData(final HttpRequest request, String saddr) {
        uri = request.getUri();
        if (uri == null) {
            log.warn("Snarfailure!");
            return;
        }
        uri = uri.replaceFirst("^https?://", "");
        uri = uri.replaceFirst("\\?.*", "");

        host = request.getHeader("Host");
        if (host == null) {
            host = uri.replaceFirst("/.*", "");
        }
        domain = host.replaceFirst(".*\\.([^.]+\\.[^.]+)", "$1");

        referer = request.getHeader("Referer");
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

        method = request.getMethod().toString();
        bytes = request.toString().length();

        srcaddr = saddr.replaceFirst("[^\\d]+(\\d+\\.\\d+\\.\\d+\\.\\d+).*", "$1");
//        for (String s : request.getHeaderNames()) {
//            log.warn("header: {} {}", s, request.getHeader(s));
//        }
        
        clientnode = new sdNode("client", "client", srcaddr, true, 12f);
        urinode = new sdNode("uri", domain, uri, false, 2);
        hostnode = new sdNode("host", domain, host, true, 6f);
        domainnode = new sdNode("domain", domain, domain, true, 9f);
        referernode = new sdNode("uri", rdomain, referer, false, 2f);
        rhostnode = new sdNode("host", rdomain, rhost, true, 6f);
        rdomainnode = new sdNode("domain", rdomain, rdomain, true, 9f);

        nodes = new sdNode[]{
                    clientnode, urinode, hostnode, domainnode, referernode, rhostnode, rdomainnode
                };
        edges = new sdEdge[]{
                    new sdEdge(rdomainnode, rhostnode),
                    new sdEdge(rhostnode, referernode),
                    new sdEdge(referernode, urinode),
                    new sdEdge(clientnode, urinode),
                    new sdEdge(hostnode, urinode),
                    new sdEdge(domainnode, hostnode)
                };
        /**
         *            sdNode servernode = new sdNode("server", domain, dstaddr, false);
         */
    }

    public sdEdge[] getEdges() {
        return (edges);
    }

    public sdNode[] getNodes() {
        return (nodes);
    }

    public sdNode[] getNodes(sdEdge e) {
        sdEdge edge = e;
        return new sdNode[]{edge.node1, edge.node2};
    }

    public void setContainer(ContainerLoader c) {
        container = c;
    }

    public void graphUpdate() {
        dump();
        net.phreakocious.httpgraph.HttpGraph.graphupdate(this, container);
    }

    public void dump() {
        log.warn("vars: {} {}", "srcaddr", srcaddr);
        log.warn("vars: {} {}", "bytes", bytes);
        log.warn("vars: {} {}", "uri", uri);
        log.warn("vars: {} {}", "host", host);
        log.warn("vars: {} {}", "domain", domain);
        log.warn("vars: {} {}", "referer", referer);
        log.warn("vars: {} {}", "rhost", rhost);
        log.warn("vars: {} {}", "rdomain", rdomain);
    }
}