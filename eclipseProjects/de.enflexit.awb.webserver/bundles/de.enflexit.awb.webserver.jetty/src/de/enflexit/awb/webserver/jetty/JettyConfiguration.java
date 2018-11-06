package de.enflexit.awb.webserver.jetty;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agentgui.core.application.Application;

/**
 * The Class JettyConfiguration.
 */
public class JettyConfiguration {

	public static String JETTY_SUB_PATH = "jetty"; 
	
	
	/**
	 * Returns (and creates if needed) the jetty home directory.
	 * @return the jetty home directory
	 */
	public static File getJettyHomeDirectory() {
		
		String jettyHomeDirectroyPath = Application.getGlobalInfo().getPathBaseDir() + JETTY_SUB_PATH;
		File jettyHomeDir = new File(jettyHomeDirectroyPath);
		if (jettyHomeDir.exists()==false) {
			jettyHomeDir.mkdir();
		}
		return jettyHomeDir;
	}
	
	/**
	 * Returns the available XML configuration files form the local jetty installation.
	 * @return the XML configuration files
	 */
	public static List<URL> getXmlConfigurationFiles() {
		
		List<URL> xmlConfigFiles = new ArrayList<>();
		
		try {
			// --- List all XML files from the etc directory -------- 
			xmlConfigFiles.add(new File("jetty.xml").toURI().toURL());
		
		
		
		} catch (MalformedURLException mUrlEx) {
			mUrlEx.printStackTrace();
		}
		return xmlConfigFiles;
	}
	
	/**
	 * Returns the jetty properties. like 'jetty.home'.
	 * @return the jetty properties
	 */
	public static Map<String, String> getJettyProperties() {
		
		Map<String, String> props = new HashMap<String, String>();
		try {
			
			props.put("jetty.home", getJettyHomeDirectory().getCanonicalPath());

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return props;
	}
	
}
