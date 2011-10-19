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
package org.gephi.visualization.drawcall.edge;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import javax.media.opengl.GL;
import org.gephi.math.Vec2;
import org.gephi.math.Vec2M;
import org.gephi.visualization.api.Color;
import org.gephi.visualization.data.graph.VizEdge2D;
import org.gephi.visualization.data.graph.VizEdgeShape;
import org.gephi.visualization.drawcall.DrawCall;
import org.gephi.visualization.drawcall.DrawCallBuilder;
import org.gephi.visualization.drawcall.MemoryPool;
import org.gephi.visualization.drawcall.TechniqueFactory;
import org.gephi.visualization.rendering.command.Technique;

/**
 * This class creates the draw calls for the edges which are not edge loops.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Edge2DDrawCallBuilder implements DrawCallBuilder<VizEdge2D> {
    protected static final int VERTEX_SIZE = 3*4;
    protected static final int INDEX_SIZE = 2;
    
    private final Technique<DrawCall> technique;
    
    private ByteBuffer vertexBuffer;
    private ByteBuffer indexBuffer;
    private int start;

    public Edge2DDrawCallBuilder(TechniqueFactory factory) {
        this.technique = factory.create(new Edge2DTechniqueFixedImpl());
        
        this.vertexBuffer = null;
        this.indexBuffer = null;
        this.start = 0;
    }
    
    @Override
    @SuppressWarnings("fallthrough")
    public DrawCall add(MemoryPool memory, VizEdge2D e) {
        if (e.shape == VizEdgeShape.EDGE_LOOP) return null;
        
        if (this.vertexBuffer == null) {
            this.vertexBuffer = memory.newBuffer();
        }
        
        if (this.indexBuffer == null) {
            this.indexBuffer = memory.newBuffer();
            this.start = 0;
        }
        
        int requiredVertexSize = VERTEX_SIZE * 4;
        int requiredIndexSize = INDEX_SIZE * 6;
        if (e.shape == VizEdgeShape.STRAIGHT_EDGE_BIDIRECTIONAL) {
            requiredVertexSize += VERTEX_SIZE * 6;
            requiredIndexSize += INDEX_SIZE * 6;
        } else if (e.shape == VizEdgeShape.STRAIGHT_EDGE_DIRECTIONAL) {
            requiredVertexSize += VERTEX_SIZE * 3;
            requiredIndexSize += INDEX_SIZE * 3;
        }
        
        boolean vBuffFull = requiredVertexSize > this.vertexBuffer.remaining();
        boolean iBuffFull = requiredIndexSize > this.indexBuffer.remaining();
        final DrawCall result = vBuffFull && iBuffFull ?
                new DrawCall(this.technique, this.vertexBuffer, 
                        this.indexBuffer, start, 
                        (this.indexBuffer.position() - start)/INDEX_SIZE, 
                        GL.GL_TRIANGLES, GL.GL_UNSIGNED_SHORT) : null;
        if (vBuffFull) {
            this.vertexBuffer = memory.newBuffer();
        }
        if (iBuffFull) {
            this.indexBuffer = memory.newBuffer();
            this.start = 0;
        } else {
            this.start = this.indexBuffer.position();
        }
        
        final Vec2 diff = e.destinationPosition.minus(e.sourcePosition);
        final float len = diff.length();
        final Vec2 d = diff.normalized();
        final Vec2 d_perp = d.perp();
        
        final int startIdx = vertexBuffer.position() / VERTEX_SIZE;
        
        final Vec2M vert = e.sourcePosition.plusM(e.thickness, d_perp);
        e.gradientStart.writeRGBA8To(this.vertexBuffer);
        this.vertexBuffer.putFloat(vert.x());
        this.vertexBuffer.putFloat(vert.y());
                
        vert.minusEq(2.0f * e.thickness, d_perp);
        e.gradientStart.writeRGBA8To(this.vertexBuffer);
        this.vertexBuffer.putFloat(vert.x());
        this.vertexBuffer.putFloat(vert.y());
                
        vert.add(e.destinationPosition, - e.thickness, d_perp);
        e.gradientEnd.writeRGBA8To(this.vertexBuffer);
        this.vertexBuffer.putFloat(vert.x());
        this.vertexBuffer.putFloat(vert.y());
                
        vert.plusEq(2.0f * e.thickness, d_perp);
        e.gradientEnd.writeRGBA8To(this.vertexBuffer);
        this.vertexBuffer.putFloat(vert.x());
        this.vertexBuffer.putFloat(vert.y());
                
        this.indexBuffer.putShort((short)(startIdx));
        this.indexBuffer.putShort((short)(startIdx + 2));
        this.indexBuffer.putShort((short)(startIdx + 1));
        
        this.indexBuffer.putShort((short)(startIdx));
        this.indexBuffer.putShort((short)(startIdx + 3));
        this.indexBuffer.putShort((short)(startIdx + 2));
        
        int triStart = startIdx + 4;
        float t;
        Color g;
        switch (e.shape) {
            case STRAIGHT_EDGE_BIDIRECTIONAL:                
                vert.add(e.sourcePosition, e.sourceSize, d);
                t = e.sourceSize / len;
                g = Color.lerp(1.0f - t, e.gradientStart, t, e.gradientEnd);
                g.writeRGBA8To(this.vertexBuffer);
                this.vertexBuffer.putFloat(vert.x());
                this.vertexBuffer.putFloat(vert.y());
                
                vert.plusEq(3.0f * e.thickness, d).minusEq(3.0f * e.thickness, d_perp);
                t += (3.0f * e.thickness) / len;
                g = Color.lerp(1.0f - t, e.gradientEnd, t, e.gradientStart);
                g.writeRGBA8To(this.vertexBuffer);
                this.vertexBuffer.putFloat(vert.x());
                this.vertexBuffer.putFloat(vert.y());
                
                vert.plusEq(6.0f * e.thickness, d_perp);
                g.writeRGBA8To(this.vertexBuffer);
                this.vertexBuffer.putFloat(vert.x());
                this.vertexBuffer.putFloat(vert.y());
                
                this.indexBuffer.putShort((short)triStart);
                this.indexBuffer.putShort((short)(triStart + 1));
                this.indexBuffer.putShort((short)(triStart + 2));
                
                triStart += 3;
                // continue in the following case
            case STRAIGHT_EDGE_DIRECTIONAL:
                vert.sub(e.destinationPosition, e.destinationSize, d);
                t = e.destinationSize / len;
                g = Color.lerp(1.0f - t, e.gradientEnd, t, e.gradientStart);
                g.writeRGBA8To(this.vertexBuffer);
                this.vertexBuffer.putFloat(vert.x());
                this.vertexBuffer.putFloat(vert.y());
                
                vert.minusEq(3.0f * e.thickness, d).plusEq(3.0f * e.thickness, d_perp);
                t += (3.0f * e.thickness) / len;
                g = Color.lerp(1.0f - t, e.gradientEnd, t, e.gradientStart);
                g.writeRGBA8To(this.vertexBuffer);
                this.vertexBuffer.putFloat(vert.x());
                this.vertexBuffer.putFloat(vert.y());
                
                vert.minusEq(6.0f * e.thickness, d_perp);
                g.writeRGBA8To(this.vertexBuffer);
                this.vertexBuffer.putFloat(vert.x());
                this.vertexBuffer.putFloat(vert.y());
                
                this.indexBuffer.putShort((short)triStart);
                this.indexBuffer.putShort((short)(triStart + 2));
                this.indexBuffer.putShort((short)(triStart + 3));
                
                break;
            case STRAIGHT_EDGE_NO_DIRECTION:
            default:
                /* DO NOTHING */
        }
        
        return result;
    }

    @Override
    public List<DrawCall> create() {
        if (this.vertexBuffer == null || this.vertexBuffer.position() == 0 ||
            this.indexBuffer == null || this.indexBuffer.position() == 0) {
            return Collections.emptyList();
        }
        DrawCall result = new DrawCall(this.technique, this.vertexBuffer, 
                        this.indexBuffer, start, 
                        (this.indexBuffer.position() - start)/INDEX_SIZE, 
                        GL.GL_TRIANGLES, GL.GL_UNSIGNED_SHORT);
        this.vertexBuffer = null;
        this.indexBuffer = null;
        this.start = 0;
        return Collections.singletonList(result);
    }
        
}
