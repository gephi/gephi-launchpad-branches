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
package org.gephi.visualization.rendering.command.instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.media.opengl.GL;
import org.gephi.visualization.drawcall.MemoryPool;
import org.gephi.visualization.rendering.command.Command;
import org.gephi.visualization.rendering.command.CommandListBuilder;
import org.gephi.visualization.rendering.command.GenericCommand;
import org.gephi.visualization.rendering.command.Technique;

/**
 * Command List Builder implementation which simply stores a list of objects.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class InstancedCommandListBuilder<E> implements CommandListBuilder<E> {
    private final Technique<E> technique;
    private List<E> list;

    public InstancedCommandListBuilder(Technique<E> technique) {
        this.technique = technique;
        this.list = null;
    }

    @Override
    public void dispose(GL gl) {
        /* EMPTY BLOCK */
    }

    @Override
    public void begin() {
        if (this.list == null) this.list = new ArrayList<E>();
    }

    @Override
    public void add(MemoryPool memory, E e) {
        if (this.list != null) this.list.add(e);
    }

    @Override
    public List<Command> create() {
        if (this.list == null) return null;
        
        Command command = new GenericCommand<E>(this.list, this.technique);
        this.list = null;
        return Collections.singletonList(command);
    }
    
}
