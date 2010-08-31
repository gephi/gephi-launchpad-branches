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
package org.gephi.visualization.component;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.gephi.ui.utils.UIUtils;

/**
 *
 * @author Mathieu Bastian
 */
public class VizToolbar extends JToolBar {

    public VizToolbar(VizToolbarGroup[] groups) {
        initDesign();

        for (VizToolbarGroup g : groups) {
            addSeparator();
            for (JComponent c : g.getToolbarComponents()) {
                add(c);
            }
        }
    }

    private void initDesign() {
        setFloatable(false);
        putClientProperty("JToolBar.isRollover", Boolean.TRUE); //NOI18N
        setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
        if (UIUtils.isAquaLookAndFeel()) {
            setBackground(UIManager.getColor("NbExplorerView.background"));
        }
    }

    public void setEnable(final boolean enabled) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                for (Component c : getComponents()) {
                    c.setEnabled(enabled);
                }
            }
        });
    }

    @Override
    public Component add(Component comp) {
        if (comp instanceof JButton) {
            UIUtils.fixButtonUI((JButton) comp);
        }

        return super.add(comp);
    }
}
