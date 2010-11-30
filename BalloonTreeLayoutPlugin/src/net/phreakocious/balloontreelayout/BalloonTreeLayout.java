package net.phreakocious.balloontreelayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.api.NodeIterator;
import org.gephi.layout.plugin.AbstractLayout;
// import org.gephi.layout.plugin.tree.GraphUtils;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.NbBundle;

/**
 * @author phreakocious
 * (Adapted from the prefuse library, original copyright and quote below)


<p>Copyright (c) 2004-2006 Regents of the University of California.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
notice and this list of conditions.

3. The name of the University may not be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.</p>

 * <p>Layout that computes a circular "balloon-tree" layout of a tree.
 * This layout places children nodes radially around their parents, and is
 * equivalent to a top-down flattened view of a ConeTree.</p>
 *
 * <p>The algorithm used is that of G. Melançon and I. Herman from their
 * research paper Circular Drawings of Rooted Trees, Reports of the Centre for
 * Mathematics and Computer Sciences, Report Number INS–9817, 1998.</p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class BalloonTreeLayout extends AbstractLayout {

    private int nodeId;
    private DirectedGraph graph;
    private int minRadius;

    public BalloonTreeLayout(LayoutBuilder layoutBuilder) {
        super(layoutBuilder);
    }

    public void initAlgo() {
        graph = graphModel.getHierarchicalDirectedGraphVisible();
        setConverged(false);
    }

    private void layout(Node n, double x, double y) {
        firstWalk(n);
        secondWalk(n, 0, 0, 1, 0);
    }

    private void firstWalk(Node n) {
        NodeData nnd = n.getNodeData();
        BalloonTreeNodeLayoutData nld = nnd.getLayoutData();
        nld.d = 0;
        Node[] children = graph.getSuccessors(n).toArray();

        for (Node c : children) {
            NodeData cnd = c.getNodeData();
            BalloonTreeNodeLayoutData cld = cnd.getLayoutData();
            firstWalk(c);            
            nld.d = Math.max(nld.d, cld.r);
            cld.a = (Math.atan(cld.r) / (nld.d + cld.r));
            System.out.print("fw: ");
            System.out.print("n=" + n.getId());
            System.out.print(" c=" + c.getId() + " -");
            System.out.print("  d = " + nld.d);
            System.out.print("  a = " + cld.a);
            System.out.println();
        }
        adjustChildren(n);
        setRadius(n);
    }

    private void adjustChildren(Node n) {
        double s = 0;
        NodeData nnd = n.getNodeData();
        BalloonTreeNodeLayoutData nld = nnd.getLayoutData();
        Node[] children = graph.getSuccessors(n).toArray();
        for (Node c : children) {
            NodeData cnd = c.getNodeData();
            BalloonTreeNodeLayoutData cld = cnd.getLayoutData();
            s += cld.a;
        }

        if (s > Math.PI) {
            nld.c = Math.PI / s;
            nld.f = 0;
        } else {
            nld.c = 1;
            nld.f = Math.PI - s;
        }
    }

    private void setRadius(Node n) {
        NodeData nnd = n.getNodeData();
        BalloonTreeNodeLayoutData nld = nnd.getLayoutData();
        double bx = 0.0, by = 0.0;

        Node[] children = graph.getSuccessors(n).toArray();

        // Compute barycenter of the layout circle containing all children
        for (Node c : children) {
            bx += c.getNodeData().x();
            by += c.getNodeData().y();
        }
        bx /= children.length;
        by /= children.length;

        double maxVal = 0;
        // Compute radius for the layout circle as the largest required to contain all children
        for (Node c : children) {
            NodeData cnd = c.getNodeData();
            BalloonTreeNodeLayoutData cld = cnd.getLayoutData();
            double cx = cnd.x();
            double cy = cnd.y();

            // Computing distance to barycenter -- may be wrong?

            double dist = Math.sqrt(Math.pow(cx - bx, 2) + Math.pow(cy - by, 2)) + cld.r;

            // System.out.println("dist = " + dist);

            if (dist > maxVal) {
                maxVal = dist;
            }
        }

        nld.rx = bx * -1;
        nld.ry = by * -1;
        
        //nld.r = Math.max((int) maxVal, minRadius) + 2 * nld.d;
        nld.r = (int) maxVal;
    }

    private void secondWalk(Node n,
            double bx, double by, double l, double t) {
        NodeData nnd = n.getNodeData();
        BalloonTreeNodeLayoutData nld = nnd.getLayoutData();

        // Store x and y values for node
        float oldx = nnd.x();
        float oldy = nnd.y();
        System.out.print("sw: ");
        System.out.print("n=" + n.getId() + " - ");
        System.out.print(" bx = " + bx);
        System.out.print(" by = " + by);
        System.out.print("  l = " + l);
        System.out.print("  t = " + t);
        System.out.print(" rx = " + nld.rx);
        System.out.print(" ry = " + nld.ry);
        System.out.println();


        float newx = (float) (bx + l * (nld.rx * Math.cos(t) - nld.ry * Math.sin(t)));
        float newy = (float) (by + l * (nld.rx * Math.sin(t) + nld.ry * Math.cos(t)));

        System.out.println("float  oldx = " + oldx + "  oldy = " + oldy);
        System.out.println("float  newx = " + newx + "  newy = " + newy);

        nnd.setX(newx);
        nnd.setY(newy);

        int numChildren = 0;
        Node[] children = graph.getSuccessors(n).toArray();
        numChildren = children.length;

        double dd = l * nld.d;
        double p = Math.PI;
        double fs = (numChildren == 0 ? 0 : nld.f / numChildren + 1);
        double pr = 0;

        for (Node c : children) {
            NodeData cnd = c.getNodeData();
            BalloonTreeNodeLayoutData cld = cnd.getLayoutData();
            double aa = nld.c * cld.a;
            double rr = nld.d * Math.tan(aa) / (1 - Math.tan(aa));
            p += pr + cld.a + fs;

            double xx = (l * rr + dd) * Math.cos(p);
            double yy = (l * rr + dd) * Math.sin(p);

            xx += nld.rx;
            yy += nld.ry;


            // Matrix multiplication -- may be wrong?

            double cosT=Math.cos(t);
            double sinT=Math.sin(t);

            xx = (cosT * xx) + ((sinT * -1) * yy);
            yy = (sinT * xx) + (cosT * yy);
               
           cnd.setX((float) xx);
           cnd.setY((float) yy);
          
            pr = cld.a;
            secondWalk(c, bx + xx, by + yy, l * rr/cld.r, p);
        }
    }

    public void goAlgo() {
        graph = graphModel.getHierarchicalDirectedGraphVisible();
        Node rootNode = GraphUtils.determineRootNode(graph);
        Node[] nodes = graph.getNodes().toArray();

        for (Node n : nodes) {
            NodeData nd = n.getNodeData();
            if (nd == null || !(nd.getLayoutData() instanceof BalloonTreeNodeLayoutData)) {
                nd.setLayoutData(new BalloonTreeNodeLayoutData());
            }
        }
        layout(rootNode, 0, 0);
        setConverged(true);
    }

    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        final String BALLOON_TREE = "Balloon Tree";

        try {
            properties.add(LayoutProperty.createProperty(
                    this, int.class,
                    NbBundle.getMessage(BalloonTreeLayout.class, "balloonTree.startId.name"),
                    BALLOON_TREE,
                    NbBundle.getMessage(BalloonTreeLayout.class, "balloonTree.startId.desc"),
                    "getNodeId", "setNodeId"));
            properties.add(LayoutProperty.createProperty(
                    this, int.class,
                    NbBundle.getMessage(BalloonTreeLayout.class, "balloonTree.minRadius.name"),
                    BALLOON_TREE,
                    NbBundle.getMessage(BalloonTreeLayout.class, "balloonTree.minRadius.desc"),
                    "getMinRadius", "setMinRadius"));

        } catch (Exception e) {
            throw new AssertionError(e);
        }

        return properties.toArray(new LayoutProperty[0]);
    }

    public void resetPropertiesValues() {
        setMinRadius(4);
        setNodeId(0);
    }

    public void endAlgo() {
        for (Node n : graph.getNodes()) {
            n.getNodeData().setLayoutData(null);
        }
    }

//<editor-fold defaultstate="collapsed" desc="getters and setters">
    public int getNodeId() {
        return nodeId;
    }

    public int getMinRadius() {
        return minRadius;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void setMinRadius(int xMinRadius) {
        minRadius = xMinRadius;

    }//</editor-fold>
}