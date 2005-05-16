/*
 * Created on May 11, 2005
 *
 * @author Fabio Zadrozny
 */
package org.python.pydev.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * @author Fabio Zadrozny
 */
public class BundleInfo implements IBundleInfo{

    private static IBundleInfo info;
    public static void setBundleInfo(IBundleInfo b){
        info = b;
    }
    public static IBundleInfo getBundleInfo(){
        if(info == null){
            info = new BundleInfo();
        }
        return info;
    }
    
    /**
     * @throws CoreException
     * @see org.python.pydev.plugin.IBundleInfo#getRelativePath(org.eclipse.core.runtime.IPath)
     */
    public File getRelativePath(IPath relative) throws CoreException {
        Bundle bundle = PydevPlugin.getDefault().getBundle();

        URL bundleURL = Platform.find(bundle, relative);
        URL fileURL;
        try {
            fileURL = Platform.asLocalURL(bundleURL);
            File f = new File(fileURL.getPath());

            return f;
        } catch (IOException e) {
            throw new CoreException(PydevPlugin.makeStatus(IStatus.ERROR, "Can't find python debug script", null));
        }
    }
    /**
     * @see org.python.pydev.plugin.IBundleInfo#getPluginID()
     */
    public String getPluginID() {
        return PydevPlugin.getDefault().getBundle().getSymbolicName();
    }

}
