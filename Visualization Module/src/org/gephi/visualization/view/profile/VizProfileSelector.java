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

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import javax.media.opengl.GL;
import org.gephi.visualization.view.pipeline.Pipeline;

/**
 * Service provider used to select the current visualization profile to be used
 * by the application.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class VizProfileSelector {

    private static final VizProfile nullProfile;
    private static final Queue<VizProfile> profiles;

    private static VizProfile forcedProfile;
    private static boolean fallToDefault;
    
    static {
        nullProfile = new NullVizProfile();
        profiles = new ArrayDeque<VizProfile>();
        /* TODO: add default profiles to the queue. */
        profiles.offer(nullProfile);
        profiles.offer(new FixedFuncProfile());
        
        for (VizProfile p : profiles) {
            p.loadProperties();
        }

        forcedProfile = null;
        fallToDefault = true;
    }

    public static synchronized VizProfile[] profiles() {
        return profiles.toArray(new VizProfile[profiles.size()]);
    }

    public static synchronized boolean offer(VizProfile p) {
        return profiles.offer(p);
    }

    public static synchronized Pipeline createPipeline(GL gl) {
        if (forcedProfile != null) {
            Pipeline pipeline = forcedProfile.createPipeline(gl);
            if (pipeline == null && !fallToDefault) return nullProfile.createPipeline(gl);
        }

        Iterator<VizProfile> it = profiles.iterator();

        while (it.hasNext()) {
            VizProfile p = it.next();
            Pipeline pipeline = (p != forcedProfile) ? p.createPipeline(gl) : null;
            if (pipeline != null) return pipeline;
        }

        return nullProfile.createPipeline(gl);
    }

    public static synchronized void forceProfile(String name, boolean useOtherIfUnavailable) {
        fallToDefault = useOtherIfUnavailable;

        Iterator<VizProfile> it = profiles.iterator();

        while (it.hasNext()) {
            VizProfile p = it.next();

            if (p.name().equalsIgnoreCase(name)) {
                forcedProfile = p;
            }
        }

        forcedProfile = null;
    }
}
