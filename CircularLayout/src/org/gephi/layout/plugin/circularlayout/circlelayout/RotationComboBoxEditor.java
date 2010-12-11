package org.gephi.layout.plugin.circularlayout.circlelayout;

import org.gephi.layout.plugin.circularlayout.dualcirclelayout.*;
import org.gephi.layout.plugin.circularlayout.abstractcombo.AbstractComboBoxEditor;
import org.openide.util.NbBundle;
import java.util.HashMap;

/**
 *
 * @author Matt
 */
public class RotationComboBoxEditor extends AbstractComboBoxEditor {

    public RotationComboBoxEditor() {
        HashMap<String,String> options = new HashMap<String,String>();
        options.put("CCW",NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.CCW"));
        options.put("CW",NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.CW"));
        super.ComboValues = options;
    }
}
