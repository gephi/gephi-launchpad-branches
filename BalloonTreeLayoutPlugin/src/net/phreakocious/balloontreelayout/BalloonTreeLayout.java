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
        secondWalk(n, 0, 0, 1, 0);
    }

    private void firstWalk(Node n) {
        BalloonTreeNodeLayoutData nLayoutData = n.getNodeData().getLayoutData();
        nLayoutData.d = 0;
        NodeIterator childIter = graph.getSuccessors(n).iterator();
        while (childIter.hasNext()) {
            Node c = (Node) childIter.next();
            BalloonTreeNodeLayoutData cLayoutData = c.getNodeData().getLayoutData();
            firstWalk(c);            
            nLayoutData.d = Math.max(nLayoutData.d, cLayoutData.r);
            cLayoutData.a = (Math.atan(cLayoutData.r) / (nLayoutData.d + cLayoutData.r));
            
        }
        adjustChildren(n);
        setRadius(n);
    }

    private void adjustChildren(Node n) {
        double s = 0;
        BalloonTreeNodeLayoutData nLayoutData = n.getNodeData().getLayoutData();
        NodeIterator childIter = graph.getSuccessors(n).iterator();
        while (childIter.hasNext()) {
            Node c = (Node) childIter.next();
            BalloonTreeNodeLayoutData cLayoutData = c.getNodeData().getLayoutData();
            s += cLayoutData.a;
        }

        if (s > Math.PI) {
            nLayoutData.c = Math.PI / s;
            nLayoutData.f = 0;
        } else {
            nLayoutData.c = 1;
            nLayoutData.f = Math.PI - s;
        }
    }

    private void setRadius(Node n) {
        BalloonTreeNodeLayoutData layoutData = n.getNodeData().getLayoutData();
        double bx = 0.0, by = 0.0;

        Node children[] = graph.getSuccessors(n).toArray();

        // Compute barycenter of the layout circle containing all children
        for (int i = 0; i < children.length; i++) {
            bx += children[i].getNodeData().x();
            by += children[i].getNodeData().y();
        }
        bx /= children.length;
        by /= children.length;

        double maxVal = 0;
        // Compute radius for the layout circle as the largest required to contain all children
        for (int i = 0; i < children.length; i++) {
            BalloonTreeNodeLayoutData cLayoutData = children[i].getNodeData().getLayoutData();
            double cx = children[i].getNodeData().x();
            double cy = children[i].getNodeData().y();

            double dist = Math.sqrt(Math.pow(cx - bx, 2) + Math.pow(cy - by, 2)) + cLayoutData.r;

            if (dist > maxVal) {
                maxVal = dist;
            }
        }

        layoutData.rx = bx * -1;
        layoutData.ry = by * -1;
        
        layoutData.r = Math.max((int) maxVal, minRadius) + 2 * layoutData.d;
    }

    private void secondWalk(Node n,
            double bx, double by, double l, double t) {
        BalloonTreeNodeLayoutData nLayoutData = n.getNodeData().getLayoutData();

        // Store x and y values for node

        n.getNodeData().setX((float) (bx + l * (nLayoutData.rx * Math.cos(t) - nLayoutData.ry * Math.sin(t))));
        n.getNodeData().setY((float) (by + l * (nLayoutData.rx * Math.sin(t) + nLayoutData.ry * Math.cos(t))));

        int numChildren = 0;
        numChildren = graph.getSuccessors(n).toArray().length;

        double dd = l * nLayoutData.d;
        double p = Math.PI;
        double fs = (numChildren == 0 ? 0 : nLayoutData.f / numChildren + 1);
        double pr = 0;
        NodeIterator childIter = graph.getSuccessors(n).iterator();
        while (childIter.hasNext()) {
            Node c = (Node) childIter.next();
            BalloonTreeNodeLayoutData cLayoutData = c.getNodeData().getLayoutData();
            double aa = nLayoutData.c * cLayoutData.a;
            double rr = nLayoutData.d * Math.tan(aa) / (1 - Math.tan(aa));
            p += pr + cLayoutData.a + fs;

            double xx = (l * rr + dd) * Math.cos(p);
            double yy = (l * rr + dd) * Math.sin(p);

            xx += nLayoutData.rx;
            yy += nLayoutData.ry;

            double cost=Math.cos(t);
            double sint=Math.sin(t);

            xx = (cost * xx) + ((sint * -1) * yy);
            yy = (sint * xx) + (cost * yy);
               
           c.getNodeData().setX((float) xx);
           c.getNodeData().setY((float) yy);
          
            pr = cLayoutData.a;
            secondWalk(c, bx + xx, by + yy, l * rr/cLayoutData.r, p);
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
