package de.enflexit.ws.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

/**
 * The Class BundleInstaller provides static methods to install jar files.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class BundleInstaller {

	private static final Logger LOG = Log.getLogger(BundleInstaller.class);
	
	/**
	 * Installs the specified list of jar bundles.
	 *
	 * @param fileList the list bundle jar files
	 * @return the list of installed bundles
	 */
	public static List<Bundle> installBundles(List<File> fileList) {
		List<Bundle> bundlesInstalled = new ArrayList<Bundle>();
		for (int i = 0; i < fileList.size(); i++) {
			Bundle bundleInstalled = installBundle(fileList.get(i));
			if (bundleInstalled!=null) {
				bundlesInstalled.add(bundleInstalled);
			}
		}
		return bundlesInstalled;
	}
	
	/**
	 * Installs the specified jar bundle.
	 *
	 * @param bundleJarFile the bundle jar file
	 * @return the bundle that was installed
	 */
	public static Bundle installBundle(File bundleJarFile) {
		
		// --- Check the symbolic bundle name of the jar to load ----
		String sbn = getBundleNameFromFile(bundleJarFile);
		if (sbn!=null && sbn.isEmpty()==false) {
			if (Platform.getBundle(sbn)!=null) {
				LOG.debug("Bundle '" + sbn + "' is already installed, skip installation of jar file!");
				return null;
			}
		}
		return installBundle("reference:file:" + bundleJarFile.getAbsolutePath());
	}
	
	/**
	 * Returns the possible bundle name from the specified file.
	 *
	 * @param file the file
	 * @return the bundle name from file
	 */
	private static String getBundleNameFromFile(File file) {
		return getBundleNameFromFileName(file.getName());
	}
	/**
	 * Return the bundle name from file name.
	 *
	 * @param fileName the file name
	 * @return the bundle name from file name
	 */
	private static String getBundleNameFromFileName(String fileName) {
		String bundleName = null;
		int cutAt = fileName.indexOf("_");
		if (cutAt>-1) {
			// --- Separate bundle name & version --- 
			bundleName = fileName.substring(0, cutAt);
		}
		return bundleName;	
	}
	
	/**
	 * Installs the specified jar bundle.
	 *
	 * @param bundleJarFilePath the bundle jar file path as string
	 * @return the bundle that was installed
	 */
	public static Bundle installBundle(String bundleJarFilePath) {
		Bundle bundle = null;
		try {
			BundleContext bundleContext = FrameworkUtil.getBundle(OsgiJetty.class).getBundleContext();
			bundle = bundleContext.installBundle(bundleJarFilePath);
			
		} catch (BundleException bEx) {
			bEx.printStackTrace();
		}
		return bundle;
	}
	
	
	/**
	 * Returns the state of an OSGI bundle as String.
	 *
	 * @param bundle the bundle to check
	 * @return the state as string
	 */
	public static String getBundleStateAsString(Bundle bundle) {
		return getBundleStateAsString(bundle.getState());
	}
	/**
	 * Returns the state of an OSGI bundle as String.
	 *
	 * @param state the state
	 * @return the state as string
	 */
	public static String getBundleStateAsString(int state) {
        switch (state) {
        case Bundle.UNINSTALLED:
            return "UNINSTALLED";
        case Bundle.INSTALLED:
            return "INSTALLED";
        case Bundle.RESOLVED:
            return "RESOLVED";
        case Bundle.STARTING:
            return "STARTING";
        case Bundle.STOPPING:
            return "STOPPING";
        case Bundle.ACTIVE:
            return "ACTIVE";
        }
        return null;
    }
}
