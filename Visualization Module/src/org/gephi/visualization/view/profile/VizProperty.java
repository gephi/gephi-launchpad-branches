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

package org.gephi.visualization.view.profile;

import org.gephi.lib.gleem.linalg.Mat2f;
import org.gephi.lib.gleem.linalg.Mat3f;
import org.gephi.lib.gleem.linalg.Mat4f;
import org.gephi.lib.gleem.linalg.Vec2f;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.lib.gleem.linalg.Vec4f;
import org.gephi.visualization.api.color.Color;

/**
 * It represents a visualization property in VizProfile. It use subclasses to
 * select between the different types the value may have. A property should be
 * created using the make static method. The user should cast the VizProperty to
 * the correct subclass to retrieve the value.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class VizProperty {
    private final String name;

    private VizProperty(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public enum Type {
        BOOLEAN,
        INT,
        FLOAT,
        VEC2F,
        VEC3F,
        VEC4F,
        MAT2F,
        MAT3F,
        MAT4F,
        COLOR,
    }

    public abstract Type type();

    /*
     * Subclasses
     */
    public static class BooleanProperty extends VizProperty {
        private final boolean value;

        private BooleanProperty(String name, boolean value) {
            super(name);
            this.value = value;
        }

        public boolean value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.BOOLEAN;
        }
    }

    public static class IntProperty extends VizProperty {
        private final int value;

        private IntProperty(String name, int value) {
            super(name);
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.INT;
        }
    }

    public static class FloatProperty extends VizProperty {
        private final float value;

        private FloatProperty(String name, float value) {
            super(name);
            this.value = value;
        }

        public float value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.FLOAT;
        }
    }

    public static class Vec2fProperty extends VizProperty {
        private final Vec2f value;

        private Vec2fProperty(String name, Vec2f value) {
            super(name);
            this.value = value;
        }

        public Vec2f value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.VEC2F;
        }
    }

    public static class Vec3fProperty extends VizProperty {
        private final Vec3f value;

        private Vec3fProperty(String name, Vec3f value) {
            super(name);
            this.value = value;
        }

        public Vec3f value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.VEC3F;
        }
    }

    public static class Vec4fProperty extends VizProperty {
        private final Vec4f value;

        private Vec4fProperty(String name, Vec4f value) {
            super(name);
            this.value = value;
        }

        public Vec4f value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.VEC4F;
        }
    }

    public static class Mat2fProperty extends VizProperty {
        private final Mat2f value;

        private Mat2fProperty(String name, Mat2f value) {
            super(name);
            this.value = value;
        }

        public Mat2f value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.MAT2F;
        }
    }

    public static class Mat3fProperty extends VizProperty {
        private final Mat3f value;

        private Mat3fProperty(String name, Mat3f value) {
            super(name);
            this.value = value;
        }

        public Mat3f value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.MAT3F;
        }
    }

    public static class Mat4fProperty extends VizProperty {
        private final Mat4f value;

        private Mat4fProperty(String name, Mat4f value) {
            super(name);
            this.value = value;
        }

        public Mat4f value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.MAT4F;
        }
    }

    public static class ColorProperty extends VizProperty {
        private final Color value;

        private ColorProperty(String name, Color value) {
            super(name);
            this.value = value;
        }

        public Color value() {
            return this.value;
        }

        @Override
        public Type type() {
            return Type.COLOR;
        }
    }

    /*
     * make static methods.
     */
    public static VizProperty make(String name, boolean value) {
        return new BooleanProperty(name, value);
    }

    public static VizProperty make(String name, int value) {
        return new IntProperty(name, value);
    }

    public static VizProperty make(String name, float value) {
        return new FloatProperty(name, value);
    }

    public static VizProperty make(String name, Vec2f value) {
        return new Vec2fProperty(name, value);
    }

    public static VizProperty make(String name, Vec3f value) {
        return new Vec3fProperty(name, value);
    }

    public static VizProperty make(String name, Vec4f value) {
        return new Vec4fProperty(name, value);
    }

    public static VizProperty make(String name, Mat2f value) {
        return new Mat2fProperty(name, value);
    }

    public static VizProperty make(String name, Mat3f value) {
        return new Mat3fProperty(name, value);
    }

    public static VizProperty make(String name, Mat4f value) {
        return new Mat4fProperty(name, value);
    }

    public static VizProperty make(String name, Color value) {
        return new ColorProperty(name, value);
    }
    
}
