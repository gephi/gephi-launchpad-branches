/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.visualization.apiimpl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.gephi.utils.collection.avl.AVLItemAccessor;
import org.gephi.utils.collection.avl.ParamAVLTree;
import org.gephi.utils.collection.avl.AVLItem;
import org.gephi.graph.api.Model;
import org.gephi.graph.api.Renderable;
import org.gephi.visualization.VizModel;
import org.gephi.lib.gleem.linalg.Vecf;
import org.gephi.visualization.opengl.octree.Octant;

/**
 *
 * @author Mathieu Bastian
 */
public abstract class ModelImpl<ObjectType extends Renderable> implements Model, AVLItem {

    private int ID = -1;
    private int cacheMarker;
    //Architecture
    protected Octant[] octants;
    protected ObjectType obj;
    //Graphical data
    protected float viewportX;
    protected float viewportY;
    protected float cameraDistance;
    protected float viewportRadius;
    protected float[] dragDistance;
    protected ColorLayer colorLayer;
    //Flags
    protected boolean selected;
    protected boolean highlight;
    public long markTime = 0;
    public long selectionMark = 0;
    public boolean mark = false;

    public abstract int[] octreePosition(float centerX, float centerY, float centerZ, float size);

    public abstract boolean isInOctreeLeaf(Octant leaf);

    public abstract void display(GL gl, GLU glu, VizModel model);

    public abstract boolean selectionTest(Vecf distanceFromMouse, float selectionSize);

    public abstract float getCollisionDistance(double angle);

    public abstract String toSVG();

    public void destroy() {
    }

    public ColorLayer getColorLayer() {
        return colorLayer;
    }

    public void setColorLayer(ColorLayer layer) {
        this.colorLayer = layer;
    }

    public int getNumber() {
        return ID;
    }

    public boolean isAutoSelected() {
        return false;
    }

    public boolean onlyAutoSelect() {
        return false;
    }

    public void setAutoSelect(boolean autoSelect) {
    }

    public void setID(int ID) {
        if (this.ID == -1) {
            this.ID = ID;
        }
    }

    public Octant[] getOctants() {
        return octants;
    }

    public void setOctant(Octant octant) {
        this.octants[0] = octant;
    }

    public void resetOctant() {
        if (this.octants != null) {
            this.octants = new Octant[]{null};
        }
    }

    public ObjectType getObj() {
        return obj;
    }

    public void setObj(ObjectType obj) {
        this.obj = obj;
    }

    public float getViewportRadius() {
        return viewportRadius;
    }

    public void setViewportRadius(float viewportRadius) {
        this.viewportRadius = viewportRadius;
    }

    public float getViewportX() {
        return viewportX;
    }

    public void setViewportX(float viewportX) {
        this.viewportX = viewportX;
    }

    public float getViewportY() {
        return viewportY;
    }

    public void setViewportY(float viewportY) {
        this.viewportY = viewportY;
    }

    public float getCameraDistance() {
        return cameraDistance;
    }

    public void setCameraDistance(float distance) {
        this.cameraDistance = distance;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public float[] getDragDistanceFromMouse() {
        return dragDistance;
    }

    public void setDragDistanceFromMouse(float[] dragDistance) {
        this.dragDistance = dragDistance;
    }

    public void setCacheMarker(int cacheMarker) {
        this.cacheMarker = cacheMarker;
    }

    public boolean isCacheMatching(int cacheMarker) {
        return cacheMarker == this.cacheMarker;
    }

    public int getCacheMarker() {
        return cacheMarker;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public boolean isValid() {
        return octants != null && octants[0] != null;
    }

    public void updatePositionFlag() {
        if (octants != null && octants[0] != null) {
            octants[0].requireUpdatePosition();
        }
    }

    public void cleanModel() {
        obj.setModel(null);
    }
}
