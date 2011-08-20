/*
Copyright 2008-2010 Gephi
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
package org.gephi.desktop.visualization.components.contextmenu;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import org.gephi.datalab.spi.ContextMenuItemManipulator;
import org.gephi.desktop.visualization.components.NodeSettingsPanel;
import org.gephi.desktop.visualization.components.NodeShapeSelectionPanel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeShape;
import org.gephi.visualization.spi.BasicItem;
import org.gephi.visualization.spi.GraphContextMenuItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;


@ServiceProvider(service = GraphContextMenuItem.class)
public class SetNodeShape extends BasicItem {

    @Override
    public void execute() {}

    @Override
    public ContextMenuItemManipulator[] getSubItems() {
        if (nodes.length > 0) {
            List<GraphContextMenuItem> subItems = new ArrayList<GraphContextMenuItem>();
            int i = 1;
            // Add native shapes
            for (final NodeShape shape : NodeShape.nativeShapes()) {
                final int j = i++;
                subItems.add(new BasicItem() {
                    @Override
                    public void execute() {
                        for (Node node : nodes) {
                            node.getNodeData().setNodeShape(shape);
                        }
                    }

                    @Override
                    public String getName() {
                        return shape.toString();
                    }

                    @Override
                    public boolean canExecute() {
                        return true;
                    }

                    @Override
                    public int getType() {
                        return 100;
                    }

                    @Override
                    public int getPosition() {
                        return j;
                    }

                    @Override
                    public Icon getIcon() {
                        return null;
                    }
                });
            }
            // Add user imported image shape
            final int j = i;
            subItems.add(new BasicItem() {
                @Override
                public void execute() {
                    NodeShapeSelectionPanel panel = new NodeShapeSelectionPanel();
                    DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(NodeShapeSelectionPanel.class, "NodeShapeSelectionPanel.title"), true, NotifyDescriptor.OK_CANCEL_OPTION, null, null);
                    if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION) && panel.getSelectedNodeShape() != null) {
                        for (Node node : nodes) {
                            node.getNodeData().setNodeShape(panel.getSelectedNodeShape());
                        }
                    }
                }

                @Override
                public String getName() {
                    return NbBundle.getMessage(NodeSettingsPanel.class, "NodeSettingsPanel.browseImage");
                }

                @Override
                public boolean canExecute() {
                    return true;
                }

                @Override
                public int getType() {
                    return 100;
                }

                @Override
                public int getPosition() {
                    return j;
                }

                @Override
                public Icon getIcon() {
                    return null;
                }
            });
            return subItems.toArray(new GraphContextMenuItem[]{});
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(NodeShapeSelectionPanel.class, "GraphContextMenu_SetNodeShape");
    }

    @Override
    public boolean canExecute() {
        return nodes.length > 0;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getPosition() {
        return 100;
    }

    @Override
    public Icon getIcon() {
        return null;
    }
    
}
