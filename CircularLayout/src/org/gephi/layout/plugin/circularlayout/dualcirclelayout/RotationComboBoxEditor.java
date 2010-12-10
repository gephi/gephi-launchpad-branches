package org.gephi.layout.plugin.circularlayout.dualcirclelayout;

import org.gephi.layout.plugin.circularlayout.abstractcombo.AbstractComboBoxEditor;
import org.openide.util.NbBundle;
import java.util.HashMap;

/**
 *
 * @author Matt
 */
public class RotationComboBoxEditor extends AbstractComboBoxEditor {

    public RotationComboBoxEditor() {
        HashMap options = new HashMap();
        options.put("CCW",NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.CCW"));
        options.put("CW",NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.CW"));
        super.ComboValues = options;
    }
}
