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

import org.gephi.lib.gleem.linalg.Vec4f;

/**
 * Class which defines how UIPrimitives are rendered.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class UIStyle {

    public final Vec4f fillColor;
    public final Vec4f borderColor;

    public float borderWidth;

    public UIStyle() {
        this.fillColor = new Vec4f();
        this.borderColor = new Vec4f();
    }

    public static UIStyle createDefault() {
        UIStyle style = new UIStyle();
        style.fillColor.set(0.0f, 0.0f, 1.0f, 0.3f);
        style.borderColor.set(0.0f, 0.0f, 1.0f, 1.0f);
        style.borderWidth = 3.0f;
        return style;
    }
}
