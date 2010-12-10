/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gephi.layout.plugin.circularlayout.circlelayout;

import org.gephi.layout.plugin.circularlayout.abstractcombo.AbstractComboBoxEditor;
import org.openide.util.NbBundle;
import java.util.HashMap;

public class NodeOrderComboBoxEditor extends AbstractComboBoxEditor {

    public NodeOrderComboBoxEditor() {
        HashMap options = new HashMap();
        options.put("CCW",NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Random.name"));
        options.put("CW",NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Degree.name"));
        options.put("CW",NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.NodeID.name"));
        super.ComboValues = options;
    }
}
