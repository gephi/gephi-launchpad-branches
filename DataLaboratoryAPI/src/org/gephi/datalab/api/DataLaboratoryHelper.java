/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalab.api;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.spi.DialogControls;
import org.gephi.datalab.spi.Manipulator;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulator;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulatorUI;
import org.gephi.datalab.spi.columns.merge.AttributeColumnsMergeStrategy;
import org.gephi.datalab.spi.columns.merge.AttributeColumnsMergeStrategyBuilder;
import org.gephi.datalab.spi.values.AttributeValueManipulator;
import org.gephi.datalab.spi.values.AttributeValueManipulatorBuilder;
import org.gephi.datalab.spi.edges.EdgesManipulator;
import org.gephi.datalab.spi.edges.EdgesManipulatorBuilder;
import org.gephi.datalab.spi.general.GeneralActionsManipulator;
import org.gephi.datalab.spi.general.PluginGeneralActionsManipulator;
import org.gephi.datalab.spi.nodes.NodesManipulator;
import org.gephi.datalab.spi.nodes.NodesManipulatorBuilder;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Helper class for simplifying the use of Data Laboratory API and SPI.
 */
@ServiceProvider(service=DataLaboratoryHelper.class)
public class DataLaboratoryHelper{

    /**
     * <p>Prepares an array with one new instance of every NodesManipulator
     * that has a builder registered and returns it.</p>
     * <p>It also returns the manipulators ordered first by type and then by position.</p>
     * @return Array of all NodesManipulator implementations
     */
    public NodesManipulator[] getNodesManipulators() {
        ArrayList<NodesManipulator> nodesManipulators = new ArrayList<NodesManipulator>();
        for (NodesManipulatorBuilder nm : Lookup.getDefault().lookupAll(NodesManipulatorBuilder.class)) {
            nodesManipulators.add(nm.getNodesManipulator());
        }
        sortManipulators(nodesManipulators);
        return nodesManipulators.toArray(new NodesManipulator[0]);
    }

    /**
     * <p>Prepares an array with one new instance of every EdgesManipulator
     * that has a builder registered and returns it.</p>
     * <p>It also returns the manipulators ordered first by type and then by position.</p>
     * @return Array of all EdgesManipulator implementations
     */
    public EdgesManipulator[] getEdgesManipulators() {
        ArrayList<EdgesManipulator> edgesManipulators = new ArrayList<EdgesManipulator>();
        for (EdgesManipulatorBuilder em : Lookup.getDefault().lookupAll(EdgesManipulatorBuilder.class)) {
            edgesManipulators.add(em.getEdgesManipulator());
        }
        sortManipulators(edgesManipulators);
        return edgesManipulators.toArray(new EdgesManipulator[0]);
    }

    /**
     * <p>Prepares an array with one instance of every GeneralActionsManipulator that is registered.</p>
     * <p>It also returns the manipulators ordered first by type and then by position.</p>
     * @return Array of all GeneralActionsManipulator implementations
     */
    public GeneralActionsManipulator[] getGeneralActionsManipulators() {
        ArrayList<GeneralActionsManipulator> generalActionsManipulators = new ArrayList<GeneralActionsManipulator>();
        generalActionsManipulators.addAll(Lookup.getDefault().lookupAll(GeneralActionsManipulator.class));
        sortManipulators(generalActionsManipulators);
        return generalActionsManipulators.toArray(new GeneralActionsManipulator[0]);
    }

    /**
     * <p>Prepares an array with one instance of every PluginGeneralActionsManipulator that is registered.</p>
     * <p>It also returns the manipulators ordered first by type and then by position.</p>
     * @return Array of all PluginGeneralActionsManipulator implementations
     */
    public PluginGeneralActionsManipulator[] getPluginGeneralActionsManipulators() {
        ArrayList<PluginGeneralActionsManipulator> pluginGeneralActionsManipulators = new ArrayList<PluginGeneralActionsManipulator>();
        pluginGeneralActionsManipulators.addAll(Lookup.getDefault().lookupAll(PluginGeneralActionsManipulator.class));
        sortManipulators(pluginGeneralActionsManipulators);
        return pluginGeneralActionsManipulators.toArray(new PluginGeneralActionsManipulator[0]);
    }

    /**
     * <p>Prepares an array that has one instance of every AttributeColumnsManipulator implementation
     * that has a builder registered and returns it.</p>
     * <p>It also returns the manipulators ordered first by type and then by position.</p>
     * @return Array of all AttributeColumnsManipulator implementations
     */
    public AttributeColumnsManipulator[] getAttributeColumnsManipulators() {
        ArrayList<AttributeColumnsManipulator> attributeColumnsManipulators = new ArrayList<AttributeColumnsManipulator>();
        attributeColumnsManipulators.addAll(Lookup.getDefault().lookupAll(AttributeColumnsManipulator.class));
        sortAttributeColumnsManipulators(attributeColumnsManipulators);
        return attributeColumnsManipulators.toArray(new AttributeColumnsManipulator[0]);
    }

    /**
     * <p>Prepares an array with one new instance of every AttributeValueManipulator
     * that has a builder registered and returns it.</p>
     * <p>It also returns the manipulators ordered first by type and then by position.</p>
     * @return Array of all AttributeValueManipulator implementations
     */
    public AttributeValueManipulator[] getAttributeValueManipulators() {
        ArrayList<AttributeValueManipulator> attributeValueManipulators = new ArrayList<AttributeValueManipulator>();
        for (AttributeValueManipulatorBuilder am : Lookup.getDefault().lookupAll(AttributeValueManipulatorBuilder.class)) {
            attributeValueManipulators.add(am.getAttributeValueManipulator());
        }
        sortManipulators(attributeValueManipulators);
        return attributeValueManipulators.toArray(new AttributeValueManipulator[0]);
    }

    /**
     * <p>Prepares an array that has one new instance of every AttributeColumnsMergeStrategy implementation that is registered.</p>
     * <p>It also returns the manipulators ordered first by type and then by position.</p>
     * @return Array of all AttributeColumnsMergeStrategy implementations
     */
    public AttributeColumnsMergeStrategy[] getAttributeColumnsMergeStrategies() {
        ArrayList<AttributeColumnsMergeStrategy> strategies = new ArrayList<AttributeColumnsMergeStrategy>();
        for (AttributeColumnsMergeStrategyBuilder cs : Lookup.getDefault().lookupAll(AttributeColumnsMergeStrategyBuilder.class)) {
            strategies.add(cs.getAttributeColumnsMergeStrategy());
        }
        sortManipulators(strategies);
        return strategies.toArray(new AttributeColumnsMergeStrategy[0]);
    }

    private void sortManipulators(ArrayList<? extends Manipulator> m) {
        Collections.sort(m, new Comparator<Manipulator>() {

            public int compare(Manipulator o1, Manipulator o2) {
                //Order by type, position.
                if (o1.getType() == o2.getType()) {
                    return o1.getPosition() - o2.getPosition();
                } else {
                    return o1.getType() - o2.getType();
                }
            }
        });
    }

    private void sortAttributeColumnsManipulators(ArrayList<? extends AttributeColumnsManipulator> m) {
        Collections.sort(m, new Comparator<AttributeColumnsManipulator>() {

            public int compare(AttributeColumnsManipulator o1, AttributeColumnsManipulator o2) {
                //Order by type, position.
                if (o1.getType() == o2.getType()) {
                    return o1.getPosition() - o2.getPosition();
                } else {
                    return o1.getType() - o2.getType();
                }
            }
        });
    }

    /**
     * Prepares the dialog UI of a manipulator if it has one and executes the manipulator in a separate
     * Thread when the dialog is accepted or directly if there is no UI.
     * @param m Manipulator to execute
     */
    public void executeManipulator(final Manipulator m) {
        if (m.canExecute()) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    final ManipulatorUI ui = m.getUI();
                    //Show a dialog for the manipulator UI if it provides one. If not, execute the manipulator directly:
                    if (ui != null) {
                        final JButton okButton = new JButton(NbBundle.getMessage(DataLaboratoryHelper.class, "DataLaboratoryHelper.ui.okButton.text"));
                        DialogControls dialogControls = new DialogControlsImpl(okButton);
                        ui.setup(m, dialogControls);
                        JPanel settingsPanel = ui.getSettingsPanel();
                        DialogDescriptor dd = new DialogDescriptor(settingsPanel, NbBundle.getMessage(DataLaboratoryHelper.class, "SettingsPanel.title", ui.getDisplayName()), ui.isModal(), new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                if (e.getSource().equals(okButton)) {
                                    ui.unSetup();
                                    executeManipulatorInOtherThread(m);
                                } else {
                                    ui.unSetup();
                                }
                            }
                        });
                        dd.setOptions(new Object[]{okButton, DialogDescriptor.CANCEL_OPTION});
                        dd.setClosingOptions(null);//All options close
                        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
                        dialog.addWindowListener(new WindowAdapter() {

                            @Override
                            public void windowClosing(WindowEvent e) {
                                ui.unSetup();
                            }
                        });
                        dialog.setVisible(true);
                    } else {
                        executeManipulatorInOtherThread(m);
                    }
                }
            });
        }
    }

    private void executeManipulatorInOtherThread(final Manipulator m) {
        new Thread() {

            @Override
            public void run() {
                m.execute();
            }
        }.start();
    }

    /**
     * Prepares the dialog UI of a AttributeColumnsManipulator if it has one and executes the manipulator in a separate
     * Thread when the dialog is accepted or directly if there is no UI.
     * @param m AttributeColumnsManipulator
     * @param table Table of the column
     * @param column Column to manipulate
     */
    public void executeAttributeColumnsManipulator(final AttributeColumnsManipulator m, final AttributeTable table, final AttributeColumn column) {
        if (m.canManipulateColumn(table, column)) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    final AttributeColumnsManipulatorUI ui = m.getUI(table, column);
                    //Show a dialog for the manipulator UI if it provides one. If not, execute the manipulator directly:
                    if (ui != null) {
                        final JButton okButton = new JButton(NbBundle.getMessage(DataLaboratoryHelper.class, "DataLaboratoryHelper.ui.okButton.text"));
                        DialogControls dialogControls = new DialogControlsImpl(okButton);
                        ui.setup(m, table, column, dialogControls);
                        JPanel settingsPanel = ui.getSettingsPanel();
                        DialogDescriptor dd = new DialogDescriptor(settingsPanel, NbBundle.getMessage(DataLaboratoryHelper.class, "SettingsPanel.title", ui.getDisplayName()), ui.isModal(), new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                if (e.getSource().equals(okButton)) {
                                    ui.unSetup();
                                    executeAttributeColumnsManipulatorInOtherThread(m, table, column);
                                } else {
                                    ui.unSetup();
                                }
                            }
                        });
                        dd.setOptions(new Object[]{okButton, DialogDescriptor.CANCEL_OPTION});
                        dd.setClosingOptions(null);//All options close
                        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
                        dialog.addWindowListener(new WindowAdapter() {

                            @Override
                            public void windowClosing(WindowEvent e) {
                                ui.unSetup();
                            }
                        });
                        dialog.setVisible(true);
                    } else {
                        executeAttributeColumnsManipulatorInOtherThread(m, table, column);
                    }
                }
            });
        }
    }

    private void executeAttributeColumnsManipulatorInOtherThread(final AttributeColumnsManipulator m, final AttributeTable table, final AttributeColumn column) {
        new Thread() {

            @Override
            public void run() {
                m.execute(table, column);
            }
        }.start();
    }

    /**
     * Returns the AttributeColumnsMergeStrategy with that class name or null if it does not exist
     */
    public NodesManipulator getNodesManipulatorByName(String name){
        for (NodesManipulatorBuilder nm : Lookup.getDefault().lookupAll(NodesManipulatorBuilder.class)) {
            if(nm.getNodesManipulator().getClass().getSimpleName().equals(name)){
                return nm.getNodesManipulator();
            }
        }
        return null;
    }

    /**
     * Returns the AttributeColumnsMergeStrategy with that class name or null if it does not exist
     */
    public EdgesManipulator getEdgesManipulatorByName(String name){
        for (EdgesManipulatorBuilder nm : Lookup.getDefault().lookupAll(EdgesManipulatorBuilder.class)) {
            if(nm.getEdgesManipulator().getClass().getSimpleName().equals(name)){
                return nm.getEdgesManipulator();
            }
        }
        return null;
    }

    /**
     * Returns the AttributeColumnsMergeStrategy with that class name or null if it does not exist
     */
    public GeneralActionsManipulator getGeneralActionsManipulatorByName(String name) {
        for (GeneralActionsManipulator m : Lookup.getDefault().lookupAll(GeneralActionsManipulator.class)) {
            if(m.getClass().getSimpleName().equals(name)){
                return m;
            }
        }
        return null;
    }

    /**
     * Returns the AttributeColumnsMergeStrategy with that class name or null if it does not exist
     */
    public PluginGeneralActionsManipulator getPluginGeneralActionsManipulatorByName(String name) {
        for (PluginGeneralActionsManipulator m : Lookup.getDefault().lookupAll(PluginGeneralActionsManipulator.class)) {
            if(m.getClass().getSimpleName().equals(name)){
                return m;
            }
        }
        return null;
    }

    /**
     * Returns the AttributeColumnsMergeStrategy with that class name or null if it does not exist
     */
    public AttributeColumnsManipulator getAttributeColumnsManipulatorByName(String name) {
        for (AttributeColumnsManipulator m : Lookup.getDefault().lookupAll(AttributeColumnsManipulator.class)) {
            if(m.getClass().getSimpleName().equals(name)){
                return m;
            }
        }
        return null;
    }

    /**
     * Returns the AttributeColumnsMergeStrategy with that class name or null if it does not exist
     */
    public AttributeValueManipulator getAttributeValueManipulatorByName(String name) {
        for (AttributeValueManipulatorBuilder am : Lookup.getDefault().lookupAll(AttributeValueManipulatorBuilder.class)) {
            if(am.getAttributeValueManipulator().getClass().getSimpleName().equals(name)){
                return am.getAttributeValueManipulator();
            }
        }
        return null;
    }

    /**
     * Returns the AttributeColumnsMergeStrategy with that class name or null if it does not exist
     */
    public AttributeColumnsMergeStrategy getAttributeColumnsMergeStrategieByName(String name) {
        for (AttributeColumnsMergeStrategyBuilder cs : Lookup.getDefault().lookupAll(AttributeColumnsMergeStrategyBuilder.class)) {
            if(cs.getAttributeColumnsMergeStrategy().getClass().getSimpleName().equals(name)){
                return cs.getAttributeColumnsMergeStrategy();
            }
        }
        return null;
    }

    class DialogControlsImpl implements DialogControls {

        JComponent okButton;

        public DialogControlsImpl(JComponent okButton) {
            this.okButton = okButton;
        }

        public void setOkButtonEnabled(boolean enabled) {
            okButton.setEnabled(enabled);
        }

        public boolean isOkButtonEnabled(){
            return okButton.isEnabled();
        }
    }

    public static DataLaboratoryHelper getDefault(){
        return Lookup.getDefault().lookup(DataLaboratoryHelper.class);
    }
}
