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
package org.gephi.project.io;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.gephi.project.impl.ProjectImpl;
import org.gephi.project.impl.ProjectInformationImpl;
import org.gephi.project.impl.WorkspaceProviderImpl;
import org.gephi.project.api.Project;
import org.gephi.project.api.Workspace;
import org.gephi.workspace.impl.WorkspaceImpl;
import org.gephi.workspace.impl.WorkspaceInformationImpl;
import org.gephi.project.spi.WorkspacePersistenceProvider;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mathieu Bastian
 */
public class GephiReader implements Cancellable {

    private ProjectImpl project;
    private boolean cancel = false;
    private Map<String, WorkspacePersistenceProvider> providers;

    public GephiReader() {
        providers = new LinkedHashMap<String, WorkspacePersistenceProvider>();
        for (WorkspacePersistenceProvider w : Lookup.getDefault().lookupAll(WorkspacePersistenceProvider.class)) {
            try {
                String id = w.getIdentifier();
                if (id != null && !id.isEmpty()) {
                    providers.put(w.getIdentifier(), w);
                }
            } catch (Exception e) {
            }
        }
    }

    public boolean cancel() {
        cancel = true;
        return true;
    }

    public Project readAll(Element root, Project project) throws Exception {
        //XPath
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        //Calculate the task max
        readCore(xpath, root);

        //Project
        this.project = (ProjectImpl) project;
        XPathExpression exp = xpath.compile("./project");
        Element projectE = (Element) exp.evaluate(root, XPathConstants.NODE);
        readProject(xpath, projectE);
        return project;
    }

    public void readCore(XPath xpath, Element root) throws Exception {
        XPathExpression exp = xpath.compile("./core");
        Element coreE = (Element) exp.evaluate(root, XPathConstants.NODE);
        int max = Integer.parseInt(coreE.getAttribute("tasks"));
        //System.out.println(max);
    }

    public void readProject(XPath xpath, Element projectE) throws Exception {
        ProjectInformationImpl info = project.getLookup().lookup(ProjectInformationImpl.class);
        WorkspaceProviderImpl workspaces = project.getLookup().lookup(WorkspaceProviderImpl.class);

        info.setName(projectE.getAttribute("name"));

        //WorkSpaces
        XPathExpression exp = xpath.compile("./workspaces/workspace");
        NodeList workSpaceList = (NodeList) exp.evaluate(projectE, XPathConstants.NODESET);

        for (int i = 0; i < workSpaceList.getLength() && !cancel; i++) {
            Element workspaceE = (Element) workSpaceList.item(i);
            Workspace workspace = readWorkspace(xpath, workspaceE);

            //Current workspace
            if (workspace.getLookup().lookup(WorkspaceInformationImpl.class).isOpen()) {
                workspaces.setCurrentWorkspace(workspace);
            }
        }
    }

    public Workspace readWorkspace(XPath xpath, Element workspaceE) throws Exception {
        WorkspaceImpl workspace = project.getLookup().lookup(WorkspaceProviderImpl.class).newWorkspace();
        WorkspaceInformationImpl info = workspace.getLookup().lookup(WorkspaceInformationImpl.class);

        //Name
        info.setName(workspaceE.getAttribute("name"));

        //Status
        String workspaceStatus = workspaceE.getAttribute("status");
        if (workspaceStatus.equals("open")) {
            info.open();
        } else if (workspaceStatus.equals("closed")) {
            info.close();
        } else {
            info.invalid();
        }

        //WorkspacePersistent
        readWorkspaceChildren(workspace, workspaceE);

        return workspace;
    }

    public void readWorkspaceChildren(Workspace workspace, Element workspaceE) throws Exception {
        NodeList children = workspaceE.getChildNodes();
        for (int i = 0; i < children.getLength() && !cancel; i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childE = (Element) child;
                WorkspacePersistenceProvider pp = providers.get(childE.getTagName());
                if (pp != null) {
                    try {
                        pp.readXML(childE, workspace);
                    } catch (UnsupportedOperationException e) {
                    }
                }
            }

        }
    }
}
