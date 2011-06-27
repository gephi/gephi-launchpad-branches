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
     * Constants
     */
    private static final int binarySizeRGBA8 = 4;
    private static final int binarySizeRGB8 = 3;
    private static final int binarySize = 4 * 4;
    private static final int binarySizeNoAlpha = 4 * 3;
    private static final int binarySizePremultiplied = 4 * 4;

    /*
     * Public final data
     */
    public final float r, g, b, a;

    /*
     * Constructors
     */
    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Color(float r, float g, float b, float a) {
        this.r = r; this.g = g; this.b = b; this.a = a;
    }

    public Color(byte r, byte g, byte b) {
        this(r/255.0f, g/255.0f, b/255.0f);
    }

    public Color(byte r, byte g, byte b, byte a) {
        this(r/255.0f, g/255.0f, b/255.0f, a/255.0f);
    }

    /**
     * Creates a new Color from its integer representation in the RGBA8 format.
     * It is not the same representation java.awt.Color use, since java.awt.Color
     * use the ARGB8 format.
     *
     * @param c Integer representation of the color in the RGBA8 format.
     */
    public Color(int c) {
        this(c >> 24, (c >> 16) & 0xff, (c >> 8) & 0xff, c & 0xff);
    }

    public Color(java.awt.Color c) {
        this(c.getRed(), c.getGreen(), c.getBlue(), c.getTransparency());
    }

    /*
     * Object methods.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ( !(obj instanceof Color) ) return false;

        Color c = (Color) obj;
        return (this.r == c.r) && (this.g == c.g) && (this.b == c.b) && (this.a == c.a);
    }

    @Override
    public int hashCode() {
        return toRGBA8();
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
    public int toRGBA8() {
        int ret = (int)(clamp(this.r, 0.0f, 1.0f) * 255.0f) << 24;
        ret |= (int)(clamp(this.g, 0.0f, 1.0f) * 255.0f) << 16;
        ret |= (int)(clamp(this.b, 0.0f, 1.0f) * 255.0f) << 8;
        ret |= (int)(clamp(this.a, 0.0f, 1.0f) * 255.0f);

        return ret;
    }

    public java.awt.Color toAWTColor() {
        return new java.awt.Color(this.r, this.g, this.b, this.a);
    }

    public float[] toArray() {
        return new float[]{this.r, this.g, this.b, this.a};
    }

    public float[] toArrayNoAlpha() {
        return new float[]{this.r, this.g, this.b};
    }

    public float[] toArrayPremultiplied() {
        return new float[]{this.ra(), this.ga(), this.ba(), this.a};
    }

    /*
     * Writers.
     */
    public void writeRGBA8To(ByteBuffer buf) {
        buf.putInt(toRGBA8());
    }

    public int writeRGBA8To(ByteBuffer buf, int i) {
        buf.putInt(i, toRGBA8());
        return i + binarySizeRGBA8;
    }

    public void writeRGB8To(ByteBuffer buf) {
        buf.put((byte)(clamp(this.r, 0.0f, 1.0f) * 255.0f));
        buf.put((byte)(clamp(this.g, 0.0f, 1.0f) * 255.0f));
        buf.put((byte)(clamp(this.b, 0.0f, 1.0f) * 255.0f));
    }

    public int writeRGB8To(ByteBuffer buf, int i) {
        buf.put(i, (byte)(clamp(this.r, 0.0f, 1.0f) * 255.0f));
        buf.put(i+1, (byte)(clamp(this.g, 0.0f, 1.0f) * 255.0f));
        buf.put(i+2, (byte)(clamp(this.b, 0.0f, 1.0f) * 255.0f));
        return i + binarySizeRGB8;
    }

    public void writeTo(ByteBuffer buf) {
        buf.putFloat(this.r);
        buf.putFloat(this.g);
        buf.putFloat(this.b);
        buf.putFloat(this.a);
    }

    public int writeTo(ByteBuffer buf, int i) {
        buf.putFloat(i, this.r);
        buf.putFloat(i+4, this.g);
        buf.putFloat(i+8, this.b);
        buf.putFloat(i+12, this.a);
        return i + binarySize;
    }

    public void writeNoAlphaTo(ByteBuffer buf) {
        buf.putFloat(this.r);
        buf.putFloat(this.g);
        buf.putFloat(this.b);
    }

    public int writeNoAlphaTo(ByteBuffer buf, int i) {
        buf.putFloat(i, this.r);
        buf.putFloat(i+4, this.g);
        buf.putFloat(i+8, this.b);
        return i + binarySizeNoAlpha;
    }

    public void writePremultipliedTo(ByteBuffer buf) {
        buf.putFloat(this.ra());
        buf.putFloat(this.ga());
        buf.putFloat(this.ba());
        buf.putFloat(this.a);
    }

    public int writePremultipliedTo(ByteBuffer buf, int i) {
        buf.putFloat(i, this.ra());
        buf.putFloat(i+4, this.ga());
        buf.putFloat(i+8, this.ba());
        buf.putFloat(i+12, this.a);
        return i + binarySizePremultiplied;
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
    public static Color readRGBA8From(ByteBuffer buf) {
        return new Color(buf.getInt());
    }

    public static Color readRGBA8From(ByteBuffer buf, int[] i) {
        final Color result = new Color(buf.getInt(i[0]));
        i[0] += binarySizeRGBA8;
        return result;
    }

    public static Color readRGB8From(ByteBuffer buf) {
        return new Color(buf.get(), buf.get(), buf.get());
    }

    public static Color readRGB8From(ByteBuffer buf, int[] i) {
        final Color result = new Color(buf.get(i[0]), buf.get(i[0]+1), buf.get(i[0]+2));
        i[0] += binarySizeRGB8;
        return result;
    }

    public static Color readFrom(ByteBuffer buf) {
        return new Color(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
    }

    public static Color readFrom(ByteBuffer buf, int[] i) {
        final Color result = new Color(buf.getFloat(i[0]), buf.getFloat(i[0]+4), buf.getFloat(i[0]+8), buf.getFloat(i[0]+12));
        i[0] += binarySize;
        return result;
    }

    public static Color readNoAlphaFrom(ByteBuffer buf) {
        return new Color(buf.getFloat(), buf.getFloat(), buf.getFloat());
    }

    public static Color readNoAlphaFrom(ByteBuffer buf, int[] i) {
        final Color result = new Color(buf.getFloat(i[0]), buf.getFloat(i[0]+4), buf.getFloat(i[0]+8));
        i[0] += binarySizeNoAlpha;
        return result;
    }

    public static Color readPremultipliedFrom(ByteBuffer buf) {
        final float r = buf.getFloat();
        final float g = buf.getFloat();
        final float b = buf.getFloat();
        final float a = buf.getFloat();
        if (a > 2.0e-16)
            return new Color(r/a, g/a, b/a, a);
        else
            return new Color(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public static Color readPremultipliedFrom(ByteBuffer buf, int[] i) {
        final float r = buf.getFloat(i[0]);
        final float g = buf.getFloat(i[0]+4);
        final float b = buf.getFloat(i[0]+8);
        final float a = buf.getFloat(i[0]+12);
        i[0] += binarySizePremultiplied;
        if (a > 2.0e-16)
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
