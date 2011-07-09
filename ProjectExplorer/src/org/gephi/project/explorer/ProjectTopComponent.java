/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
*/
package org.gephi.project.explorer;

import java.io.Serializable;
import java.util.logging.Logger;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Projects;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
final class ProjectTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static ProjectTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "ProjectTopComponent";
    private final ExplorerManager manager = new ExplorerManager();

    private ProjectTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(ProjectTopComponent.class, "CTL_ProjectTopComponent"));
        setToolTipText(NbBundle.getMessage(ProjectTopComponent.class, "HINT_ProjectTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        //associateLookup(ExplorerUtils.createLookup(manager, getActionMap()));

        initExplorer();
    }

    private void initExplorer() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        Projects projects = pc.getProjects();
        manager.setRootContext(new ProjectsNode(projects));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        view = new BeanTreeView();
        //((BeanTreeView)view).setRootVisible(false);

        setLayout(new java.awt.BorderLayout());
        add(view, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane view;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized ProjectTopComponent getDefault() {
        if (instance == null) {
            instance = new ProjectTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the ProjectTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ProjectTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(ProjectTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ProjectTopComponent) {
            return (ProjectTopComponent) win;
        }
        Logger.getLogger(ProjectTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        //initExplorer();
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    @Override
    protected void componentDeactivated() {
        //ExplorerUtils.activateActions(manager, false);
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        Projects projects = pc.getProjects();
        return new ResolvableHelper(projects);
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public ExplorerManager getExplorerManager() {
        return manager;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;
        private Projects projects;

        public ResolvableHelper(Projects projects) {
            this.projects = projects;
        }

        public Object readResolve() {
            ProjectTopComponent ptc = ProjectTopComponent.getDefault();
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            if (this.projects != null) {
                pc.setProjects(this.projects);
                ptc.initExplorer();
            //pc.getProjects().reinitLookup();
            }
            return ptc;
        }
    }
}