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
package org.gephi.visualization.opengl.text;

import org.gephi.visualization.impl.TextDataImpl;
import javax.swing.ImageIcon;
import org.gephi.visualization.apiimpl.ModelImpl;

/**
 *
 * @author Mathieu Bastian
 */
public class ScaledSizeMode implements SizeMode {

    private static float FACTOR_2D = 2000f;

    public void init() {
    }

    public void setSizeFactor2d(float sizeFactor, TextDataImpl text, ModelImpl model) {
        float factor = FACTOR_2D * sizeFactor / model.getCameraDistance();
        factor *= text.getSize();
        text.setSizeFactor(factor);
    }

    public void setSizeFactor3d(float sizeFactor, TextDataImpl text, ModelImpl model) {
        float factor = sizeFactor * 1.9f + 0.1f;        //Between 0.1 and 2
        factor *= text.getSize();
        text.setSizeFactor(factor);
    }

    public String getName() {
        return "Scaled";
    }

    public ImageIcon getIcon() {
        return new ImageIcon(getClass().getResource("/org/gephi/visualization/opengl/text/ScaledSizeMode.png"));
    }

    @Override
    public String toString() {
        return getName();
    }
}
