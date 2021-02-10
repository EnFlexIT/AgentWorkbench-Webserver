package de.enflexit.ws.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * The Class OsgiJetty can be used to start and stop the OSGI-based Jetty. 
 * Therefore, it will be ensured that all required OSGI bundles of Jetty
 * are in the the OSGI State {@link Bundle#INSTALLED} before Jetty will be started.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OsgiJetty {

	private static final Logger LOG = Log.getLogger(OsgiJetty.class);
	
	public static final String JETTY_OSGI_BOOT_BUNDLE = "org.eclipse.jetty.osgi.boot";
	public static final String ARIES_SPIFLY_DYNAMIC_BUNDLE = "org.apache.aries.spifly.dynamic.bundle";
	
	private static List<String> requiredJettyBundleNames;
	private static HashMap<String, String> requiredJettyBundleVersion;
	
	private static List<Bundle> bundlesToStart;
	
	/**
	 * Starts Jetty.
	 */
	public static void start() {
		
		// --- Check if the required bundle are already active. -----
		if (isRequiredBundlesAreActive()==true) return;
		// --- Simply start the bundles -----------------------------
		for (int i = 0; i < getBundlesToStart().size(); i++) {
			BundleHandler.startBundle(getBundlesToStart().get(i));
		}
	}
	/**
	 * Stops Jetty.
	 */
	public static void stop() {
		for (int i = getBundlesToStart().size()-1; i>=0; i--) {
			BundleHandler.stopBundle(getBundlesToStart().get(i));
		}
	}
	
	/**
	 * Checks if the required bundle are already active.
	 * @return true, if is restart bundles to start
	 */
	private static boolean isRequiredBundlesAreActive() {
		for (int i = 0; i < getBundlesToStart().size(); i++) {
			if (!(getBundlesToStart().get(i).getState()==Bundle.ACTIVE)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the bundles to start.
	 * @return the bundles to start
	 */
	private static List<Bundle> getBundlesToStart() {
		if (bundlesToStart==null) {
			bundlesToStart = new ArrayList<Bundle>();
			
			// --- Get (and possibly install) the required jetty modules ------
			TreeMap<String, Bundle> bundleMap = OsgiJetty.getBundleMap();
			
			// --- Define the bundles to start --------------------------------
			List<String> bundlesNamesToStart = new ArrayList<>();
			bundlesNamesToStart.add(ARIES_SPIFLY_DYNAMIC_BUNDLE);
			bundlesNamesToStart.add(JETTY_OSGI_BOOT_BUNDLE);

			// --- Remind the important bundles ------------------------------- 
			bundlesToStart = new ArrayList<>();
			for (int i = 0; i < bundlesNamesToStart.size(); i++) {

				String bundleName = bundlesNamesToStart.get(i);
				Bundle bundle = bundleMap.get(bundleName);

				if (bundle!=null) {
					bundlesToStart.add(bundle);
				} else {
					LOG.warn("Could not resolve bundle '" + bundleName + "' => Not installed yet.");
				}
			}
		}
		return bundlesToStart;
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
		if (containsAllJettyOSGIBundle(bundlesLoadedList)==false) {
			// ----------------------------------------------------------------
			// --- Install missing jetty bundles ------------------------------
			// ----------------------------------------------------------------
			File pluginDir = WebServerGlobalInfo.getPluginDirectory();
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
	 * Checks if the specified list of bundles contains all jetty OSGI bundle.
	 *
	 * @param bundleListToCheck the list bundle 
	 * @return true, if successful
	 */
	private static boolean containsAllJettyOSGIBundle(List<Bundle> bundleListToCheck) {
		
		List<String> missingBundles = new ArrayList<>(getRequiredJettyBundles());
		for (int i = 0; i < bundleListToCheck.size(); i++) {
			missingBundles.remove(bundleListToCheck.get(i).getSymbolicName());
		}
		
		if (missingBundles.size()>0) {
			LOG.warn(missingBundles.size() + " bundles need to be installed to operate Jetty.");
			return false;
		}
		return true;
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
								
							} else if (bundleNameCandidate.equals("org.apache.geronimo.specs.geronimo-jta")==true) {
								cutAt = fileName.lastIndexOf("_");
								bundleNameCandidate = fileName.substring(0, cutAt);
								versionCandidate    = fileName.substring(cutAt+1);
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
			bundlesInstalled = BundleHandler.installBundles(Arrays.asList(jarFiles));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		return bundlesInstalled;
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
			
			requiredJettyBundleNames.add("org.apache.geronimo.specs.geronimo-jta_1.1_spec");
			
			requiredJettyBundleNames.add("javax.activation");
			requiredJettyBundleNames.add("javax.annotation");
			requiredJettyBundleNames.add("javax.inject");
			requiredJettyBundleNames.add("javax.mail.glassfish");
			
			requiredJettyBundleNames.add("javax.servlet");
			requiredJettyBundleNames.add("javax.servlet.jsp");
			requiredJettyBundleNames.add("javax.servlet.jsp.jstl");
			requiredJettyBundleNames.add("javax.websocket");
			
			requiredJettyBundleNames.add("org.apache.xalan");
			requiredJettyBundleNames.add("org.apache.xml.serializer");
			
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
			requiredJettyBundleVersion.put("org.objectweb.asm", "7.2.");

			requiredJettyBundleVersion.put("org.eclipse.jetty.", "9.4.35");
		}
		return requiredJettyBundleVersion;
	}
	
	/**
	 * Prints the eclipse launch configuration for the jetty bundles.
	 */
	@SuppressWarnings("unused")
	private static void printEclipseLaunchConfigurationForJetyBundles() {
		
		// --- Example string -----------------------------
		// <setEntry value="org.slf4j.api@default:default"/>
		for (int i = 0; i < getRequiredJettyBundles().size(); i++) {
			String bundleName = getRequiredJettyBundles().get(i);
			System.out.println("<setEntry value=\"" + bundleName + "@default:default\"/>");
		}
	}
	
}
