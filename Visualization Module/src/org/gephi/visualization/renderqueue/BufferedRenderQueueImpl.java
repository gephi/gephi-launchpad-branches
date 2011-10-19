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
package org.gephi.visualization.renderqueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.opengl.GL;
import org.gephi.visualization.data.camera.Camera;
import org.gephi.visualization.data.camera.RenderArea;
import org.gephi.visualization.drawcall.DrawCall;
import org.gephi.visualization.drawcall.DrawCallBuilder;
import org.gephi.visualization.drawcall.MemoryPool;
import org.gephi.visualization.rendering.command.Technique;

/**
 * Render queue implementation which render several objects in the same batch.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class BufferedRenderQueueImpl<E> implements RenderQueueImpl<E> {
    private final MemoryPool memory;
    
    private final List<DrawCallBuilder<E>> builders;    
    private final Map<Technique, Integer> techniqueMap;
    private final List<List<DrawCall>> drawCalls;

    public BufferedRenderQueueImpl(MemoryPool memory, 
            List<DrawCallBuilder<E>> builders) {
        this.memory = memory;
        
        this.builders = builders;        
        this.techniqueMap = new HashMap<Technique, Integer>();
        this.drawCalls = new ArrayList<List<DrawCall>>();
    }

    @Override
    public void makeDrawable() {
        for (DrawCallBuilder<E> b : this.builders) {
            List<DrawCall> dcs = b.create();
            for (DrawCall dc : dcs) {
                addDrawCall(dc);
            }
        }
    }

    @Override
    public void makeEditable() {
        /* EMPTY BLOCK */
    }

    @Override
    public void add(E e) {
        for (DrawCallBuilder<E> b : this.builders) {
            DrawCall dc = b.add(this.memory, e);
            addDrawCall(dc);
        }
    }
    
    private void addDrawCall(DrawCall drawCall) {
        if (drawCall == null) return;
        
        if (this.techniqueMap.containsKey(drawCall.technique)) {
            final int i = this.techniqueMap.get(drawCall.technique);
            this.drawCalls.get(i).add(drawCall);
        } else {
            final ArrayList<DrawCall> lst = new ArrayList<DrawCall>();
            lst.add(drawCall);
            this.drawCalls.add(lst);
            this.techniqueMap.put(drawCall.technique, this.drawCalls.size() - 1);
        }
    }

    @Override
    public void clearQueue() {
        this.techniqueMap.clear();
        this.drawCalls.clear();
    }

    @Override
    public void draw(GL gl, Camera camera, RenderArea area, int drawCalls) {
        for (List<DrawCall> dcs : this.drawCalls) {
            assert dcs.size() > 0 : dcs.size();
            
            final Technique<DrawCall> technique = dcs.get(0).technique;
            
            if (!technique.begin(gl, camera, area, drawCalls > 0)) return;
            
            while (technique.advanceToNextPass(gl)) {
                for (DrawCall dc : dcs) {
                    technique.draw(gl, dc);
                }
            }
            
            technique.end(gl);
            
        }
    }
    
}
