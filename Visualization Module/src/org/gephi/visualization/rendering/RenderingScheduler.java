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
package org.gephi.visualization.rendering;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.media.opengl.GLAutoDrawable;

/**
 * Controls the rendering loop. The FPS can change at runtime.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class RenderingScheduler {
    
    private final GLAutoDrawable drawable;
    
    private final FPSCounter counter;
    private final AtomicLong frameTime;
    
    private final AtomicBoolean animating;
    private Thread renderingThread;
    private final Object lock;
    
    public RenderingScheduler(GLAutoDrawable drawable) {
        this.drawable = drawable;
        
        this.counter = new FPSCounter();
        this.frameTime = new AtomicLong(0);
        
        this.renderingThread = null;
        this.animating = new AtomicBoolean(false);
        this.lock = new Object();
    }
    
    public RenderingScheduler(GLAutoDrawable drawable, int fps) {
        this.drawable = drawable;
        
        this.counter = new FPSCounter();
        this.frameTime = new AtomicLong(fps == 0 ? 0 : 1000 / fps);
        
        this.renderingThread = null;
        this.animating = new AtomicBoolean(false);
        this.lock = new Object();
    }
    
    public synchronized void startRendering() {
        if (this.isRunning()) return;

        this.renderingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (animating.get()) {
                        drawable.display();
                        
                        final long delta = frameTime.get();

                        long timeout = delta - counter.timeSinceLastTick();
                        while (timeout > 0) {
                            synchronized (lock) {
                                lock.wait(timeout);
                            }
                            timeout = delta - counter.timeSinceLastTick();
                        }
                        counter.tick();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        this.animating.set(true);
        this.renderingThread.start();
    }
    
    public synchronized void stopRendering() {
        if (!this.isRunning()) return;
        
        this.animating.set(false);
        this.renderingThread = null;
    }
    
    public void setFPS(int fps) {
        this.frameTime.set(fps == 0 ? 0 : 1000 / fps);
        
        synchronized (this.lock) {
            this.lock.notify();
        }
    }
    
    public boolean isRunning() {
        return this.renderingThread != null && this.animating.get();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.stopRendering();
        } finally {
            super.finalize();
        }
    }
    
    public double getFPS() {
        return this.counter.getFPS();
    }
}
