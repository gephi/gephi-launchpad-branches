/*
Copyright 2008-2010 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
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
package org.gephi.lib.jogamp;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Manages the JogAmp native libraries loading.
 *
 * @author Antonio Patriarca
 */
public class JogAmpNativesInstaller extends ModuleInstall {
    private boolean exitOnFatalError = true;

    @Override
    public void restored() {
        final String osName = System.getProperty("os.name").toLowerCase();
        final String osArch = System.getProperty("os.arch").toLowerCase();
        
        final String osDir = getOsDir(osName);
        final String archDir = getArchDir(osArch, osDir);
        final String path = "modules/lib/" + osDir + "/" + archDir;

        final File jogAmpDistFolder = InstalledFileLocator.getDefault().locate(path, null, false);
        if (jogAmpDistFolder != null) {
                loadNatives(jogAmpDistFolder, osDir);
        } else {
            fatalError(String.format(NbBundle.getMessage(JogAmpNativesInstaller.class, "JogAmpNativesInstaller_error1"), new Object[]{path}));
        }
    }

    private String getOsDir(final String osName) {
        if (osName.startsWith("linux")) {
            return "linux";
        } else if (osName.startsWith("windows")) {
            return "windows";
        } else if (osName.startsWith("mac")) {
            return "macosx";
        } else {
            fatalError(String.format(NbBundle.getMessage(JogAmpNativesInstaller.class, "JogAmpNativesInstaller_error2"), new Object[]{osName}));
            return null;
        }
    }

    private String getArchDir(final String osArch, final String osDir) {
        if (osDir.equals("macosx")) {
            return "universal";
        } else if (osArch.equals("amd64") || osArch.equals("x86_64")) {
            return "amd64";
        } else if (osArch.equals("x86") || osArch.equals("i586") || osArch.equals("i386")) {
            return "i586";
        } else {
            fatalError(String.format(NbBundle.getMessage(JogAmpNativesInstaller.class, "JogAmpNativesInstaller_error3"), new Object[]{osDir, osArch}));
            return null;
        }
    }

    private void loadNatives(final File jogAmpDistFolder, final String osDir) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {

                    com.jogamp.gluegen.runtime.NativeLibLoader.disableLoading();
                    com.jogamp.nativewindow.impl.NWJNILibLoader.disableLoading();
                    com.jogamp.newt.impl.NEWTJNILibLoader.disableLoading();
                    // find opengl lib loader to disable it

                    final String prefix = getPrefix(osDir);
                    final String suffix = getSuffix(osDir);

                    loadLibrary(jogAmpDistFolder, "gluegen-rt", prefix, suffix);
                    loadLibrary(jogAmpDistFolder, "jogl_desktop", prefix, suffix);
                    loadLibrary(jogAmpDistFolder, "jogl_es1", prefix, suffix);
                    loadLibrary(jogAmpDistFolder, "jogl_es2", prefix, suffix);
                    if (osDir.equals("linux")) {
                        loadLibrary(jogAmpDistFolder, "nativewindow_x11", prefix, suffix);
                    } else if (osDir.equals("windows")) {
                        loadLibrary(jogAmpDistFolder, "nativewindow_win32", prefix, suffix);
                    }
                    loadLibrary(jogAmpDistFolder, "nativewindow_awt", prefix, suffix);
                    loadLibrary(jogAmpDistFolder, "newt", prefix, suffix);
                }
            });
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

     private void loadLibrary(final File installDir, final String libName, final String prefix, final String suffix) {
        final String nativeLibName = prefix + libName + suffix;
        try {
            final String path = new File(installDir, nativeLibName).getPath();
            System.load(path);
        } catch (UnsatisfiedLinkError ex) {
            if (ex.getMessage().indexOf("already loaded") == -1) {
                fatalError(String.format(NbBundle.getMessage(JogAmpNativesInstaller.class, "JogAmpNativesInstaller_error4"), new Object[]{nativeLibName}));
            }
        }
    }

    private void fatalError(final String error) {
        Exception ex = new Exception(error);
        NotifyDescriptor.Exception e = new NotifyDescriptor.Exception(ex);
        DialogDisplayer.getDefault().notify(e);
        if (exitOnFatalError) {
            System.exit(1);
        }
    }

    private String getPrefix(final String osDir) {
        if (osDir.equals("linux") || osDir.equals("macosx")) {
            return "lib";
        } else {
            return "";
        }
    }

    private String getSuffix(final String osDir) {
        if (osDir.equals("linux")) {
            return ".so";
        } else if (osDir.equals("windows")) {
            return ".dll";
        } else if (osDir.equals("macosx")) {
            return ".jnilib";
        } else {
            return "";
        }
    }

}
