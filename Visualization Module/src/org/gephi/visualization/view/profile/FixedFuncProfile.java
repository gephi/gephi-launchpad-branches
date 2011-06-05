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

/**
 * Profile based on OpenGL 1.1 to 1.5 versions. Different pipelines are choosen
 * depending on the OpenGL version.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FixedFuncProfile implements VizProfile {

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

}
