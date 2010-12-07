
package org.gephi.layout.plugin.dualcirclelayout;

import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Matt
 */
@ServiceProvider(service=LayoutBuilder.class)
public class DualCircleLayoutBuilder implements LayoutBuilder {

    private DualCircleLayoutUI ui = new DualCircleLayoutUI();

    @Override
    public String getName() {
        return NbBundle.getMessage(DualCircleLayoutBuilder.class, "DualCircleLayout.name");
    }

    @Override
    public Layout buildLayout() {
        return new DualCircleLayout(this, 4);
    }

    @Override
    public LayoutUI getUI() {
        return ui;
    }

    private static class DualCircleLayoutUI implements LayoutUI {

        @Override
        public String getDescription() {
            return NbBundle.getMessage(DualCircleLayoutBuilder.class, "DualCircleLayout.description");
        }

        @Override
        public Icon getIcon() {
            return null;
        }

        @Override
        public JPanel getSimplePanel(Layout layout) {
            return null;
        }

        @Override
        public int getQualityRank() {
            return -1;
        }

        @Override
        public int getSpeedRank() {
            return -1;
        }
    }
}
