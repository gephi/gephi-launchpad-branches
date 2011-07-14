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

package org.gephi.visualization.vizmodel;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

/**
 * Class representing configuration for visualization.
 *
 * @author Vojtech Bardiovsky
 */
public class Config {

    private HashMap<String, String> stringProperties;
    private HashMap<String, Integer> integerProperties;
    private HashMap<String, Float> floatProperties;
    private HashMap<String, float[]> floatArrayProperties;
    private HashMap<String, Boolean> booleanProperties;
    private HashMap<String, Font> fontProperties;
    private HashMap<String, Color> colorProperties;

    public Config() {
        defaultValues();
    }

    private void defaultValues() {
        setProperty("defaultCulling", false);
        setProperty("defaultMaterial", false);
        setProperty("defaultCameraTarget", new float[]{0f, 0f, 0f});
        // ...
    }

    public void setProperty(String key, String value) {
        stringProperties.put(key, value);
    }

    public void setProperty(String key, int value) {
        integerProperties.put(key, value);
    }

    public void setProperty(String key, float value) {
        floatProperties.put(key, value);
    }

    public void setProperty(String key, boolean value) {
        booleanProperties.put(key, value);
    }

    public void setProperty(String key, float[] array) {
        floatArrayProperties.put(key, array);
    }

    public void setProperty(String key, Font font) {
        fontProperties.put(key, font);
    }

    public void setProperty(String key, Color color) {
        colorProperties.put(key, color);
    }

    public String getStringProperty(String key) {
        return stringProperties.get(key);
    }

    public int getIntProperty(String key) {
        return integerProperties.get(key);
    }

    public float getFloatProperty(String key) {
        return floatProperties.get(key);
    }

    public boolean getBooleanProperty(String key) {
        return booleanProperties.get(key);
    }

    public float[] getFloatArrayProperty(String key) {
        return floatArrayProperties.get(key);
    }

    public Font getFontProperty(String key) {
        return fontProperties.get(key);
    }

    public Color getColorProperty(String key) {
        return colorProperties.get(key);
    }

}
