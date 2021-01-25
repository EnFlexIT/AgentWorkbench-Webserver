package de.enflexit.awb.webserver.core;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

/**
 * The Class OsgiJetty can be used to ensure that all required OSGI bundles of Jetty
 * are in the the OSGI State {@link Bundle#ACTIVE} before Jetty will be started.
 * In turn, Jetty can also be stopped.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OsgiJetty {

	private static final Logger LOG = Log.getLogger(OsgiJetty.class);
	
	public static final String JETTY_OSGI_BOOT_BUNDLE = "org.eclipse.jetty.osgi.boot";
	public static final String ARIES_SPIFLY_DYNAMIC_BUNDLE = "org.apache.aries.spifly.dynamic.bundle";
	
	private static List<String> requiredJettyBundleNames;
	private static HashMap<String, String> requiredJettyBundleVersion;
	
	private static Bundle jettyOsgiBootBundel;
	private static Bundle ariesSiflyDynamicBundle;
	
	/**
	 * Starts Jetty.
	 */
	public static void start() {
		startJettyBundles();
	}
	/**
	 * Stops Jetty.
	 */
	public static void stop() {
		
		try {
			if (jettyOsgiBootBundel!=null) {
				jettyOsgiBootBundel.stop();
				jettyOsgiBootBundel = null;
			}
			if (ariesSiflyDynamicBundle!=null) {
				ariesSiflyDynamicBundle.stop();
				ariesSiflyDynamicBundle = null;
			}
			
		} catch (BundleException bEx) {
			bEx.printStackTrace();
		}
	}
	
	/**
	 * Starts the required jetty bundles, where the bundle 'org.eclipse.jetty.osgi.boot'
	 * is the last and which will start Jetty.
	 */
	private static void startJettyBundles() {
		
		TreeMap<String, Bundle> bundleMap = OsgiJetty.getBundleMap();
		
		List<String> reqBundleList = OsgiJetty.getRequiredJettyBundles();
		for (int i = 0; i < reqBundleList.size(); i++) {
			
			String bundleName = reqBundleList.get(i);
			Bundle bundle = bundleMap.get(bundleName);
			if (bundle==null) {
				// --- Backup access --------------------------------
				if (bundleName.equals("org.apache.geronimo.specs.geronimo-jta_1.1_spec_1.1.1.jar")==true) {
					bundle = bundleMap.get("org.apache.geronimo.specs.geronimo-jta_1.1_spec");
				} else {
					System.err.println("[]");
				}
			}
			
			if (bundle!=null) {
				
				boolean startBundle = false;
				// --- Remind the Jetty OSG bundle ------------------
				if (bundleName.equals(JETTY_OSGI_BOOT_BUNDLE)) {
					jettyOsgiBootBundel = bundle;
					startBundle = true;
				} else if (bundleName.equals(ARIES_SPIFLY_DYNAMIC_BUNDLE)) {
					ariesSiflyDynamicBundle = bundle;
					startBundle = true;
				}
				// --- Start the current bundle ---------------------
				if (startBundle==true) {
					try {
						bundle.start();
					} catch (BundleException bEx) {
						bEx.printStackTrace();
					}
				}
				
			} else {
				LOG.warn("Could not resolve bundle '" + bundleName + "' => Not installed yet.");
			}
		}
	}
	
	/**
	 * Return the available / installed OSGI bundles in a TreeMap.
	 * @return the available bundles
	 */
	private static TreeMap<String, Bundle> getBundleMap() {
		
		TreeMap<String, Bundle> bundleMap = new TreeMap<>();
		
		Bundle localBundle = FrameworkUtil.getBundle(OsgiJetty.class);
		BundleContext bc = localBundle.getBundleContext();
		List<Bundle> bundlesLoadedList = new ArrayList<>(Arrays.asList(bc.getBundles()));
		
		// --- Check if the bundle array contains the jetty OSGI bundle -------
		if (containsJettyOSGIBundle(bundlesLoadedList)==false) {
			// ----------------------------------------------------------------
			// --- Install missing jetty bundles ------------------------------
			// ----------------------------------------------------------------
			File pluginDir = getPluginDirectory(bundlesLoadedList);
			List<Bundle> bundlesLoadedListNew = installJettyBundles(pluginDir);
			if (bundlesLoadedListNew!=null) {
				// ------------------------------------------------------------
				// --- In case of a missing bundle ----------------------------
				// ------------------------------------------------------------
				if (bundlesLoadedListNew.size()!=getRequiredJettyBundles().size()) {
					// --- Reduce to missing bundle list ----------------------
					List<String> missingBundles = new ArrayList<>(getRequiredJettyBundles());
					for (int i = 0; i < bundlesLoadedListNew.size(); i++) {
						missingBundles.remove(bundlesLoadedListNew.get(i).getSymbolicName());
					}
					// --- Remove bundles that were already loaded ------------
					missingBundles.remove("org.apache.geronimo.specs.geronimo-jta_1.1_spec_1.1.1.jar");
					for (int i=0; i < missingBundles.size(); i++) {
						if (Platform.getBundle(missingBundles.get(i))!=null) {
							missingBundles.remove(i);
							i--;
						}
					}
					
					// --- Print missing bundle? ------------------------------ 
					if (missingBundles.size()>0) {
						int noOfRrequiredBundles = getRequiredJettyBundles().size();
						int noOfMissingBundless = missingBundles.size();
						LOG.warn("" + (noOfRrequiredBundles - noOfMissingBundless) + " of " + noOfRrequiredBundles + " jetty bundles wer loaded");
						for (int i = 0; i < missingBundles.size(); i++) {
							if (Platform.getBundle(missingBundles.get(i))==null) {
								LOG.warn("=> Missing bundle '" + missingBundles.get(i) + "'");
							}
						}
					}
				}
				// --- Add to the list of loaded bundles ----------------------
				bundlesLoadedList.addAll(bundlesLoadedListNew);
			}
		}
		
		for (int i = 0; i < bundlesLoadedList.size(); i++) {
			Bundle bundleLoaded = bundlesLoadedList.get(i);
			bundleMap.put(bundleLoaded.getSymbolicName(), bundleLoaded);
		}
		return bundleMap;
	}
	/**
	 * Checks if the specified bundle array contains the jetty OSGI bundle.
	 *
	 * @param bundleList the list bundle 
	 * @return true, if successful
	 */
	private static boolean containsJettyOSGIBundle(List<Bundle> bundleList) {
		for (int i = 0; i < bundleList.size(); i++) {
			if (bundleList.get(i).getSymbolicName().equals(JETTY_OSGI_BOOT_BUNDLE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the plugin directory of the current runtime.
	 *
	 * @param bundleArray the current bundle list
	 * @return the plugin directory of the current runtime
	 */
	private static File getPluginDirectory(List<Bundle> bundleList) {
		
		// --- Resolve the bundle 'org.eclipse.core.runtime' --------
		Bundle eqBundle = null;
		for (int i = 0; i < bundleList.size(); i++) {
			if (bundleList.get(i).getSymbolicName().equals("org.eclipse.core.runtime")) {
				eqBundle = bundleList.get(i);
				break;
			}
		}
		
		File pluginDir = null;
		try {
			// --- Get URL of runtime bundle ------------------------
			URL resolvedURL = FileLocator.resolve(eqBundle.getEntry("/"));
			
			// --- Extract 'file:' indicator ------------------------
			String filePathName = resolvedURL.toExternalForm();
			int cutAt = filePathName.indexOf("file:/") + "file:/".length();
			if (cutAt>-1) {
				filePathName = filePathName.substring(cutAt);
			}
			
			// --- Get file object ----------------------------------
			File eqFile = new File(filePathName);
			pluginDir = eqFile.getParentFile();
			
		} catch (Exception ex) { 
			ex.printStackTrace();
		}		
		return pluginDir;
	}
	
	/**
	 * Resolve jetty bundles.
	 *
	 * @param bundleArray the currently loaded bundles as array
	 * @return the bundle[]
	 */
	private static List<Bundle> installJettyBundles(File pluginDirectory) {
		
		List<Bundle> bundlesInstalled = null;
		
		try {
			// --- Check existence of plugin directory --------------
			if (pluginDirectory.exists()==false) {
				LOG.warn("The specified plugin directory '" + pluginDirectory.getAbsolutePath() + "' could not be found!");
				return null;
			}
			
			// --- List possible jetty jar-files --------------------
			File[] jarFiles = pluginDirectory.listFiles(new FilenameFilter() {
				
				/* (non-Javadoc)
				 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
				 */
				@Override
				public boolean accept(File dir, String fileName) {
					
					boolean accept = false;
					if (fileName.endsWith(".jar")==false) {
						// --- Simply reject ----------------------------------
						return false;
						
					} else if (fileName.contains(".source")==true) {
						return false;

					} else if (fileName.equals("")==true) {
						// --- Space for a possible accept/reject -------------
						
					} else if (getRequiredJettyBundles().contains(fileName)==true) {
						return true;
					
					} else {
						// --- Check jar file name ----------------------------
						int cutAt = fileName.indexOf("_");
						if (cutAt>-1) {
							// --- Separate bundle name & version -------------
							String bundleNameCandidate = fileName.substring(0, cutAt);
							String versionCandidate    = fileName.substring(cutAt+1);
							
							// --- Get required version -----------------------
							String versionRequired     = getRequiredJettyBundleVersion().get(bundleNameCandidate);
							if (bundleNameCandidate.startsWith("org.eclipse.jetty.")==true) {
								if (bundleNameCandidate.equals("org.eclipse.jetty.schemas")) {
									versionRequired    = null;
								} else {
									versionRequired    = getRequiredJettyBundleVersion().get("org.eclipse.jetty.");
								}
								
							} else if (bundleNameCandidate.startsWith("org.objectweb.asm")==true) {
								versionRequired        = getRequiredJettyBundleVersion().get("org.objectweb.asm");
							}

							// --- Check to accept or not ---------------------
							boolean isJettyBundle = getRequiredJettyBundles().contains(bundleNameCandidate);
							boolean isRightVersion = versionRequired==null || versionCandidate.startsWith(versionRequired);
							if (isJettyBundle==true && isRightVersion==true) {
								accept = true;
							}
						}
						
					}
					return accept;
				}
			});
			
			// --- Install these bundles --------------------------------------
			bundlesInstalled = installBundles(Arrays.asList(jarFiles));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		return bundlesInstalled;
	}
	
	/**
	 * Installs the specified list of jar bundled and adds their bundle instances to the local bundle vector {@link #getBundleVector()}.
	 * @param bundleJarFile the bundle jar file
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
	 * Installs the specified jar bundle and adds the Bundle instance to the local bundle vector {@link #getBundleVector()}.
	 * @param bundleJarFile the bundle jar file
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
	 * Installs the specified jar bundle and adds the Bundle instance to the local bundle vector {@link #getBundleVector()}.
	 * @param bundleJarFilePath the bundle jar file path
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
	 * Returns the jetty bundles that should be ACTIVE before starting Jetty. 
	 * The list either contains the symbolic bundle name or the actual file name of the jar file.
	 *  
	 * @return the required jetty bundles
	 */
	private static List<String> getRequiredJettyBundles() {
		if (requiredJettyBundleNames==null) {

			// --- Create list of required bundles ----------------------------
			requiredJettyBundleNames = new ArrayList<>();

			// --- The third party bundles first ------------------------------
			requiredJettyBundleNames.add(ARIES_SPIFLY_DYNAMIC_BUNDLE);

			requiredJettyBundleNames.add("org.objectweb.asm");
			requiredJettyBundleNames.add("org.objectweb.asm.commons");
			requiredJettyBundleNames.add("org.objectweb.asm.tree");
			requiredJettyBundleNames.add("org.objectweb.asm.tree.analysis");
			
			requiredJettyBundleNames.add("org.apache.geronimo.specs.geronimo-jta_1.1_spec_1.1.1.jar");
			
			requiredJettyBundleNames.add("javax.inject");
			requiredJettyBundleNames.add("javax.annotation");
			requiredJettyBundleNames.add("javax.mail.glassfish");
			
			requiredJettyBundleNames.add("javax.servlet");
			requiredJettyBundleNames.add("javax.servlet.jsp");
			requiredJettyBundleNames.add("javax.servlet.jsp.jstl");
			requiredJettyBundleNames.add("javax.websocket");
			
			requiredJettyBundleNames.add("org.apache.taglibs.taglibs-standard-spec");
			requiredJettyBundleNames.add("org.apache.taglibs.standard-impl");
			
			requiredJettyBundleNames.add("org.mortbay.jasper.apache-el");
			requiredJettyBundleNames.add("org.mortbay.jasper.apache-jsp");
			
			// --- Some eclipse bundles ---------------------------------------
			requiredJettyBundleNames.add("org.eclipse.equinox.http.servlet");
			
			
			// --- The Jetty bundles from feature... --------------------------
			requiredJettyBundleNames.add("javax.security.auth.message");
			requiredJettyBundleNames.add("org.eclipse.jetty.alpn.client");
			requiredJettyBundleNames.add("org.eclipse.jetty.alpn.server");
			requiredJettyBundleNames.add("org.eclipse.jetty.annotations");
			requiredJettyBundleNames.add("org.eclipse.jetty.client");
			requiredJettyBundleNames.add("org.eclipse.jetty.continuation");
			requiredJettyBundleNames.add("org.eclipse.jetty.deploy");
			requiredJettyBundleNames.add("org.eclipse.jetty.http");
			requiredJettyBundleNames.add("org.eclipse.jetty.http2.client");
			requiredJettyBundleNames.add("org.eclipse.jetty.http2.client.http");
			requiredJettyBundleNames.add("org.eclipse.jetty.http2.common");
			requiredJettyBundleNames.add("org.eclipse.jetty.http2.hpack");
			requiredJettyBundleNames.add("org.eclipse.jetty.http2.server");
			requiredJettyBundleNames.add("org.eclipse.jetty.io");
			requiredJettyBundleNames.add("org.eclipse.jetty.jaas");
			requiredJettyBundleNames.add("org.eclipse.jetty.jmx");
			requiredJettyBundleNames.add("org.eclipse.jetty.jndi");
			requiredJettyBundleNames.add("org.eclipse.jetty.osgi-servlet-api");
			requiredJettyBundleNames.add("org.eclipse.jetty.osgi.alpn.fragment");
			
			requiredJettyBundleNames.add("org.eclipse.jetty.osgi.httpservice");
			requiredJettyBundleNames.add("org.eclipse.jetty.plus");
			requiredJettyBundleNames.add("org.eclipse.jetty.proxy");
			requiredJettyBundleNames.add("org.eclipse.jetty.rewrite");
			requiredJettyBundleNames.add("org.eclipse.jetty.schemas");
			requiredJettyBundleNames.add("org.eclipse.jetty.security");
			requiredJettyBundleNames.add("org.eclipse.jetty.server");
			requiredJettyBundleNames.add("org.eclipse.jetty.servlet");
			requiredJettyBundleNames.add("org.eclipse.jetty.servlets");
			requiredJettyBundleNames.add("org.eclipse.jetty.util");
			requiredJettyBundleNames.add("org.eclipse.jetty.util.ajax");
			requiredJettyBundleNames.add("org.eclipse.jetty.webapp");
			requiredJettyBundleNames.add("org.eclipse.jetty.websocket.api");
			requiredJettyBundleNames.add("org.eclipse.jetty.websocket.client");
			requiredJettyBundleNames.add("org.eclipse.jetty.websocket.common");
			requiredJettyBundleNames.add("org.eclipse.jetty.websocket.javax.websocket");
			requiredJettyBundleNames.add("org.eclipse.jetty.websocket.javax.websocket.server");
			requiredJettyBundleNames.add("org.eclipse.jetty.websocket.server");
			requiredJettyBundleNames.add("org.eclipse.jetty.websocket.servlet");
			requiredJettyBundleNames.add("org.eclipse.jetty.xml");

			// --- ... and finally, the OGI boot bundle of Jetty ------------------- 
			requiredJettyBundleNames.add("org.eclipse.jetty.osgi.boot.warurl");
			requiredJettyBundleNames.add(JETTY_OSGI_BOOT_BUNDLE);
		}
		return requiredJettyBundleNames;
	}
	
	
	
	/**
	 * Returns the required jetty bundle version HaspMap with selected version reminder.
	 * @return the required jetty bundle version
	 */
	private static HashMap<String, String> getRequiredJettyBundleVersion() {
		if (requiredJettyBundleVersion==null) {
			requiredJettyBundleVersion = new HashMap<>();
			requiredJettyBundleVersion.put("javax.annotation", "1.3.");
			requiredJettyBundleVersion.put("javax.servlet", "3.1.");
			requiredJettyBundleVersion.put("javax.servlet.jsp", "2.2.");
			requiredJettyBundleVersion.put("org.eclipse.jetty.", "9.4.35");
			requiredJettyBundleVersion.put("org.objectweb.asm", "7.2.");
		}
		return requiredJettyBundleVersion;
	}
	
	/**
	 * Returns the state of an OSGI bundle as String.
	 *
	 * @param state the state
	 * @return the string
	 */
	public static String toState(int state) {
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
