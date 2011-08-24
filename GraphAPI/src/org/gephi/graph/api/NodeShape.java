/*
Copyright 2008-2011 Gephi
Authors : Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>, Antonio Patriarca <antoniopatriarca@gmail.com>
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
package org.gephi.graph.api;

import java.io.IOException;
import java.net.URISyntaxException;
import org.openide.util.Lookup;

/**
 * Class representing visualization shape of a node.
 * 
 * @author Vojtech Bardiovsky
 * @author Antonio Patriarca
 */
public class NodeShape {
    
    // Predefined shapes
    public final static NodeShape DEFAULT = new NodeShape(Value.DEFAULT);
    public final static NodeShape CIRCLE = new NodeShape(Value.CIRCLE);
    public final static NodeShape TRIANGLE = new NodeShape(Value.TRIANGLE);
    public final static NodeShape SQUARE = new NodeShape(Value.SQUARE);
    public final static NodeShape DIAMOND = new NodeShape(Value.DIAMOND);
    public final static NodeShape PENTAGON = new NodeShape(Value.PENTAGON);
    public final static NodeShape HEXAGON = new NodeShape(Value.HEXAGON);
    
    /**
     * Returns a node shape representing an image given by the uri.
     */
    public static NodeShape imageShape(String uri) throws NodeShapeException {
        ImageNodeShapeFactory nodeShapeFactory = Lookup.getDefault().lookup(ImageNodeShapeFactory.class);
        return nodeShapeFactory.createNodeShape(uri);
    }
    
    public final Value value;

    public boolean isImage() {
        return value == Value.IMAGE;
    }
    
    protected NodeShape(Value value) {
        this.value = value;
    }
    
    /**
     * Implementations of this interface are responsible for loading the image
     * from the given URI and providing its ID for further reference.
     */
    public static interface ImageNodeShapeFactory {
        public NodeShape createNodeShape(String uri) throws NodeShapeException;
    }  
    
    public enum Value {
        DEFAULT(1.0f),
        CIRCLE(2.0f / (float)Math.sqrt(Math.PI)),
        TRIANGLE(2.0f / (float)Math.pow(3.0f, 0.25f)),
        SQUARE(1.0f),
        DIAMOND((float)Math.sqrt(2.0f)),
        PENTAGON(1.0f),
        HEXAGON(1.0f), 
        IMAGE(1.0f);
        
        private final float scaleFactor;

        private Value(float scaleFactor) {
            this.scaleFactor = scaleFactor;
        }
        
        public float scaleFactor() {
            return this.scaleFactor;
        }
        
        @Override
        public String toString() {
            switch (this) {
                case CIRCLE: return "Circle";
                case DEFAULT: return "Default";
                case DIAMOND: return "Diamond";
                case HEXAGON: return "Hexagon";
                case PENTAGON: return "Pentagon";
                case SQUARE: return "Square";
                case TRIANGLE: return "Triangle";
                case IMAGE: return "Image";
            }
            return null;
        }
        
    };
    /**
     * Returns array of all non-default, non-image shapes.
     */
    public static NodeShape[] specificShapes() {
        return new NodeShape[] {CIRCLE, TRIANGLE, SQUARE, DIAMOND, PENTAGON, HEXAGON};
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    public static class NodeShapeException extends Exception {
        public enum Cause {IO_ERROR, BAD_URI, UNSUPPORTED_IMAGE_FORMAT}
        private final Cause cause;
        
        public NodeShapeException(Cause cause, Throwable e) {
            super(e);
            this.cause = cause;
        }

        public Cause getExceptionCause() {
            return cause;
        }
        
    }
    
}
