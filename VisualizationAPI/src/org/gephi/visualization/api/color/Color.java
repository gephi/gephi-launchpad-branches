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

package org.gephi.visualization.api.color;

import java.nio.ByteBuffer;

/**
 * Immutable color class.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Color {

    /*
     * Constants colors.
     */
    public final static Color WHITE = new Color(255, 255, 255);
    public final static Color LIGHT_GRAY = new Color(192, 192, 192);
    public final static Color GRAY = new Color(128, 128, 128);
    public final static Color DARK_GRAY  = new Color(64, 64, 64);
    public final static Color BLACK = new Color(0, 0, 0);
    public final static Color RED = new Color(255, 0, 0);
    public final static Color PINK = new Color(255, 175, 175);
    public final static Color ORANGE = new Color(255, 200, 0);
    public final static Color YELLOW = new Color(255, 255, 0);
    public final static Color GREEN = new Color(0, 255, 0);
    public final static Color MAGENTA = new Color(255, 0, 255);
    public final static Color CYAN = new Color(0, 255, 255);
    public final static Color BLUE = new Color(0, 0, 255);
    public final static Color TRANSPARENT = new Color(0.0f, 0.0f, 0.0f, 0.0f);

    /*
     * Private data
     */
    private final float r, g, b, a;

    /*
     * Constructors
     */
    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Color(float r, float g, float b, float a) {
        this.r = r; this.g = g; this.b = b; this.a = a;
    }

    public Color(int r, int g, int b) {
        this(r/255.0f, g/255.0f, b/255.0f);
    }

    public Color(int r, int g, int b, int a) {
        this(r/255.0f, g/255.0f, b/255.0f, a/255.0f);
    }

    public Color(int c) {
        this(c >> 24, (c >> 16)& 0xff, (c >> 8) & 0xff, c & 0xff);
    }

    public Color(java.awt.Color c) {
        this(c.getRed(), c.getGreen(), c.getBlue(), c.getTransparency());
    }

    public Color(float[] c) {
        this(c[0], c[1], c[2], c[3]);
    }

    public Color(ByteBuffer buf) {
        this(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
    }

    public Color(ByteBuffer buf, int i) {
        this(buf.getFloat(i), buf.getFloat(i+4), buf.getFloat(i+8), buf.getFloat(i+12));
    }

    /*
     * Object methods.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if ( !(obj instanceof Color) ) return false;

        return this.toRGBA() == ((Color)obj).toRGBA();
    }

    @Override
    public int hashCode() {
        return toRGBA();
    }

    @Override
    public String toString() {
        return "(" + this.r + ", " + this.g + ", " + this.b + ", " + this.a + ")";
    }

    /*
     * Getters
     */
    public float r() {
        return this.r;
    }

    // premultiplied version of the red component
    public float ra() {
        return this.r * this.a;
    }

    public float g() {
        return this.g;
    }

    // premultiplied version of the green component
    public float ga() {
        return this.g * this.a;
    }

    public float b() {
        return this.b;
    }

    // premultiplied version of the blue component
    public float ba() {
        return this.b * this.a;
    }

    public float a() {
        return this.a;
    }

    /*
     * Casts.
     */
    public int toRGBA() {
        int ret = (int)(clamp(this.r, 0.0f, 1.0f) * 255.0f) << 24;
        ret |= (int)(clamp(this.g, 0.0f, 1.0f) * 255.0f) << 16;
        ret |= (int)(clamp(this.b, 0.0f, 1.0f) * 255.0f) << 8;
        ret |= (int)(clamp(this.a, 0.0f, 1.0f) * 255.0f);

        return ret;
    }

    public int toRGB() {
        int ret = (int)(clamp(this.r, 0.0f, 1.0f) * 255.0f) << 16;
        ret |= (int)(clamp(this.g, 0.0f, 1.0f) * 255.0f) << 8;
        ret |= (int)(clamp(this.b, 0.0f, 1.0f) * 255.0f);

        return ret;
    }

    public java.awt.Color toAWTColor() {
        return new java.awt.Color(this.r, this.g, this.b, this.a);
    }

    public float[] toArrayRGBA() {
        return new float[]{this.r, this.g, this.b, this.a};
    }

    public float[] toArrayRGB() {
        return new float[]{this.r, this.g, this.b};
    }

    public float[] toArrayPremultiplied() {
        return new float[]{this.ra(), this.ga(), this.ba(), this.a};
    }

    /*
     * Writers.
     */
    public void writeRGBATo(ByteBuffer buf) {
        buf.putInt(toRGBA());
    }

    public void writeRGBTo(ByteBuffer buf) {
        buf.put((byte)(clamp(this.r, 0.0f, 1.0f) * 255.0f));
        buf.put((byte)(clamp(this.g, 0.0f, 1.0f) * 255.0f));
        buf.put((byte)(clamp(this.b, 0.0f, 1.0f) * 255.0f));
    }

    public void writeArrayRGBATo(ByteBuffer buf) {
        buf.putFloat(this.r);
        buf.putFloat(this.g);
        buf.putFloat(this.b);
        buf.putFloat(this.a);
    }

    public void writeArrayRGBTo(ByteBuffer buf) {
        buf.putFloat(this.r);
        buf.putFloat(this.g);
        buf.putFloat(this.b);
    }

    public void writePremultipliedTo(ByteBuffer buf) {
        buf.putFloat(this.ra());
        buf.putFloat(this.ga());
        buf.putFloat(this.ba());
        buf.putFloat(this.a);
    }

    public void writeRGBATo(ByteBuffer buf, int i) {
        buf.putInt(i, toRGBA());
    }

    public void writeRGBTo(ByteBuffer buf, int i) {
        buf.put(i, (byte)(clamp(this.r, 0.0f, 1.0f) * 255.0f));
        buf.put(i+1, (byte)(clamp(this.g, 0.0f, 1.0f) * 255.0f));
        buf.put(i+2, (byte)(clamp(this.b, 0.0f, 1.0f) * 255.0f));
    }

    public void writeArrayRGBATo(ByteBuffer buf, int i) {
        buf.putFloat(i, this.r);
        buf.putFloat(i+4, this.g);
        buf.putFloat(i+8, this.b);
        buf.putFloat(i+12, this.a);
    }

    public void writeArrayRGBTo(ByteBuffer buf, int i) {
        buf.putFloat(i, this.r);
        buf.putFloat(i+4, this.g);
        buf.putFloat(i+8, this.b);
    }

    public void writePremultipliedTo(ByteBuffer buf, int i) {
        buf.putFloat(i, this.ra());
        buf.putFloat(i+4, this.ga());
        buf.putFloat(i+8, this.ba());
        buf.putFloat(i+12, this.a);
    }

    /*
     * Operations on colors
     */
    public Color clamp(float m, float M) {
        return new Color(clamp(this.r, m, M), clamp(this.g, m, M), clamp(this.b, m, M), clamp(this.a, m, M));
    }

    public Color clamp() {
        return clamp(0.0f, 1.0f);
    }

    /*
     * Static methods.
     */
    public static Color readRGBAFrom(ByteBuffer buf) {
        return new Color(buf.getInt());
    }

    public static Color readRGBAFrom(ByteBuffer buf, int i) {
        return new Color(buf.getInt(i));
    }

    public static Color readRGBFrom(ByteBuffer buf) {
        int c = (buf.get() << 16) | (buf.get() << 8) | buf.get();
        return new Color(c);
    }

    public static Color readRGBFrom(ByteBuffer buf, int i) {
        int c = (buf.get(i) << 16) | (buf.get(i+1) << 8) | buf.get(i+2);
        return new Color(c);
    }

    public static Color readArrayRGBAFrom(ByteBuffer buf) {
        return new Color(buf);
    }

    public static Color readArrayRGBAFrom(ByteBuffer buf, int i) {
        return new Color(buf, i);
    }

    public static Color readArrayRGBFrom(ByteBuffer buf) {
        final float r = buf.getFloat();
        final float g = buf.getFloat();
        final float b = buf.getFloat();
        return new Color(r, g, b);
    }

    public static Color readArrayRGBFrom(ByteBuffer buf, int i) {
        final float r = buf.getFloat(i);
        final float g = buf.getFloat(i+4);
        final float b = buf.getFloat(i+8);
        return new Color(r, g, b);
    }

    public static Color readPremultipliedFrom(ByteBuffer buf) {
        final float r = buf.getFloat();
        final float g = buf.getFloat();
        final float b = buf.getFloat();
        final float a = buf.getFloat();
        if (a < 1.0f/255.0f)
            return new Color(r/a, g/a, b/a, a);
        else
            return new Color(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public static Color readPremultipliedFrom(ByteBuffer buf, int i) {
        final float r = buf.getFloat(i);
        final float g = buf.getFloat(i+4);
        final float b = buf.getFloat(i+8);
        final float a = buf.getFloat(i+12);
        if (a < 1.0f/255.0f)
            return new Color(r/a, g/a, b/a, a);
        else
            return new Color(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /*
     * Private methods
     */
    private float clamp(float t, float m, float M) {
        return t < m ? m : (t > M ? M : t);
    }
    
}
