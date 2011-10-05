/*
Copyright 2008-2011 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.visualization.rendering.apiimpl.command.edge;

import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.gephi.math.linalg.Vec2M;
import org.gephi.visualization.data.graph.VizEdge2D;
import org.gephi.visualization.data.graph.VizEdgeShape;
import org.gephi.visualization.rendering.command.buffer.Layout;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class EdgeBody2DLayoutGL12 extends Layout<VizEdge2D> {
    
    static final int VERTEX_SIZE = 24;

    @Override
    public boolean useStaticBuffer() {
        return false;
    }

    @Override
    public int glDrawMode() {
        return GL.GL_TRIANGLES;
    }

    @Override
    public int glDrawType() {
        return GL.GL_UNSIGNED_SHORT;
    }

    @Override
    public ByteBuffer getStaticIndexBuffer() {
        throw new UnsupportedOperationException("No static index buffer.");
    }

    @Override
    public int add(ByteBuffer b, VizEdge2D e) {
        throw new UnsupportedOperationException("No static index buffer.");
    }

    @Override
    @SuppressWarnings("fallthrough")
    public boolean add(ByteBuffer b, ByteBuffer ind, VizEdge2D e) {
        int requiredSize = VERTEX_SIZE * 4;
        if (e.shape == VizEdgeShape.STRAIGHT_EDGE_BIDIRECTIONAL) {
            requiredSize += VERTEX_SIZE * 6;
        } else if (e.shape == VizEdgeShape.STRAIGHT_EDGE_DIRECTIONAL) {
            requiredSize += VERTEX_SIZE * 3;
        }
        if (b.remaining() < requiredSize) return false;
        
        final Vec2M d = e.destinationPosition.minusM(e.sourcePosition).normalize();
        final Vec2M d_perp = d.perpM();
        
        int startIdx = b.position() / VERTEX_SIZE;
        
        final Vec2M vert = e.sourcePosition.plusM(e.thickness, d_perp);
        b.putFloat(e.gradientStart.r);
        b.putFloat(e.gradientStart.g);
        b.putFloat(e.gradientStart.b);
        b.putFloat(vert.x());
        b.putFloat(vert.y());
        b.putFloat(0.0f);
        
        vert.minusEq(2.0f * e.thickness, d_perp);
        b.putFloat(e.gradientStart.r);
        b.putFloat(e.gradientStart.g);
        b.putFloat(e.gradientStart.b);
        b.putFloat(vert.x());
        b.putFloat(vert.y());
        b.putFloat(0.0f);
        
        vert.add(e.destinationPosition, - e.thickness, d_perp);
        b.putFloat(e.gradientEnd.r);
        b.putFloat(e.gradientEnd.g);
        b.putFloat(e.gradientEnd.b);
        b.putFloat(vert.x());
        b.putFloat(vert.y());
        b.putFloat(0.0f);
        
        vert.plusEq(2.0f * e.thickness, d_perp);
        b.putFloat(e.gradientEnd.r);
        b.putFloat(e.gradientEnd.g);
        b.putFloat(e.gradientEnd.b);
        b.putFloat(vert.x());
        b.putFloat(vert.y());
        b.putFloat(0.0f);
        
        ind.putShort((short)(startIdx));
        ind.putShort((short)(startIdx + 2));
        ind.putShort((short)(startIdx + 1));
        
        ind.putShort((short)(startIdx));
        ind.putShort((short)(startIdx + 3));
        ind.putShort((short)(startIdx + 2));
        
        switch (e.shape) {
            case STRAIGHT_EDGE_BIDIRECTIONAL:                
                vert.add(e.sourcePosition, e.sourceSize, d);
                // TODO: calculate gradient color..
                b.putFloat(e.gradientStart.r);
                b.putFloat(e.gradientStart.g);
                b.putFloat(e.gradientStart.b);
                b.putFloat(vert.x());
                b.putFloat(vert.y());
                b.putFloat(0.0f);
                
                vert.plusEq(3.0f * e.thickness, d).minusEq(3.0f * e.thickness, d_perp);
                // TODO: calculate gradient color..
                b.putFloat(e.gradientStart.r);
                b.putFloat(e.gradientStart.g);
                b.putFloat(e.gradientStart.b);
                b.putFloat(vert.x());
                b.putFloat(vert.y());
                b.putFloat(0.0f);
                
                vert.plusEq(6.0f * e.thickness, d_perp);
                // TODO: calculate gradient color..
                b.putFloat(e.gradientStart.r);
                b.putFloat(e.gradientStart.g);
                b.putFloat(e.gradientStart.b);
                b.putFloat(vert.x());
                b.putFloat(vert.y());
                b.putFloat(0.0f);
                
                ind.putShort((short)(startIdx + 4));
                ind.putShort((short)(startIdx + 5));
                ind.putShort((short)(startIdx + 6));
                
                startIdx += 3;
                // continue in the following case
            case STRAIGHT_EDGE_DIRECTIONAL:
                vert.sub(e.destinationPosition, e.destinationSize, d);
                // TODO: calculate gradient color..
                b.putFloat(e.gradientEnd.r);
                b.putFloat(e.gradientEnd.g);
                b.putFloat(e.gradientEnd.b);
                b.putFloat(vert.x());
                b.putFloat(vert.y());
                b.putFloat(0.0f);
                
                vert.minusEq(3.0f * e.thickness, d).plusEq(3.0f * e.thickness, d_perp);
                // TODO: calculate gradient color..
                b.putFloat(e.gradientEnd.r);
                b.putFloat(e.gradientEnd.g);
                b.putFloat(e.gradientEnd.b);
                b.putFloat(vert.x());
                b.putFloat(vert.y());
                b.putFloat(0.0f);
                
                vert.minusEq(6.0f * e.thickness, d_perp);
                // TODO: calculate gradient color..
                b.putFloat(e.gradientEnd.r);
                b.putFloat(e.gradientEnd.g);
                b.putFloat(e.gradientEnd.b);
                b.putFloat(vert.x());
                b.putFloat(vert.y());
                b.putFloat(0.0f);
                
                ind.putShort((short)(startIdx + 4));
                ind.putShort((short)(startIdx + 5));
                ind.putShort((short)(startIdx + 6));
                
                break;
            case STRAIGHT_EDGE_NO_DIRECTION:
            default:
                /* DO NOTHING */
        }
        
        return true;
    }

    @Override
    public void enableClientStates(GL gl) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return;
        
        gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    }

    @Override
    public void disableClientStates(GL gl) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return;
        
        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }

    @Override
    public void setPointers(GL gl, ByteBuffer b, int i) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null || i != 0) return;
        
        if (b.remaining() > VERTEX_SIZE && b.position() == 0) {
            gl2.glColorPointer(3, GL.GL_FLOAT, VERTEX_SIZE, b);
            b.position(12);
            gl2.glVertexPointer(3, GL.GL_FLOAT, VERTEX_SIZE, b);
            b.position(0);
        }
    }

    @Override
    public void setOffsets(GL gl, int i) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null || i != 0) return;
        
        gl2.glColorPointer(3, GL.GL_FLOAT, VERTEX_SIZE, 0);
        gl2.glVertexPointer(3, GL.GL_FLOAT, VERTEX_SIZE, 12);
    }
    
}
