package org.gephi.data.attributes.store;

import org.gephi.project.api.Workspace;

/**
 *
 * @author Ernesto A
 */
public interface AttributeStoreController {
    
    void newStore(Workspace workspace);
    
    AttributeStore getNodeStore(Workspace workspace);
    
    AttributeStore getEdgeStore(Workspace workspace);
    
    void  removeStore(Workspace workspace);
    
    void shutdown();

}
