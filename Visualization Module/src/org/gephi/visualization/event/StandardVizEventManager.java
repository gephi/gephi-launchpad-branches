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

package org.gephi.visualization.event;

import org.gephi.visualization.api.event.VizEvent.Type;
import org.gephi.visualization.api.event.VizEventListener;
import org.gephi.visualization.api.event.VizEventManager;

public class StandardVizEventManager implements VizEventManager {

    @Override
    public void startDrag() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drag() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopDrag() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseLeftPress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseRightPress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseMiddlePress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseLeftClick() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseRightClick() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseMiddleClick() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseMove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseLeftPressing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseReleased() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(VizEventListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(VizEventListener[] listeners) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(VizEventListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(VizEventListener[] listeners) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasListeners(Type type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
