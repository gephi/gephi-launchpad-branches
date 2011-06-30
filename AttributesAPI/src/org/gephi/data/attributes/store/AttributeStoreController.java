package org.gephi.data.attributes.store;

import org.gephi.project.api.Workspace;

/**
 *
 * @author Ernesto A
 */
public interface AttributeStoreController {
    
    AttributeStore newStore(Workspace workspace);
    
    AttributeStore getStore(Workspace workspace);
    
    void  removeStore(Workspace workspace);
    
    void shutdown();

}
