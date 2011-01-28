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
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.type.DynamicFloat;
import org.gephi.data.attributes.type.Interval;
import org.gephi.data.properties.PropertiesColumn;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = Generator.class)
public class DynamicGraph implements Generator {

    protected int numberOfNodes = 50;
    protected double wiringProbability = 0.05;

    public void generate(ContainerLoader container) {
        Random random = new Random();

        AttributeColumn col = container.getAttributeModel().getNodeTable().addColumn("score", AttributeType.DYNAMIC_INT);

        NodeDraft[] nodeArray = new NodeDraft[numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++) {
            NodeDraft nodeDraft = container.factory().newNodeDraft();
            nodeDraft.setId("n" + i);
            container.addNode(nodeDraft);

            Random r = new Random();
            int randomStart = r.nextInt(10) + 2000;
            int randomEnd = randomStart + 20 + r.nextInt(10);
            nodeDraft.addTimeInterval("" + randomStart, "" + randomEnd);

            randomEnd = randomStart + r.nextInt(10);
            nodeDraft.addAttributeValue(col, r.nextInt(5), "" + randomStart, "" + randomEnd);
            randomStart = randomEnd + 1;
            randomEnd = randomStart + r.nextInt(10);
            nodeDraft.addAttributeValue(col, r.nextInt(5), "" + randomStart, "" + randomEnd);

            nodeArray[i] = nodeDraft;
        }

        if (wiringProbability > 0) {
            AttributeColumn oldWeight = container.getAttributeModel().getEdgeTable().getColumn(PropertiesColumn.EDGE_WEIGHT.getIndex());
            AttributeColumn weightCol = container.getAttributeModel().getEdgeTable().replaceColumn(oldWeight, PropertiesColumn.EDGE_WEIGHT.getId(), PropertiesColumn.EDGE_WEIGHT.getTitle(), AttributeType.DYNAMIC_FLOAT, AttributeOrigin.PROPERTY, null);

            for (int i = 0; i < numberOfNodes - 1; i++) {
                NodeDraft node1 = nodeArray[i];
                for (int j = i + 1; j < numberOfNodes; j++) {
                    NodeDraft node2 = nodeArray[j];
                    if (random.nextDouble() < wiringProbability) {
                        EdgeDraft edgeDraft = container.factory().newEdgeDraft();
                        edgeDraft.setSource(node1);
                        edgeDraft.setTarget(node2);

                        Random r = new Random();
                        DynamicFloat dynamicWeight = new DynamicFloat(new Interval<Float>(2010, 2012, false, true, new Float(r.nextInt(3)+1)));
                        dynamicWeight = new DynamicFloat(dynamicWeight, new Interval<Float>(2012, 2014, false, true, new Float(r.nextInt(3)+2)));
                        dynamicWeight = new DynamicFloat(dynamicWeight, new Interval<Float>(2014, 2016, false, true, new Float(r.nextInt(3)+3)));
                        dynamicWeight = new DynamicFloat(dynamicWeight, new Interval<Float>(2016, 2018, false, true, new Float(r.nextInt(3)+4)));
                        dynamicWeight = new DynamicFloat(dynamicWeight, new Interval<Float>(2018, 2020, false, true, new Float(r.nextInt(3)+5)));
                        dynamicWeight = new DynamicFloat(dynamicWeight, new Interval<Float>(2020, 2022, false, true, new Float(r.nextInt(3)+6)));
                        dynamicWeight = new DynamicFloat(dynamicWeight, new Interval<Float>(2022, 2024, false, true, new Float(r.nextInt(3)+7)));
                        dynamicWeight = new DynamicFloat(dynamicWeight, new Interval<Float>(2024, 2026, false, false, new Float(r.nextInt(3)+8)));
                        edgeDraft.addAttributeValue(weightCol, dynamicWeight);

                        container.addEdge(edgeDraft);
                    }
                }
            }
        }
    }

    public String getName() {
        return "Dynamic Graph Example";
    }

    public GeneratorUI getUI() {
        return null;
    }

    public boolean cancel() {
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
    }
}
