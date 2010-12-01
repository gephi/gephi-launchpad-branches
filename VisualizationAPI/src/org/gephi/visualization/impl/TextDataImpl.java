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
package org.gephi.visualization.impl;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import org.gephi.graph.api.TextData;

/**
 *
 * @author Mathieu Bastian
 */
public class TextDataImpl implements TextData {

    TextLine line = new TextLine();
    TextLine[] wrappedLines;
    float r = -1;
    float g;
    float b;
    float a = 1f;
    float size = 1f;
    float sizeFactor = 1f;
    boolean visible = true;

    public TextLine getLine() {
        return line;
    }

    public void setWrappedLines(TextDataImpl.TextLine[] lines) {
        this.wrappedLines = lines;
    }

    public void setText(String line) {
        this.line = new TextLine(line, this.line.bounds);
    }

    public boolean hasCustomColor() {
        return r > 0;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setSizeFactor(float sizeFactor) {
        this.sizeFactor = sizeFactor * size;
    }

    public void setColor(float r, float g, float b, float alpha) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = alpha;
    }

    public void setColor(Color color) {
        if (color == null) {
            r = -1;
        } else {
            setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        }
    }

    public float getWidth() {
        Rectangle2D rec = line.bounds;
        if (rec != null) {
            return (float) rec.getWidth() * sizeFactor;
        }
        return 0f;
    }

    public float getHeight() {
        Rectangle2D rec = line.bounds;
        if (rec != null) {
            return (float) rec.getHeight() * sizeFactor;
        }
        return 0f;
    }

    public String getText() {
        return line.text;
    }

    public float getSizeFactor() {
        return sizeFactor;
    }

    public float getSize() {
        return size;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getAlpha() {
        return a;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public static class TextLine {

        String text = "";
        Rectangle2D bounds;

        public TextLine() {
        }

        public TextLine(String text) {
            this.text = text;
        }

        public TextLine(String text, Rectangle2D bounds) {
            this.text = text;
            this.bounds = bounds;
        }

        public String getText() {
            return text;
        }

        public void setBounds(Rectangle2D bounds) {
            this.bounds = bounds;
        }
    }
}
