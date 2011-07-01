package org.gephi.data.attributes.store.locking;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Ernesto A
 */
public class LockingProvider {
    
    private final Map<Integer, Semaphore> mutexes = new HashMap<Integer, Semaphore>();

    public LockingProvider() {
    }

    public void lock(Integer id) throws InterruptedException {
        Semaphore mutex = getMutex(id);

        synchronized(mutex) {
            mutex.acquire();
        }
    }
    
    public void unlock(Integer id) {
        Semaphore mutex = getMutex(id);
        
        synchronized(mutex) {
            // only release if the permit is already acquired
            if (mutex.availablePermits() == 0) {
                mutex.release();
                
                // remove the mutex if no threads are waiting for it
                if (!mutex.hasQueuedThreads() || mutex.getQueueLength() == 0) {
                    mutexes.remove(id);
                }
            }
        }
    }
    
    private synchronized Semaphore getMutex(Integer id) {
        Semaphore mutex = mutexes.get(id);
        
        if (mutex == null) {
            mutex = new Semaphore(1);
            mutexes.put(id, mutex);
        }
        
        return mutex;
    }
}
