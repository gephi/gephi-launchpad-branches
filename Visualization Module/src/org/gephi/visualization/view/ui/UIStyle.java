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

package org.gephi.visualization.view.ui;

import org.gephi.visualization.api.color.Color;

/**
 * Class which defines how UIPrimitives are rendered.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class UIStyle {

    public final static UIStyle SELECTION;
    static {
        final Color fSC = new Color(0.0f, 0.0f, 1.0f, 0.3f);
        final Color bSC = new Color(0.0f, 0.0f, 1.0f, 0.8f);
        final float bSW = 3.0f;
        SELECTION = new UIStyle(fSC, bSC, bSW);
    }

    private final Color fillColor;
    private final Color borderColor;
    private final float borderWidth;

    public UIStyle(Color fillColor, Color borderColor, float borderWidth) {
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
    }

    public Color fillColor() {
        return fillColor;
    }

    public Color borderColor() {
        return borderColor;
    }

    public float borderWidth() {
        return borderWidth;
    }
}
