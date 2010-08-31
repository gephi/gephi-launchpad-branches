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
package org.gephi.io.generator.plugin;

import java.util.Random;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = Generator.class)
public class RandomGraph implements Generator {

    protected int numberOfNodes = 50;
    protected double wiringProbability = 0.05;
    protected ProgressTicket progress;
    protected boolean cancel = false;

    public void generate(ContainerLoader container) {

        int max = numberOfNodes;
        if (wiringProbability > 0) {
            max += numberOfNodes - 1;
        }
        Progress.start(progress, max);
        int progressUnit = 0;
        Random random = new Random();

        NodeDraft[] nodeArray = new NodeDraft[numberOfNodes];
        for (int i = 0; i < numberOfNodes && !cancel; i++) {
            NodeDraft nodeDraft = container.factory().newNodeDraft();
            nodeDraft.setId("n" + i);
            container.addNode(nodeDraft);
            nodeArray[i] = nodeDraft;
            Progress.progress(progress, ++progressUnit);
        }

        if (wiringProbability > 0) {
            for (int i = 0; i < numberOfNodes - 1 && !cancel; i++) {
                NodeDraft node1 = nodeArray[i];
                for (int j = i + 1; j < numberOfNodes && !cancel; j++) {
                    NodeDraft node2 = nodeArray[j];
                    if (random.nextDouble() < wiringProbability) {
                        EdgeDraft edgeDraft = container.factory().newEdgeDraft();
                        edgeDraft.setSource(node1);
                        edgeDraft.setTarget(node2);
                        container.addEdge(edgeDraft);
                    }
                }
                Progress.progress(progress, ++progressUnit);
            }
        }

        Progress.finish(progress);
        progress = null;
    }

    public String getName() {
        return "Random Graph";
    }

    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(RandomGraphUI.class);
    }

    public void setNumberOfNodes(int numberOfNodes) {
        if (numberOfNodes < 0) {
            throw new IllegalArgumentException("# of nodes must be greater than 0");
        }
        this.numberOfNodes = numberOfNodes;
    }

    public void setWiringProbability(double wiringProbability) {
        if (wiringProbability < 0 || wiringProbability > 1) {
            throw new IllegalArgumentException("Wiring probability must be between 0 and 1");
        }
        this.wiringProbability = wiringProbability;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public double getWiringProbability() {
        return wiringProbability;
    }

    public boolean cancel() {
        cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }
}
