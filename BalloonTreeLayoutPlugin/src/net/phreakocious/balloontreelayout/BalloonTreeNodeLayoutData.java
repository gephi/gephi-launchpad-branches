
package net.phreakocious.balloontreelayout;

import org.gephi.graph.spi.LayoutData;

/**
 *
 * @author phreakocious
 */
public class BalloonTreeNodeLayoutData implements LayoutData {
        public int d = 0;           // Constant distance used to position each child node's layout circle
        public int r = 0;           // Radius of the layout circle containing the full subtree induced by this node
        public double rx = 0;       // X coordinate, relative to barycenter
        public double ry = 0;       // Y coordinate, relative to barycenter
        public double a = 0;        // Angle equal to the halfsector formed by this node, relative to its ancestor
        public double c = 0;        // Scaling factor
        public double f = 0;        // Free space
}
