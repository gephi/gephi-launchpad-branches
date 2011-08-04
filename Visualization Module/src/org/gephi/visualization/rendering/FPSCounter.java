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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Calculates a frame rate using the round robin algorithm using two levels.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FPSCounter {
    
    /**
     * Number of samples in each circular queue (level).
     */
    private final static int NUM_SAMPLES = 32;
    
    /**
     * Time of nanoseconds passed between successive writing in the queues.
     */
    private final static long TIME_DELTA = 125000000;
    
    /**
     * Inverse of <code>TIME_DELTA<code> when calculated in seconds.
     */
    private final static double UPDATE_RATE = 1000000000.0 / (double) TIME_DELTA;
    
    /**
     * Level 0 circular queue.
     */
    private final int[] level0;
    
    /**
     * Position in the level 0 circular queue.
     */
    private int p0;
    
    /**
     * Level 1 circular queue.
     */
    private final int[] level1;
    
    /**
     * Position in the level 1 circular queue.
     */
    private int p1;
    
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
     * Creates a new FPS Counter.
     */
    public FPSCounter() {
        this.level0 = new int[NUM_SAMPLES];
        this.p0 = 0;
        
        this.level1 = new int[NUM_SAMPLES];
        this.p1 = 0;
        
        this.lastUpdate = System.nanoTime();
        this.lastTick = this.lastUpdate;
        this.count = 0;
    }
    
    /**
     * Updates the circular buffers with the old data.
     */
    private void updateLevels() {
        this.level0[ this.p0++ ] = this.count;
        this.count = 0;
        
        if (this.p0 >= NUM_SAMPLES) {
            this.p0 = 0;
            
            int c = 0;
            for (int i = 0; i < NUM_SAMPLES; ++i) {
                c += this.level0[i];
            }
            this.level1[ this.p1++ ] = c;
            
            this.p1 = this.p1 < NUM_SAMPLES ? this.p1 : 0;
        }
    }
    
    /**
     * Adds a frame to the counter and updates the internal queues if needed.
     */
    public synchronized void tick() {
        this.lastTick = System.nanoTime();
        
        while ((this.lastTick - this.lastUpdate) > TIME_DELTA) {
            updateLevels();
            this.lastUpdate += TIME_DELTA;            
        }
        
        ++this.count;
    }
    
    /**
     * Returns a list of frame rates from a given level.
     * 
     * @param level the level at which to get the frame rates
     * @return the list of frame rates
     */
    public synchronized List<Double> getFPSList(int level) {
        final List<Double> result = new ArrayList<Double>(NUM_SAMPLES);
        
        final int[] array = level == 0 ? this.level0 : this.level1;
        final int p = level == 0 ? this.p0 : this.p1;
        final double mult = level == 0 ? UPDATE_RATE : UPDATE_RATE / 32.0;
        
        for (int i = p+1; i < NUM_SAMPLES; ++i) {
            result.add((double)array[i] * mult);
        }
        for (int i = 0; i <= p; ++i) {
            result.add((double)array[i] * mult);
        }
        
        return result;
    }
    
    /**
     * Returns the last frame rate reading from the level 0.
     * @return the FPS
     */
    public synchronized double getFPS() {
        return this.level0[ (this.p0 + NUM_SAMPLES - 1) % NUM_SAMPLES ] * UPDATE_RATE;
    }
    
    /**
     * Returns the last frame rate reading from the level 1.
     * @return the FPS
     */
    public synchronized double getFPSSmooth() {
        return this.level1[ (this.p1 + NUM_SAMPLES - 1) % NUM_SAMPLES ] * UPDATE_RATE / 32.0;
    }
    
    /**
     * Returns the time passed since the last tick.
     * @return 
     */
    public long timeSinceLastTick() {
        return System.nanoTime() - this.lastTick;        
    }
    
    /**
     * Resets all counters and queues.
     */
    public synchronized void reset() {
        Arrays.fill(this.level0, 0);
        Arrays.fill(this.level1, 0);
        
        this.p0 = 0;
        this.p1 = 0;
        
        this.lastUpdate = System.nanoTime();
        this.lastTick = this.lastUpdate;
        this.count = 0;
    }
}
