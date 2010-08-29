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
package org.gephi.ui.partition.plugin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import net.java.dev.colorchooser.ColorChooser;
import org.gephi.partition.api.Part;
import org.gephi.partition.api.Partition;
import org.gephi.partition.plugin.EdgeColorTransformer;
import org.gephi.partition.spi.Transformer;
import org.gephi.utils.PaletteUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class EdgeColorTransformerPanel extends javax.swing.JPanel {

    private EdgeColorTransformer edgeColorTransformer;
    private Partition partition;
    private JPopupMenu popupMenu;

    public EdgeColorTransformerPanel() {
        net.miginfocom.swing.MigLayout migLayout1 = new net.miginfocom.swing.MigLayout();
        migLayout1.setColumnConstraints("[pref!]20[pref!]");
        setLayout(migLayout1);

        initComponents();
        createPopup();
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (edgeColorTransformer != null) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public void setup(Partition partition, Transformer transformer) {
        removeAll();
        edgeColorTransformer = (EdgeColorTransformer) transformer;
        if (edgeColorTransformer.getMap().isEmpty()) {
            List<Color> colors = PaletteUtils.getSequenceColors(partition.getPartsCount());
            int i = 0;
            for (Part p : partition.getParts()) {
                edgeColorTransformer.getMap().put(p.getValue(), colors.get(i));
                i++;
            }
        }

        this.partition = partition;
        for (final Part p : partition.getParts()) {
            JLabel partLabel = new JLabel(p.getDisplayName());
            add(partLabel);
            final ColorChooser colorChooser = new ColorChooser(edgeColorTransformer.getMap().get(p.getValue()));
            colorChooser.setPreferredSize(new Dimension(16, 16));
            colorChooser.setMaximumSize(new Dimension(16, 16));
            colorChooser.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    edgeColorTransformer.getMap().put(p.getValue(), colorChooser.getColor());
                }
            });
            add(colorChooser, "wrap");
        }
    }

    private void createPopup() {
        popupMenu = new JPopupMenu();
        JMenuItem randomizeItem = new JMenuItem(NbBundle.getMessage(EdgeColorTransformerPanel.class, "EdgeColorTransformerPanel.action.randomize"));
        randomizeItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                edgeColorTransformer.getMap().clear();
                setup(partition, edgeColorTransformer);
                revalidate();
                repaint();
            }
        });
        popupMenu.add(randomizeItem);
        JMenuItem allBlackItem = new JMenuItem(NbBundle.getMessage(NodeColorTransformerPanel.class, "EdgeColorTransformerPanel.action.allBlacks"));
        allBlackItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                for (Entry<Object, Color> entry : edgeColorTransformer.getMap().entrySet()) {
                    entry.setValue(Color.BLACK);
                }
                setup(partition, edgeColorTransformer);
                revalidate();
                repaint();
            }
        });
        popupMenu.add(allBlackItem);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
