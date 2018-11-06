package de.enflexit.awb.webserver;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;

import de.enflexit.awb.webserver.jetty.JettyConfiguration;

/**
 * The Class JettyInstance.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyInstance {

	/**
	 * Instantiates a new Jetty instance.
	 */
	public JettyInstance() {
	}
	
	/**
	 * Starts the Jetty web server.
	 */
	public void startJetty() {
		
		try {

			// --- The configuration files ----------------
			List<URL> xmlConfigFiles = JettyConfiguration.getXmlConfigurationFiles();
			
			// --- The properties -------------------------
			Map<String, String> properties = JettyConfiguration.getJettyProperties();
					
			// --- Get the actual server instance ---------
			Server server = this.loadServer(xmlConfigFiles, properties);
			server.start();
			server.join();
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
	}
	
	/**
	 * Load server.
	 *
	 * @param xmlConfigUrls the xml config urls
	 * @param props the props
	 * @return the server
	 * @throws Exception the exception
	 */
	private Server loadServer(List<URL> xmlConfigUrls, Map<String, String> props) throws Exception {
		
		XmlConfiguration last = null;
		// Hold list of configured objects
		Object[] obj = new Object[xmlConfigUrls.size()];

		// Configure everything
		for (int i = 0; i < xmlConfigUrls.size(); i++) {
			URL configURL = xmlConfigUrls.get(i);
			XmlConfiguration configuration = new XmlConfiguration(configURL);
			if (last != null) {
				// Let configuration know about prior configured objects
				configuration.getIdMap().putAll(last.getIdMap());
			}
			configuration.getProperties().putAll(props);
			obj[i] = configuration.configure();
			last = configuration;
		}

		// Find Server Instance.
		Server foundServer = null;
		int serverCount = 0;
		for (int i = 0; i < xmlConfigUrls.size(); i++) {
			if (obj[i] instanceof Server) {
				if (obj[i].equals(foundServer)) {
					// Identical server instance found
					continue; // Skip
				}
				foundServer = (Server) obj[i];
				serverCount++;
			}
		}

		if (serverCount <= 0) {
			throw new IllegalStateException("Load failed to configure a " + Server.class.getName());
		}
		if (serverCount == 1) {
			return foundServer;
		}
		throw new IllegalStateException(String.format("Configured %d Servers, expected 1", serverCount));
	}
	
	
}
