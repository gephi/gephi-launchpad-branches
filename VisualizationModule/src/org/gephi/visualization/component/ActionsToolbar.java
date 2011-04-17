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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.ui.components.JColorButton;
import org.gephi.ui.utils.UIUtils;
import org.gephi.visualization.VizController;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class ActionsToolbar extends JToolBar {

    //Settings
    private Color color = new Color(0.6f, 0.6f, 0.6f);
    private float size = 1f;

    public ActionsToolbar() {
        initDesign();
        initContent();
    }

    private void initContent() {

        //Center on graph
        final JButton centerOnGraphButton = new JButton();
        centerOnGraphButton.setToolTipText(NbBundle.getMessage(VizBarController.class, "ActionsToolbar.centerOnGraph"));
        centerOnGraphButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/centerOnGraph.png")));
        centerOnGraphButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                VizController.getInstance().getGraphIO().centerOnGraph();
            }
        });
        add(centerOnGraphButton);

        //Center on zero
        final JButton centerOnZeroButton = new JButton();
        centerOnZeroButton.setToolTipText(NbBundle.getMessage(VizBarController.class, "ActionsToolbar.centerOnZero"));
        centerOnZeroButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/centerOnZero.png")));
        centerOnZeroButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                VizController.getInstance().getGraphIO().centerOnZero();
            }
        });
        add(centerOnZeroButton);

        //Reset colors
        final JColorButton resetColorButton = new JColorButton(color, true, false);
        resetColorButton.setToolTipText(NbBundle.getMessage(ActionsToolbar.class, "ActionsToolbar.resetColors"));
        resetColorButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                color = resetColorButton.getColor();
                GraphController gc = Lookup.getDefault().lookup(GraphController.class);
                Graph graph = gc.getModel().getGraphVisible();
                for (Node n : graph.getNodes().toArray()) {
                    n.getNodeData().setR(color.getRed() / 255f);
                    n.getNodeData().setG(color.getGreen() / 255f);
                    n.getNodeData().setB(color.getBlue() / 255f);
                    n.getNodeData().setAlpha(1f);
                }
                for (Edge e : graph.getEdges().toArray()) {
                    e.getEdgeData().setR(-1f);
                    e.getEdgeData().setG(color.getGreen() / 255f);
                    e.getEdgeData().setB(color.getBlue() / 255f);
                    e.getEdgeData().setAlpha(1f);
                }
            }
        });
        add(resetColorButton);

        //Reset sizes
        final JButton resetSizeButton = new JButton();
        resetSizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/resetSize.png")));
        resetSizeButton.setToolTipText(NbBundle.getMessage(ActionsToolbar.class, "ActionsToolbar.resetSizes"));
        resetSizeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GraphController gc = Lookup.getDefault().lookup(GraphController.class);
                Graph graph = gc.getModel().getGraphVisible();
                for (Node n : graph.getNodes().toArray()) {
                    n.getNodeData().setSize(size);
                }
            }
        });
        resetSizeButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (SwingUtilities.isRightMouseButton(e)) {
                    Object res = JOptionPane.showInputDialog(resetSizeButton, NbBundle.getMessage(ActionsToolbar.class, "ActionsToolbar.resetSizes.dialog"), "" + size);
                    if (res != null) {
                        try {
                            size = Float.parseFloat((String) res);
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        });
        add(resetSizeButton);

        //Reset label colors
        final JButton resetLabelColorButton = new JButton();
        resetLabelColorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/resetLabelColor.png")));
        resetLabelColorButton.setToolTipText(NbBundle.getMessage(ActionsToolbar.class, "ActionsToolbar.resetLabelColors"));
        resetLabelColorButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                GraphController gc = Lookup.getDefault().lookup(GraphController.class);
                Graph graph = gc.getModel().getGraphVisible();
                for (Node n : graph.getNodes().toArray()) {
                    n.getNodeData().getTextData().setColor(null);
                }
                for (Edge e : graph.getEdges().toArray()) {
                    e.getEdgeData().getTextData().setColor(null);
                }
            }
        });
        add(resetLabelColorButton);

        //Reset label colors
        final JButton resetLabelVisibleButton = new JButton();
        resetLabelVisibleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/resetLabelVisible.png")));
        resetLabelVisibleButton.setToolTipText(NbBundle.getMessage(ActionsToolbar.class, "ActionsToolbar.resetLabelVisible"));
        resetLabelVisibleButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                GraphController gc = Lookup.getDefault().lookup(GraphController.class);
                Graph graph = gc.getModel().getGraphVisible();
                for (Node n : graph.getNodes().toArray()) {
                    n.getNodeData().getTextData().setVisible(true);
                }
                for (Edge e : graph.getEdges().toArray()) {
                    e.getEdgeData().getTextData().setVisible(true);
                }
            }
        });
        add(resetLabelVisibleButton);

        //Reset label size
        JButton resetLabelSizeButton = new JButton();
        resetLabelSizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/resetLabelSize.png")));
        resetLabelSizeButton.setToolTipText(NbBundle.getMessage(ActionsToolbar.class, "ActionsToolbar.resetLabelSizes"));
        resetLabelSizeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GraphController gc = Lookup.getDefault().lookup(GraphController.class);
                Graph graph = gc.getModel().getGraphVisible();
                for (Node n : graph.getNodes().toArray()) {
                    n.getNodeData().getTextData().setSize(1f);
                }
            }
        });
        add(resetLabelSizeButton);
    }

    private void initDesign() {
        setFloatable(false);
        setOrientation(JToolBar.VERTICAL);
        putClientProperty("JToolBar.isRollover", Boolean.TRUE); //NOI18N
        setBorder(BorderFactory.createEmptyBorder(0, 2, 15, 2));
        setOpaque(false);
    }

    @Override
    public void setEnabled(final boolean enabled) {
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
