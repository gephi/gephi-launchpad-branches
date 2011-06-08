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
import org.gephi.visualization.data.FrameData;
import org.gephi.visualization.view.pipeline.Pipeline;
import org.gephi.visualization.view.profile.options.VizProfileOptionPanel;

/**
 * Profile used when no other profile is available. Its pipeline do nothing.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class NullVizProfile implements VizProfile {

    public NullVizProfile() {
    }

    @Override
    public String name() {
        return "Null profile.";
    }

    @Override
    public String description() {
        return "Profile used when no other profile is available. Its pipeline do nothing.";
    }

    @Override
    public void setProperty(VizProperty property) {
        /* EMPTY BLOCK */
    }

    @Override
    public Pipeline createPipeline(GL gl) {
        return new NullPipeline();
    }

    @Override
    public void loadProperties() {
        /* EMPTY BLOCK */
    }

    @Override
    public void saveProperties() {
        /* EMPTY BLOCK */
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

    /**
     * Pipeline used when no other pipeline can be used.
     */
    private static class NullPipeline implements Pipeline {

        public NullPipeline() {
            /* EMPTY BLOCK */
        }

        @Override
        public String name() {
            return "NullPipeline.";
        }

        @Override
        public String description() {
            return "Pipeline used when no other pipeline can be used.";
        }

        @Override
        public String debugInfo() {
            return "";
        }

        @Override
        public boolean init(GL gl) {
            return true;
        }

        @Override
        public void draw(GL gl, FrameData frameData) {
            /* EMPTY BLOCK */
        }

        @Override
        public void dispose(GL gl) {
            /* EMPTY BLOCK */
        }
    }
}
