package de.enflexit.awb.webserver;

import java.io.File;
import java.util.Dictionary;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;

import agentgui.core.application.Application;

/**
 * The Class JettyRuntime serves as central (singleton) instance to access runtime information
 * as well as the actual Server instance of the Jetty web server.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyRuntime {

	public static final String JETTY_SERVER_ID = "AWB-Jetty";
	public static final String JETTY_SUB_PATH = "jetty"; 
	public static final String JETTY_CONFIG_START_WITH_JADE = "JETTY_START_WITH_JADE";

	private JettyConfiguration jettyConfiguration; 
	private IEclipsePreferences eclipsePreferences;
	private boolean isServerExecuted;

	
	/**
	 * Start the Jetty server with the bundle configuration.
	 * @return the server
	 */
	public void startServer() {
		this.startServer(null);
	}
	/**
	 * Start the Jetty server with the specified configuration.
	 *
	 * @param jettyConfig the jetty configuration. If null, the local configuration will be used
	 * @return the server
	 * @see #getJettyConfiguration()
	 */
	public void startServer(Dictionary<String, ? extends Object> jettyConfig) {
		
		try {
			// --- Check configuration ----------
			Dictionary<String, ? extends Object> jettyConfigToUse = jettyConfig;
			if (jettyConfigToUse==null) {
				jettyConfigToUse = this.getJettyConfiguration().getJettyActivatorConfiguration();
			}
			// --- Start the server -------------
			if (jettyConfigToUse!=null) {
				JettyConfigurator.startServer(JETTY_SERVER_ID, jettyConfigToUse);
				this.isServerExecuted = true;
			}
			 
		} catch (Exception ex) {
			this.isServerExecuted = false;
			this.stopServer();
			ex.printStackTrace();
		}
	}
	/**
	 * Stops the Jetty server.
	 * @return true, if successful
	 */
	public void stopServer() {
		try {
			JettyConfigurator.stopServer(JETTY_SERVER_ID);
			isServerExecuted = false;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * Checks if the server is executed.
	 * @return true, if the server is executed
	 */
	public boolean isServerExecuted() {
		return isServerExecuted;
	}

	/**
	 * Returns the current jetty configuration.
	 * @return the jetty configuration
	 */
	public JettyConfiguration getJettyConfiguration() {
		if (jettyConfiguration==null) {
			jettyConfiguration = new JettyConfiguration();
		}
		return jettyConfiguration;
	}
	
	/**
	 * Returns the eclipse preferences.
	 * @return the eclipse preferences
	 */
	public IEclipsePreferences getEclipsePreferences() {
		if (eclipsePreferences==null) {
			IScopeContext iScopeContext = ConfigurationScope.INSTANCE;
			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
			eclipsePreferences = iScopeContext.getNode(bundle.getSymbolicName());
		}
		return eclipsePreferences;
	}
	/**
	 * Saves the bundle properties.
	 */
	public void saveEclipsePreferences() {
		try {
			this.getEclipsePreferences().flush();
		} catch (BackingStoreException bsEx) {
			bsEx.printStackTrace();
		}
	}
	
	
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
	
}
