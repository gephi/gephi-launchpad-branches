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

import javax.media.opengl.GL;
import org.gephi.visualization.view.pipeline.Pipeline;
import org.gephi.visualization.view.pipeline.gl11.GL11Pipeline3D;
import org.gephi.visualization.view.profile.VizProperty.BooleanProperty;
import org.gephi.visualization.view.profile.VizProperty.IntProperty;
import org.gephi.visualization.view.profile.VizProperty.Type;
import org.gephi.visualization.view.profile.options.VizProfileOptionPanel;

/**
 * Profile based on OpenGL 1.1 to 1.5 versions. Different pipelines are choosen
 * depending on the OpenGL version.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FixedFuncProfile implements VizProfile {

    private boolean lod;
    private int lodLevels;
    
    private final static int MAX_LOD_LEVEL = 10;

    @Override
    public String name() {
        return "FixedFuncProfile";
    }

    @Override
    public String description() {
        return "Profile based on OpenGL 1.1 to 1.5 versions. Different pipelines are choosen depending on the OpenGL version.";
    }

    @Override
    public Pipeline createPipeline(GL gl) {
        return new GL11Pipeline3D();
    }

    @Override
    public void setProperty(VizProperty property) {
        String name = property.name();
        VizProperty.Type type = property.type();
        if (name.equals("LEVEL_OF_DETAIL") && type == Type.BOOLEAN) {
            this.lod = ((BooleanProperty) property).value();
        } else if (name.equals("LOD_LEVELS") && type == Type.INT) {
            int level = ((IntProperty) property).value();
            if (level >= 0 && level < MAX_LOD_LEVEL) {
                this.lodLevels = level;
            }
        }
    }

    @Override
    public void loadProperties() {
        /* TODO: IMPLEMENT METHOD */
    }

    @Override
    public void saveProperties() {
        /* TODO: IMPLEMENT METHOD */
    }

    @Override
    public VizProfileOptionPanel optionPanel() {
        return new VizProfileOptionPanel(this) {

            @Override
            protected VizProperty[] getProperties() {
                return new VizProperty[]{};
            }

            @Override
            public boolean valid() {
                return true;
            }

            @Override
            protected void updateValues() {
                /* EMPTY BLOCK */
            }
        };
    }

}
