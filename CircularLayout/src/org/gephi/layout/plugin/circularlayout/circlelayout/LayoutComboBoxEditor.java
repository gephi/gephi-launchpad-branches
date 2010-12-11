package org.gephi.layout.plugin.circularlayout.circlelayout;

import org.gephi.layout.plugin.circularlayout.abstractcombo.AbstractComboBoxEditor;
import org.openide.util.NbBundle;
import java.util.HashMap;

/**
 *
 * @author Matt
 */
public class LayoutComboBoxEditor extends AbstractComboBoxEditor {

    public LayoutComboBoxEditor() {
        HashMap<String,String> options = new HashMap<String,String>();
        options.put("1",NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.NodeID.name"));
        options.put("2",NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Random.name"));
        options.put("3",NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Degree.name"));
        super.ComboValues = options;
    }
}
