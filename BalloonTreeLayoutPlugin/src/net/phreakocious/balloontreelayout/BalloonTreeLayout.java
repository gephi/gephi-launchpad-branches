package net.phreakocious.balloontreelayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterator;
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

    private Map<Integer, List<Node>> depthMapper;
    private Map<Node, Double> nodeToRadiusMapper;
    private int maxShortestPath;
    private int nodeId;
    private DirectedGraph graph;
    private int minRadius;

    public BalloonTreeLayout(LayoutBuilder layoutBuilder) {
        super(layoutBuilder);
    }

    public void initAlgo() {
        graph = graphModel.getHierarchicalDirectedGraphVisible();
        depthMapper = new HashMap<Integer, List<Node>>();
        nodeToRadiusMapper = new HashMap<Node, Double>();
        maxShortestPath = 0;
        setConverged(false);
    }

    private void layout(Node n, double x, double y) {
        firstWalk(n);
        secondWalk(n, null, x, y, 10, 10);
    }

    private void firstWalk(Node n) {
        BalloonTreeNodeLayoutData nLayoutData = n.getNodeData().getLayoutData();
        nLayoutData.d = 0;
        double s = 0;
        NodeIterator childIter = graph.getSuccessors(n).iterator();
        while (childIter.hasNext()) {
            Node c = (Node) childIter.next();
            firstWalk(c);
            BalloonTreeNodeLayoutData cLayoutData = c.getNodeData().getLayoutData();
            nLayoutData.d = Math.max(cLayoutData.d, cLayoutData.r);
            cLayoutData.a = (Math.atan(cLayoutData.r) / (nLayoutData.d + cLayoutData.r));
            s += cLayoutData.a;
        }
        adjustChildren(nLayoutData, s);
        setRadius(n);
    }

    private void adjustChildren(BalloonTreeNodeLayoutData layoutData, double s) {
        if (s > Math.PI) {
            layoutData.c = (Math.PI / s);
            layoutData.f = 0;
        } else {
            layoutData.c = 1;
            layoutData.f = Math.PI - s;
        }
    }

    private void setRadius(Node n) {
        BalloonTreeNodeLayoutData layoutData = n.getNodeData().getLayoutData();
        double cx = 0.0, cy = 0.0;

        Node children[] = graph.getSuccessors(n).toArray();

        // Compute barycenter of the layout circle containing all children
        for (int i = 0; i < children.length - 1; i++) {
            float thisX = children[i].getNodeData().x();
            float thisY = children[i].getNodeData().y();
            float nextX = children[i + 1].getNodeData().x() ;
            float nextY = children[i + 1].getNodeData().y();

            cx = cx + (thisX + nextX) * (thisY * nextX - thisX * nextY);
            cy = cy + (thisY + nextY) * (thisY * nextX - thisX * nextY);
        }
        cx /= (6 * area(n));
        cy /= (6 * area(n));
        layoutData.rx = cx * -1;
        layoutData.ry = cy * -1;
        
        layoutData.r = Math.max(layoutData.d, minRadius) + 2 * layoutData.d;
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

    private void secondWalk(Node n, Node r,
            double x, double y, double l, double t) {
        n.getNodeData().setX((float) x / 100);
        n.getNodeData().setY((float) y / 100);

        BalloonTreeNodeLayoutData nlayoutData = n.getNodeData().getLayoutData();

        int numChildren = 0;
        numChildren = graph.getSuccessors(n).toArray().length;

        double dd = l * nlayoutData.d;
        double p = t + Math.PI;
        double fs = (numChildren == 0 ? 0 : nlayoutData.f / numChildren);
        double pr = 0;
        NodeIterator childIter = graph.getSuccessors(n).iterator();
        while (childIter.hasNext()) {
            Node c = (Node) childIter.next();
            BalloonTreeNodeLayoutData clayoutData = c.getNodeData().getLayoutData();
            double aa = nlayoutData.c * clayoutData.a;
            double rr = nlayoutData.d * Math.tan(aa) / (1 - Math.tan(aa));
            p += pr + aa + fs;
            double xx = (l * rr + dd) * Math.cos(p);
            double yy = (l * rr + dd) * Math.sin(p);
            pr = aa;
            secondWalk(c, n, x + xx, y + yy, l * nlayoutData.c, p);
        }
    }

    public void goAlgo() {
        graph = graphModel.getHierarchicalDirectedGraphVisible();
        Node rootNode = GraphUtils.determineRootNode(graph);
        Node[] nodes = graph.getNodes().toArray();

        for (Node n : nodes) {
            if (n.getNodeData().getLayoutData() == null || !(n.getNodeData().getLayoutData() instanceof BalloonTreeNodeLayoutData)) {
                n.getNodeData().setLayoutData(new BalloonTreeNodeLayoutData());
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
