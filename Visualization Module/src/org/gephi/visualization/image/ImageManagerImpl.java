/*
Copyright 2008-2011 Gephi
Authors : Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
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

package org.gephi.visualization.image;

import org.gephi.visualization.api.ImageNodeShape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.gephi.graph.api.NodeShape;
import org.gephi.graph.api.NodeShape.ImageNodeShapeFactory;
import org.gephi.visualization.api.ImageManager;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 * Class for loading images and providing access to them by their ID. 
 * 
 * @author Vojtech Bardiovsky
 */
@ServiceProviders({
    @ServiceProvider(service = ImageManager.class), 
    @ServiceProvider(service = ImageNodeShapeFactory.class)
})
public class ImageManagerImpl implements ImageManager {

    private int id;
    // TODO this list is the only one where order matters -> use better structure like map
    private List<BufferedImage> images = new ArrayList<BufferedImage>();
    private List<NodeShape> nodeShapes = new ArrayList<NodeShape>();
    
    @Override
    public BufferedImage getImage(int id) {
        return images.get(id);
    }

    @Override
    public NodeShape createNodeShape(String uri) throws URISyntaxException, IOException {
        BufferedImage image = ImageIO.read(new File(new URI(uri)));
        NodeShape nodeShape = new ImageNodeShape(id++);
        images.add(image);
        nodeShapes.add(nodeShape);
        return nodeShape;
    }

    @Override
    public ImageNodeShape[] getCreatedShapes() {
        return nodeShapes.toArray(new ImageNodeShape[]{});
    }
    
}
