package net.phreakocious.balloontreelayout;

import java.util.ArrayList;
import java.util.List;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.layout.plugin.AbstractLayout;
// import org.gephi.layout.plugin.tree.GraphUtils;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.NbBundle;

/**
 * @author phreakocious
 * (Adapted from the prefuse library, original copyright and quote below)
 */
/*
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
 */
/**
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
        System.out.println("fw: root node done.");
        secondWalk(n, x, y, 1, 0);
    }

    private void firstWalk(Node n) {
        NodeData nnd = n.getNodeData();
        BalloonTreeNodeLayoutData nld = nnd.getLayoutData();
        nld.d = 0;

        Node[] children = graph.getSuccessors(n).toArray();

        for (Node c : children) {
            firstWalk(c);
            NodeData cnd = c.getNodeData();
            BalloonTreeNodeLayoutData cld = cnd.getLayoutData();
            nld.d = Math.max(nld.d, cld.r);
            cld.a = Math.atan(cld.r) / (nld.d + cld.r);
            System.out.print("fw: ");
            System.out.print("n=" + n.getId());
            System.out.print(" c=" + c.getId() + " -");
            System.out.print("  d = " + cld.d);
            System.out.print("  a = " + cld.a);
            System.out.print("     n.r = " + nld.r);
            System.out.print("     n.d = " + nld.d);
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
            System.out.println("ac: s > PI!");
        } else {
            nld.c = 1;
            nld.f = Math.PI - s;
            System.out.println("ac: s <= PI.");
        }
    }

    private void setRadius(Node n) {
        NodeData nnd = n.getNodeData();
        BalloonTreeNodeLayoutData nld = nnd.getLayoutData();
        double cx = 0.0, cy = 0.0;

        Node[] children = graph.getSuccessors(n).toArray();

        // Compute barycenter of the layout circle containing all children
        for (int i = 0; i < children.length - 1; i++) {
            float thisX = children[i].getNodeData().x();
            float thisY = children[i].getNodeData().y();
            float nextX = children[i + 1].getNodeData().x();
            float nextY = children[i + 1].getNodeData().y();
            cx = cx + (thisX + nextX) * (thisY * nextX - thisX * nextY);
            cy = cy + (thisY + nextY) * (thisY * nextX - thisX * nextY);
        }

        cx /= (6 * area(n));
        cy /= (6 * area(n));
        nld.rx = -cx;
        nld.ry = -cy;

        nld.r = Math.max(nld.d, minRadius) + 2 * nld.d;
    }

    private void secondWalk(Node n,
            double x, double y, double l, double t) {
        NodeData nnd = n.getNodeData();
        BalloonTreeNodeLayoutData nld = nnd.getLayoutData();

        int numChildren = 0;
        Node[] children = graph.getSuccessors(n).toArray();
        numChildren = children.length;

        double dd = l * nld.d;
        double p = t + Math.PI;
        double fs = (numChildren == 0 ? 0 : nld.f / numChildren + 1);
        double pr = 0;

        for (Node c : children) {
            NodeData cnd = c.getNodeData();
            BalloonTreeNodeLayoutData cld = cnd.getLayoutData();
            double aa = nld.c * cld.a;
            double rr = nld.d * Math.tan(aa) / (1 - Math.tan(aa));
            p += pr + aa + fs;
            double xx = (l * rr + dd) * Math.cos(p);
            double yy = (l * rr + dd) * Math.sin(p);
            pr = aa;
            secondWalk(c, x + xx, y + yy, l * nld.c, p);
        }
    }
      private double area(Node n) {
          Node children[] = graph.getSuccessors(n).toArray();
          double sum = 0.0;
         for (int i = 0; i < children.length - 1; i++) {
             float thisX = children[i].getNodeData().x();
             float thisY = children[i].getNodeData().y();
             float nextX = children[i + 1].getNodeData().x();
             float nextY = children[i + 1].getNodeData().y();
             sum = sum + (thisX * nextY) - (thisY * nextX);
         }
         return Math.abs(sum / 2);
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
