/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.visualization.apiimpl.contextmenuitems;

import java.awt.event.KeyEvent;
import javax.swing.Icon;
import org.gephi.datalab.api.GraphElementsController;
import org.gephi.visualization.spi.GraphContextMenuItem;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 */
@ServiceProvider(service = GraphContextMenuItem.class)
public class Delete extends BasicItem {

    public void execute() {
        NotifyDescriptor.Confirmation notifyDescriptor = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(Delete.class, "GraphContextMenu.Delete.message"),
                NbBundle.getMessage(Delete.class, "GraphContextMenu.Delete.message.title"), NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(notifyDescriptor).equals(NotifyDescriptor.YES_OPTION)) {
            GraphElementsController gec = Lookup.getDefault().lookup(GraphElementsController.class);
            gec.deleteNodes(nodes);
        }
    }

    public String getName() {
        return NbBundle.getMessage(Delete.class, "GraphContextMenu_Delete");
    }

    public boolean canExecute() {
        return nodes.length > 0;
    }

    public int getType() {
        return 200;
    }

    public int getPosition() {
        return 0;
    }

    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/visualization/api/resources/delete.png", false);
    }

    @Override
    public Integer getMnemonicKey() {
        return KeyEvent.VK_D;
    }
}
