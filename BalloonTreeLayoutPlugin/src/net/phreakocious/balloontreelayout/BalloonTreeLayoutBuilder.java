package net.phreakocious.balloontreelayout;

import javax.swing.Icon;
import javax.swing.JPanel;
//import org.gephi.layout.plugin.tree.TreeLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=LayoutBuilder.class)
public class BalloonTreeLayoutBuilder implements LayoutBuilder {

    public String getName() {
        return NbBundle.getMessage(BalloonTreeLayoutBuilder.class, "name");
    }

    public LayoutUI getUI() {
        return new BalloonTreeLayoutUI();
    }

    public Layout buildLayout() {
        return new BalloonTreeLayout(this);
    }

    private class BalloonTreeLayoutUI implements LayoutUI {

        public String getDescription() {
            return NbBundle.getMessage(BalloonTreeLayoutBuilder.class, "description");
        }

        public Icon getIcon() {
            return null;
        }

        public JPanel getSimplePanel(Layout layout) {
            return null;
        }

        public int getQualityRank() {
            return -1;
        }

        public int getSpeedRank() {
            return -1;
        }
    }
}
