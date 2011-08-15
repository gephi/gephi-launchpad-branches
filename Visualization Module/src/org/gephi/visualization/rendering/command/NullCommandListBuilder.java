/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.visualization.rendering.command;

import java.util.Collections;
import java.util.List;
import javax.media.opengl.GL;

/**
 *
 * @author antonio
 */
public class NullCommandListBuilder<E> implements CommandListBuilder<E> {

    @Override
    public void begin() {
        /* EMPTY BLOCK */
    }

    @Override
    public void add(E e) {
        /* EMPTY BLOCK */
    }

    @Override
    public List<Command> create() {
        return Collections.emptyList();
    }

    @Override
    public void dispose(GL gl) {
        /* EMPTY BLOCK */
    }
    
}
