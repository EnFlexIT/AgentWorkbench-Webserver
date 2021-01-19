package de.enflexit.awb.webserver.core;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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

	public static final String JETTY_OSGI_BOOT_BUNDLE = "org.eclipse.jetty.osgi.boot";
	
	private static Bundle jettyOsgiBootBundel;
	
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
		if (jettyOsgiBootBundel!=null) {
			try {
				jettyOsgiBootBundel.stop();
				jettyOsgiBootBundel = null;
			} catch (BundleException bEx) {
				bEx.printStackTrace();
			}
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
			
			
			if (bundle!=null) {
				// --- Remind the Jetty OSG bundle ------------------
				if (bundleName.equals(JETTY_OSGI_BOOT_BUNDLE)) {
					jettyOsgiBootBundel = bundle;
				}
				// --- Start the current bundle ---------------------
				try {
					bundle.start();
					
				} catch (BundleException bEx) {
					bEx.printStackTrace();
				}
				
			} else {
				System.err.println("[" + OsgiJetty.class.getSimpleName() + "] Could not find bundle '" + bundleName + "'");
			}
		}
	}
	
	/**
	 * Returns the jetty bundles that should be ACTIVE before starting Jetty.
	 * @return the required jetty bundles
	 */
	private static List<String> getRequiredJettyBundles() {
		
		// --- Create list of required bundles --------------------------------
		List<String> jettyBundleName = new ArrayList<>();

		// --- The third party bundles first ----------------------------------
		jettyBundleName.add("org.apache.aries.spifly.dynamic.bundle");

		jettyBundleName.add("org.objectweb.asm");
		jettyBundleName.add("org.objectweb.asm.commons");
		jettyBundleName.add("org.objectweb.asm.tree");
		jettyBundleName.add("org.objectweb.asm.tree.analysis");
		
		jettyBundleName.add("org.apache.geronimo.specs.geronimo-jta_1.1_spec");
		
		jettyBundleName.add("javax.inject");
		jettyBundleName.add("javax.annotation");
		jettyBundleName.add("javax.mail.glassfish");
		
		jettyBundleName.add("javax.servlet");
		jettyBundleName.add("javax.servlet.jsp");
		jettyBundleName.add("javax.servlet.jsp.jstl");
		jettyBundleName.add("javax.websocket");
		
		jettyBundleName.add("org.apache.taglibs.taglibs-standard-spec");
		jettyBundleName.add("org.apache.taglibs.standard-impl");
		
		jettyBundleName.add("org.mortbay.jasper.apache-el");
		jettyBundleName.add("org.mortbay.jasper.apache-jsp");
		
		// --- The Jetty bundles ... ------------------------------------------		
		jettyBundleName.add("org.eclipse.jetty.alpn.client");
		jettyBundleName.add("org.eclipse.jetty.alpn.server");
		jettyBundleName.add("org.eclipse.jetty.annotations");
		//jettyBundleName.add("org.eclipse.jetty.client");
		jettyBundleName.add("org.eclipse.jetty.continuation");
		jettyBundleName.add("org.eclipse.jetty.deploy");
		jettyBundleName.add("org.eclipse.jetty.http");
		//jettyBundleName.add("org.eclipse.jetty.http2.client");
		//jettyBundleName.add("org.eclipse.jetty.http2.client.http");
		jettyBundleName.add("org.eclipse.jetty.http2.common");
		jettyBundleName.add("org.eclipse.jetty.http2.hpack");
		jettyBundleName.add("org.eclipse.jetty.http2.server");
		jettyBundleName.add("org.eclipse.jetty.io");
		jettyBundleName.add("org.eclipse.jetty.jaas");
		jettyBundleName.add("org.eclipse.jetty.jmx");
		jettyBundleName.add("org.eclipse.jetty.jndi");
		jettyBundleName.add("org.eclipse.jetty.osgi-servlet-api");
		//jettyBundleName.add("org.eclipse.jetty.osgi.alpn.fragment");
		
		jettyBundleName.add("org.eclipse.jetty.osgi.httpservice");
		jettyBundleName.add("org.eclipse.jetty.plus");
		jettyBundleName.add("org.eclipse.jetty.proxy");
		jettyBundleName.add("org.eclipse.jetty.rewrite");
		jettyBundleName.add("org.eclipse.jetty.schemas");
		jettyBundleName.add("org.eclipse.jetty.security");
		jettyBundleName.add("org.eclipse.jetty.server");
		jettyBundleName.add("org.eclipse.jetty.servlet");
		jettyBundleName.add("org.eclipse.jetty.servlets");
		jettyBundleName.add("org.eclipse.jetty.util");
		jettyBundleName.add("org.eclipse.jetty.util.ajax");
		jettyBundleName.add("org.eclipse.jetty.webapp");
		jettyBundleName.add("org.eclipse.jetty.websocket.api");
		jettyBundleName.add("org.eclipse.jetty.websocket.client");
		jettyBundleName.add("org.eclipse.jetty.websocket.common");
		jettyBundleName.add("org.eclipse.jetty.websocket.javax.websocket");
		jettyBundleName.add("org.eclipse.jetty.websocket.javax.websocket.server");
		jettyBundleName.add("org.eclipse.jetty.websocket.server");
		jettyBundleName.add("org.eclipse.jetty.websocket.servlet");
		jettyBundleName.add("org.eclipse.jetty.xml");

		// --- ... and finally, the OGI boot bundle of Jetty ------------------- 
		jettyBundleName.add("org.eclipse.jetty.osgi.boot.warurl");
		jettyBundleName.add(JETTY_OSGI_BOOT_BUNDLE);
		
		return jettyBundleName;
	}
	
	/**
	 * Return the available OSGI bundles in a TreeMap.
	 * @return the available bundles
	 */
	private static TreeMap<String, Bundle> getBundleMap() {
		
		TreeMap<String, Bundle> bundleMap = new TreeMap<>();
		
		Bundle localBundle = FrameworkUtil.getBundle(OsgiJetty.class);
		BundleContext bc = localBundle.getBundleContext();
		Bundle[] bundleArray = bc.getBundles();
		for (int i = 0; i < bundleArray.length; i++) {
			
			bundleMap.put(bundleArray[i].getSymbolicName(), bundleArray[i]);
			boolean printBundlsAvailable = false;
			if (printBundlsAvailable==true) {
				// --- Print the 
				System.out.println(OsgiJetty.toState(bundleArray[i].getState()) + "\t" +  bundleArray[i].getSymbolicName() + "\t" + bundleArray[i].toString());
				// --- Some optional, adjustable  filtering ---------
				if (bundleArray[i].getSymbolicName().startsWith("abc.org.eclipse.jetty")==true) {
					// --- Special print for list integration ------- 
					System.out.println( "jettyBundleName.add(\"" + bundleArray[i].getSymbolicName() + "\");");
				}
			}
		}
		return bundleMap;
	}

	/**
	 * Returns the state of an OSGI bundle as String.
	 *
	 * @param state the state
	 * @return the string
	 */
	private static String toState(int state) {
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
