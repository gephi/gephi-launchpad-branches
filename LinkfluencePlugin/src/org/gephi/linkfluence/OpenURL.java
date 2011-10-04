/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Eduardo Ramos <eduramiba@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.linkfluence;

import javax.swing.Icon;
import org.gephi.datalab.spi.ContextMenuItemManipulator;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.nodes.NodesManipulator;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.visualization.spi.GraphContextMenuItem;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 */
@ServiceProvider(service = GraphContextMenuItem.class)
public class OpenURL implements GraphContextMenuItem, NodesManipulator{

    private Node node;

    public void setup(Node[] nodes, Node clickedNode) {
        setup(null, nodes);
    }

    @Override
    public void setup(HierarchicalGraph graph, Node[] nodes) {
        if (nodes.length == 1) {
            node = nodes[0];
        }else{
            node=null;
        }
    }

    public void execute() {
    }

    @Override
    public ContextMenuItemManipulator[] getSubItems() {
        GraphContextMenuItem[] subItems=new GraphContextMenuItem[2];

        //Always provide subitems so their shortcuts are available:
        subItems[0]=new OpenURLLastItem();
        subItems[1]=new OpenURLSubItem();
        return subItems;
    }

    public String getName() {
        return NbBundle.getMessage(OpenURL.class, "GraphContextMenu_OpenURL");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(OpenURL.class, "GraphContextMenu_OpenURL.description");
    }

    @Override
    public boolean isAvailable() {
        return node != null;
    }

    public boolean canExecute() {
        return true;
    }

    public int getType() {
        return 400;
    }

    public int getPosition() {
        return 300;
    }

    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/linkfluence/resources/globe-network.png", false);
    }

    public Integer getMnemonicKey() {
        return null;
    }

    public ManipulatorUI getUI() {
        return null;
    }
}
