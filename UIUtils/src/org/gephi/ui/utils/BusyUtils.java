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
package org.gephi.ui.utils;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Mathieu Bastian
 */
public class BusyUtils {

    /**
     * Creates a new <code>JXBusyLabel</code> wrapper and set it at the center of <code>scrollPane</code>. When users
     * calls <code>BusyLabel.setBusy(false)</code>, the label is removed from <code>scrollPanel</code> and
     * <code>component</code> is set instead.
     * @param scrollPane the scroll Panel where the label is to be put
     * @param text the text set to the newly created label
     * @param component the component to set in <code>scrollPane</code> when it is not busy anymore
     * @return the newly created <code>JXBusyLabel</code> wrapper
     */
    public static BusyLabel createCenteredBusyLabel(JScrollPane scrollPane, String text, JComponent component) {
        return new BusyLabel(scrollPane, text, component);
    }

    public static class BusyLabel {

        private JScrollPane scrollPane;
        private JXBusyLabel busyLabel;
        private JComponent component;

        private BusyLabel(JScrollPane scrollpane, String text, JComponent component) {
            this.scrollPane = scrollpane;
            this.component = component;
            busyLabel = new JXBusyLabel(new Dimension(20, 20));
            busyLabel.setText(text);
            busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        public void setBusy(boolean busy) {
            if (busy) {
                if (scrollPane != null) {
                    scrollPane.setViewportView(busyLabel);
                }

                busyLabel.setBusy(true);
            } else {
                busyLabel.setBusy(false);
                if (scrollPane != null) {
                    scrollPane.setViewportView(component);
                }
            }
        }

        public void setBusy(boolean busy, JComponent component) {
            this.component = component;
            setBusy(busy);
        }
    }
}
