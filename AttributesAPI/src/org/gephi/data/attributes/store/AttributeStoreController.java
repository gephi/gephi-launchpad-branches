package org.gephi.data.attributes.store;

import org.gephi.data.attributes.api.AttributeModel;

/**
 *
 * @author Ernesto A
 */
public interface AttributeStoreController {
    
    void newStore(AttributeModel model);
    
    AttributeStore getNodeStore(AttributeModel model);
    
    AttributeStore getEdgeStore(AttributeModel model);
    
    void  removeStore(AttributeModel model);
    
    void shutdown();

}
