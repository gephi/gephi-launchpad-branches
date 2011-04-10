/*
Copyright 2008-2010 Gephi
Authors : Jérémy Subtil <jeremy.subtil@gephi.org>
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

package org.gephi.desktop.preview;

import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.SupervisorPropery;
import org.gephi.preview.api.supervisors.EdgeSupervisor;
import org.gephi.preview.api.supervisors.GlobalEdgeSupervisor;
import org.gephi.preview.api.supervisors.NodeSupervisor;
import org.gephi.preview.api.supervisors.SelfLoopSupervisor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * This class provides some sets of properties for the preview UI.
 *
 * @author Jérémy Subtil <jeremy.subtil@gephi.org>
 */
public class PreviewNode extends AbstractNode {

    public PreviewNode() {
        super(Children.LEAF);
        setDisplayName(NbBundle.getMessage(PreviewNode.class, "PreviewNode.displayName"));
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        PreviewController controller = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel model = controller.getModel();
        if (model != null) {
            NodeSupervisor ns = model.getNodeSupervisor();
            GlobalEdgeSupervisor ges = model.getGlobalEdgeSupervisor();
            SelfLoopSupervisor sls = model.getSelfLoopSupervisor();
            EdgeSupervisor unes = model.getUndirectedEdgeSupervisor();
            EdgeSupervisor ues = model.getUniEdgeSupervisor();
            EdgeSupervisor bes = model.getBiEdgeSupervisor();

            Sheet.Set nodeSet = Sheet.createPropertiesSet();
            nodeSet.setDisplayName(NbBundle.getMessage(PreviewNode.class, "PreviewNode.Node.displayName"));
            nodeSet.setName("nodes");

            Sheet.Set edgeSet = Sheet.createPropertiesSet();
            edgeSet.setDisplayName(NbBundle.getMessage(PreviewNode.class, "PreviewNode.Edge.displayName"));
            edgeSet.setName("edges");

            Sheet.Set selfLoopSet = Sheet.createPropertiesSet();
            selfLoopSet.setDisplayName(NbBundle.getMessage(PreviewNode.class, "PreviewNode.SelfLoop.displayName"));
            selfLoopSet.setName("selfLoops");

            Sheet.Set undirectedEdgeSet = Sheet.createPropertiesSet();
            undirectedEdgeSet.setDisplayName(NbBundle.getMessage(PreviewNode.class, "PreviewNode.Undirected.displayName"));
            undirectedEdgeSet.setName("undirectedEdges");

            Sheet.Set uniEdgeSet = Sheet.createPropertiesSet();
            uniEdgeSet.setDisplayName(NbBundle.getMessage(PreviewNode.class, "PreviewNode.Directed.displayName"));
            uniEdgeSet.setName("uniEdges");

            Sheet.Set biEdgeSet = Sheet.createPropertiesSet();
            biEdgeSet.setDisplayName(NbBundle.getMessage(PreviewNode.class, "PreviewNode.Mutual.displayName"));
            biEdgeSet.setName("biEdges");

            for (SupervisorPropery p : ns.getProperties()) {
                nodeSet.put(p.getProperty());
            }

            for (SupervisorPropery p : ges.getProperties()) {
                edgeSet.put(p.getProperty());
            }

            for (SupervisorPropery p : sls.getProperties()) {
                selfLoopSet.put(p.getProperty());
            }

            for (SupervisorPropery p : unes.getProperties()) {
                undirectedEdgeSet.put(p.getProperty());
            }

            for (SupervisorPropery p : ues.getProperties()) {
                uniEdgeSet.put(p.getProperty());
            }

            for (SupervisorPropery p : bes.getProperties()) {
                biEdgeSet.put(p.getProperty());
            }

            sheet.put(nodeSet);
            sheet.put(edgeSet);
            sheet.put(selfLoopSet);
            sheet.put(undirectedEdgeSet);
            sheet.put(uniEdgeSet);
            sheet.put(biEdgeSet);
        }
        return sheet;
    }

    private PropertySupport.Reflection createProperty(Object o, Class type, String method, String name, String displayName) throws NoSuchMethodException {
        PropertySupport.Reflection p = new PropertySupport.Reflection(o, type, method);
        p.setName(name);
        p.setDisplayName(displayName);
        return p;
    }

    private PropertySupport.Reflection createProperty(Object o, Class type, String method, String name, String displayName, Class editor) throws NoSuchMethodException {
        PropertySupport.Reflection p = createProperty(o, type, method, name, displayName);
        p.setPropertyEditorClass(editor);
        return p;
    }
}
