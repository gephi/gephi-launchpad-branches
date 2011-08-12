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

import java.util.Arrays;

/**
 * Calculates a frame rate as a moving average of the last few seconds.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FPSCounter {
    
    /**
     * Number of samples in the circular queue.
     */
    private final static int NUM_SAMPLES = 32;
    
    /**
     * Time in milliseconds passed between successive writing in the queues.
     */
    private final static long TIME_DELTA = 125;
    
    /**
     * Inverse of the total time of the moving window (TIME_DELTA * NUM_SAMPLES).
     */
    private final static double ONE_OVER_TOTAL_TIME = 1000.0 / (double) (TIME_DELTA * NUM_SAMPLES);
    
    /**
     * Circular queue where the number of frames in each time slice are stored.
     */
    private final int[] queue;
    
    /**
     * Position in circular queue.
     */
    private int i;
    
    /**
     * Absolute time of the last writing on the queue.
     */
    private long lastUpdate;
    
    /**
     * Absolute time of the last tick.
     */
    private long lastTick;
    
    /**
     * Number of ticks from the last time to now.
     */
    private int count;
    
    /**
     * Number of frames in the circular queue.
     */
    private int totalFrames;

    /**
     * Creates a new FPS Counter.
     */
    public FPSCounter() {
        this.queue = new int[NUM_SAMPLES];
        this.i = 0;
        
        this.lastUpdate = System.currentTimeMillis();
        this.lastTick = this.lastUpdate;
        this.count = 0;
        this.totalFrames = 0;
    }
    
    /**
     * Updates the circular queue if the time has passed.
     */
    private void updateQueue() {
        this.totalFrames -= this.queue[this.i];
        this.totalFrames += this.count;
        this.queue[this.i] = this.count;
        this.count = 0;
        
        if (++this.i >= NUM_SAMPLES) {
            this.i = 0;
        }
    }
    
    /**
     * Adds a frame to the counter and updates the internal queues if needed.
     */
    public synchronized void tick() {
        this.lastTick = System.currentTimeMillis();
        
        while ((this.lastTick - this.lastUpdate) > TIME_DELTA) {
            updateQueue();
            this.lastUpdate += TIME_DELTA;            
        }
        
        ++this.count;
    }
    
    /**
     * Returns the average frame rate in the last NUM_SAMPLES * TIME_DELTA ms.
     * 
     * @return the FPS
     */
    public synchronized double getFPS() {
        return this.totalFrames * ONE_OVER_TOTAL_TIME;
    }
    
    /**
     * Returns the time passed since the last tick.
     * @return 
     */
    public long timeSinceLastTick() {
        return System.currentTimeMillis() - this.lastTick;
    }
    
    /**
     * Resets all counters and queues.
     */
    public synchronized void reset() {
        Arrays.fill(this.queue, 0);
        this.i = 0;
        
        this.lastUpdate = System.currentTimeMillis();
        this.lastTick = this.lastUpdate;
        this.count = 0;
        this.totalFrames = 0;
    }
}
