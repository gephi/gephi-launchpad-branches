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
package org.gephi.visualization.rendering.command.instanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.gephi.visualization.rendering.command.Command;
import org.gephi.visualization.rendering.command.CommandListBuilder;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class InstancedCommandListBuilder<E> implements CommandListBuilder<E> {
    private boolean isRunning;
    private List<E> instances;
    private final InstancedTechnique<E> technique;

    public InstancedCommandListBuilder(InstancedTechnique<E> technique) {
        this.technique = technique;
        this.isRunning = false;
        this.instances = null;
    }
    
    @Override
    public void begin() {
        if (isRunning) return;
        
        this.instances = new ArrayList<E>();
        this.isRunning = true;
    }

    @Override
    public void add(E e) {
        if (!isRunning) return;
        
        this.instances.add(e);
    }

    @Override
    public void add(Collection<? extends E> c) {
        if (!isRunning) return;
        
        this.instances.addAll(c);
    }

    @Override
    public void add(E[] es) {
        if (!isRunning) return;
        this.instances.addAll(Arrays.asList(es));
    }

    @Override
    public List<Command> create() {
        if (!isRunning) return null;
        
        InstancedCommand<E> command = new InstancedCommand<E>(this.instances, this.technique);
        this.instances = null;
        this.isRunning = false;
        
        return Collections.singletonList((Command)command);
    }
    
}
