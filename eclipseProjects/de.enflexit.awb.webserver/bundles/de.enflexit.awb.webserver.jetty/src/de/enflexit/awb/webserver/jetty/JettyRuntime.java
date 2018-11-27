package de.enflexit.awb.webserver.jetty;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jetty.server.Server;
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

	public static final String JETTY_SUB_PATH = "jetty"; 
	public static final String JETTY_CONFIG_PORT = "JETTY_PORT";
	public static final String JETTY_CONFIG_START_WITH_JADE = "JETTY_START_WITH_JADE";
	
	private static JettyRuntime jettyRuntime;

	private IEclipsePreferences eclipsePreferences;
	private Server serverInstance;

	
	/**
	 * Instantiates a new jetty runtime.
	 */
	private JettyRuntime() { }
	/**
	 * Returns the single instance of JettyRuntime.
	 * @return single instance of JettyRuntime
	 */
	public static JettyRuntime getInstance() {
		if (jettyRuntime==null) {
			jettyRuntime = new JettyRuntime();
		}
		return jettyRuntime;
	}
	
	
	/**
	 * Start the Jetty server.
	 * @return the server
	 */
	public Server startServer() {
		
		Server server = new Server(this.getEclipsePreferences().getInt(JETTY_CONFIG_PORT, 8080));
		try {
			server.start();
//			server.join();
			this.setServer(server);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return this.getServer();
	}
	
	/**
	 * Stops the Jetty server.
	 * @return true, if successful
	 */
	public boolean stopServer() {
		
		boolean stopped = false;
		if (this.isServerExecuted()==true && this.getServer().isStopping()==false) {
			try {
				this.getServer().stop();
				stopped = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		} else {
			stopped = true;
		}
		return stopped;
	}
	/**
	 * Checks if the server is executed.
	 * @return true, if the server is executed
	 */
	public boolean isServerExecuted() {
		if (this.getServer()!=null) {
			return this.getServer().isRunning();
		}
		return false;
	}

	/**
	 * Returns the current Jetty server instance.
	 * @return the server (may return <code>null</code>)
	 */
	public Server getServer() {
		return serverInstance;
	}
	/**
	 * Sets the current server.
	 * @param serverInstance the new server
	 */
	private void setServer(Server serverInstance) {
		this.serverInstance = serverInstance;
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
