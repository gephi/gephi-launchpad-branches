
package org.gephi.layout.plugin.circularlayout;

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
public class CircleLayoutBuilder implements LayoutBuilder {

    private CircleLayoutUI ui = new CircleLayoutUI();

    @Override
    public String getName() {
        return NbBundle.getMessage(CircleLayoutBuilder.class, "CircleLayout.name");
    }

    @Override
    public Layout buildLayout() {
        return new CircleLayout(this, 500.0, false);
    }

    @Override
    public LayoutUI getUI() {
        return ui;
    }

    private static class CircleLayoutUI implements LayoutUI {

        @Override
        public String getDescription() {
            return NbBundle.getMessage(CircleLayoutBuilder.class, "CircleLayout.description");
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
