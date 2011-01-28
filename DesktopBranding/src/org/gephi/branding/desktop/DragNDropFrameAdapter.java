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
package org.gephi.branding.desktop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.TransferHandler;
import org.gephi.desktop.importer.api.ImportControllerUI;
import org.gephi.desktop.project.api.ProjectControllerUI;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/dnd/toplevel.html">Top-Level Drop Swing tutorial</a>
 *
 * @author Mathieu Bastian
 */
public class DragNDropFrameAdapter {

    private static final String GEPHI_EXTENSION = "gephi";

    public static void register() {
        JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
        frame.setTransferHandler(new TransferHandler() {

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    return false;
                }
                //Due to bug 6759788 - http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6759788
                //Impossible to get data here and look if compatible format
                return true;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }
                try {
                    List data = (List) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    File file = (File) data.get(0);
                    FileObject fileObject = FileUtil.toFileObject(file);
                    if(!file.exists()) {
                        return false;
                    }
                    if (fileObject.hasExt(GEPHI_EXTENSION)) {

                        ProjectControllerUI pc = Lookup.getDefault().lookup(ProjectControllerUI.class);
                        try {
                            pc.openProject(file);
                        } catch (Exception ew) {
                            ew.printStackTrace();
                            NotifyDescriptor.Message msg = new NotifyDescriptor.Message(NbBundle.getMessage(DragNDropFrameAdapter.class, "DragNDropFrameAdapter.openGephiError"), NotifyDescriptor.WARNING_MESSAGE);
                            DialogDisplayer.getDefault().notify(msg);
                        }
                    } else {
                        ImportControllerUI importController = Lookup.getDefault().lookup(ImportControllerUI.class);
                        if (importController.getImportController().isFileSupported(FileUtil.toFile(fileObject))) {
                            importController.importFile(fileObject);
                        } else {
                            return false;
                        }
                    }
                    return true;
                } catch (UnsupportedFlavorException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return false;
            }
        });
    }
}
