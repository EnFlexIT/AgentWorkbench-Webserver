package de.enflexit.ws.awb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.http.jetty.JettyConstants;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import agentgui.core.application.Application;
import de.enflexit.ws.awb.config.JettyConfiguration;

/**
 * The Class JettyManager serves as central instance to access runtime information
 * as well as the actual Server instance of the Jetty web server.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyManager {

	public static final String JETTY_SUB_PATH = "jetty"; 
	public static final int    JETTY_DEFAULT_IDLE_TIMEOUT = 30000;
	
	public static final String JETTY_CONFIG_START_WITH_JADE = "JETTY_START_WITH_JADE";

	
	private JettyConfiguration jettyConfiguration; 
	private Server server;
	private HandlerList handlerList;
	private List<Listener> lifeCycleListener;
	
	
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
	 * Returns the current server instance.
	 * @return the server instance
	 */
	public Server getServer() {
		return server;
	}
	/**
	 * Sets the server.
	 * @param server the new server
	 */
	public void setServer(Server server) {
		this.server = server;
	}
	
	
	/**
	 * Returns the handler list for the server.
	 * @return the handler list
	 */
	private HandlerList getHandlerList() {
		if (handlerList==null) {
			handlerList = new HandlerList();
		}
		return handlerList;
	}
	/**
	 * Sets the handler list for the server.
	 * @param handlerList the new handler list
	 */
	public void setHandlerList(HandlerList handlerList) {
		this.handlerList = handlerList;
	}
	/**
	 * Adds the specified handler to the list of server handlers.
	 * @param handlerToAdd the handler
	 */
	private void addToHandlerList(Handler handlerToAdd) {
		if (handlerToAdd!=null) {
			this.getHandlerList().addHandler(handlerToAdd);
		}
	}
	
	
	/**
	 * Returns all registered life cycle listener.
	 * @return the life cycle listener
	 */
	private List<Listener> getLifeCycleListener() {
		if (lifeCycleListener==null) {
			lifeCycleListener = new ArrayList<>();
		}
		return lifeCycleListener;
	}
	/**
	 * Adds the specified life cycle listener.
	 * @param lcl the life cycle listener to add
	 */
	public void addLifeCycleListener(Listener lcl) {
		if (this.getLifeCycleListener().contains(lcl)==false) {
			this.getLifeCycleListener().add(lcl);
			// --- Same action directly on server? --------
			if (this.getServer()!=null) {
				this.getServer().addLifeCycleListener(lcl);
			}
		}
	}
	/**
	 * Removes the specified life cycle listener.
	 * @param lcl the life cycle listener to remove
	 */
	public void removeLifeCycleListener(Listener lcl) {
		if (this.getLifeCycleListener().contains(lcl)==false) {
			this.getLifeCycleListener().remove(lcl);
			// --- Same action directly on server? --------
			if (this.getServer()!=null) {
				this.getServer().removeLifeCycleListener(lcl);
			}
		}
	}
	
	
	/**
	 * Start the Jetty server with the current bundle configuration.
	 * @return the server
	 */
	public void startServer() {
		this.startServer(null);
	}
	/**
	 * Start the Jetty server with the specified configuration.
	 *
	 * @param jettyConfig the jetty configuration. If null, the local configuration will be used
	 * @see #getJettyConfiguration()
	 */
	public void startServer(final JettyConfiguration jettyConfig) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				JettyManager.this.startServerInternal(jettyConfig);
				
			}
		}, this.getClass().getSimpleName() + "Thread").start();
		
	}
	/**
	 * Start the Jetty server with the specified configuration.
	 *
	 * @param jettyConfig the jetty configuration. If null, the local configuration will be used
	 * @see #getJettyConfiguration()
	 */
	private void startServerInternal(JettyConfiguration jettyConfig) {
		
		try {

			// --- Check / get default configuration --------------------------
			JettyConfiguration jettyConfigToUse = jettyConfig;
			if (jettyConfigToUse==null) {
				jettyConfigToUse = this.getJettyConfiguration();
			}
			
			// --- Create server instance with Threading settings -------------
			int maxThreadsUse = jettyConfigToUse.getConfiguredInt(JettyConstants.HTTP_MAXTHREADS, 200);
			int minThreadsUse = jettyConfigToUse.getConfiguredInt(JettyConstants.HTTP_MINTHREADS, 8);
			this.server = new Server(new QueuedThreadPool(maxThreadsUse, minThreadsUse));
			
			// --- Add all known life cycle listener --------------------------
			for (int i = 0; i < this.getLifeCycleListener().size(); i++) {
				this.server.addLifeCycleListener(this.getLifeCycleListener().get(i)); 
				
			}

			// --- Set HttpConfiguration --------------------------------------
			HttpConfiguration http_config = new HttpConfiguration();
			// --- Create HTTP connector --------------------------------------
			ServerConnector httpConnector = this.createHttpConnector(jettyConfigToUse, this.server, http_config);
			if (httpConnector!=null) {
				try {
					httpConnector.open();
				} catch (IOException e) {
					throw new IllegalArgumentException(e.getMessage(), e);  
				}
				this.server.addConnector(httpConnector);
			}
			// --- Create HTTP connector --------------------------------------
			ServerConnector httpsConnector = this.createHttpsConnector(jettyConfigToUse, this.server, http_config);
			if (httpsConnector != null) {
				try {
					httpsConnector.open();
				} catch (IOException e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
				this.server.addConnector(httpsConnector);
			}
			
			
			// --- Try to add *.war to the server -----------------------------
			this.addWebApp("D:/async-rest.war", "/async");
			//this.addWebApp("D:/enflexit-licensor.war", "/eom");
			//this.addWebApp("D:/javadoc-proxy.war", "/jdoc");
			this.addWebApp("D:/rapDemo.war", "/");
			
			
			// --- Start the server -------------------------------------------
			this.server.setHandler(this.getHandlerList());
			this.server.start();
			//this.server.join();
			 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * Creates the HTTP ServerConnector if configured so.
	 *
	 * @param jettyConfig the jetty configuration
	 * @param server the server instance
	 * @param http_config the HttpConfiguration to use
	 * @return the server connector
	 */
	private ServerConnector createHttpConnector(JettyConfiguration jettyConfig, Server server, HttpConfiguration http_config) {
		
		boolean isHttpEnabled  = jettyConfig.getConfiguredBoolean(JettyConstants.HTTP_ENABLED, true);
		boolean isHttpsEnabled = jettyConfig.getConfiguredBoolean(JettyConstants.HTTPS_ENABLED, false);
		
		int portHTTP  = jettyConfig.getConfiguredInt(JettyConstants.HTTP_PORT, 8080); 
		int portHTTPS = jettyConfig.getConfiguredInt(JettyConstants.HTTPS_PORT, 8443);
		
		String hostHTTP = jettyConfig.getConfiguredString(JettyConstants.HTTP_HOST, null);   
		
		
		ServerConnector httpConnector = null;
		if (isHttpEnabled==true) {
			// HTTP Configuration
			if (isHttpsEnabled==true) {
				http_config.setSecureScheme("https");
				http_config.setSecurePort(portHTTPS);
			}
			// HTTP connector
			httpConnector = new ServerConnector(server, new HttpConnectionFactory(http_config));
			httpConnector.setPort(portHTTP);
			httpConnector.setHost(hostHTTP);
			httpConnector.setIdleTimeout(JETTY_DEFAULT_IDLE_TIMEOUT);
		}
		return httpConnector;
	}

	
	/**
	 * Creates the HTTPS ServerConnector if configured so.
	 *
	 * @param jettyConfig the jetty configuration
	 * @param server the server instance
	 * @param http_config the HttpConfiguration to use
	 * @return the server connector
	 */
	private ServerConnector createHttpsConnector(JettyConfiguration jettyConfig, Server server, HttpConfiguration http_config) {
		
		boolean isHttpsEnabled = jettyConfig.getConfiguredBoolean(JettyConstants.HTTPS_ENABLED, false);

		int portHTTPS    = jettyConfig.getConfiguredInt(JettyConstants.HTTPS_PORT, 8443);
		String hostHTTPS = jettyConfig.getConfiguredString(JettyConstants.HTTPS_HOST, null);   
		
		String sslKeyStore = jettyConfig.getConfiguredString(JettyConstants.SSL_KEYSTORE, null);
		String sslPassword = jettyConfig.getConfiguredString(JettyConstants.SSL_PASSWORD, null);
		String sslKeyPassword = jettyConfig.getConfiguredString(JettyConstants.SSL_KEYPASSWORD, null);
		String sslKeyStoreType = jettyConfig.getConfiguredString(JettyConstants.SSL_KEYSTORETYPE, "JKS");
		String sslProtocol = jettyConfig.getConfiguredString(JettyConstants.SSL_PROTOCOL, "TLS");
		
		boolean isWantClientAuth = jettyConfig.getConfiguredBoolean(JettyConstants.SSL_WANTCLIENTAUTH, false);
		boolean isNeedClientAuth = jettyConfig.getConfiguredBoolean(JettyConstants.SSL_NEEDCLIENTAUTH, false);
		
		
		ServerConnector httpsConnector = null;
		if (isHttpsEnabled==true) {
			// SSL Context Factory for HTTPS and SPDY
			SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
			//sslContextFactory.setKeyStore(KeyS)

			sslContextFactory.setKeyStorePath(sslKeyStore);
			sslContextFactory.setKeyStorePassword(sslPassword);
			sslContextFactory.setKeyManagerPassword(sslKeyPassword);
			sslContextFactory.setKeyStoreType(sslKeyStoreType);
			sslContextFactory.setProtocol(sslProtocol);
			sslContextFactory.setWantClientAuth(isWantClientAuth);
			sslContextFactory.setNeedClientAuth(isNeedClientAuth);

			// HTTPS Configuration
			HttpConfiguration https_config = new HttpConfiguration(http_config);
			https_config.addCustomizer(new SecureRequestCustomizer());

			// HTTPS connector
			httpsConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https_config)); //$NON-NLS-1$
			httpsConnector.setPort(portHTTPS);
			httpsConnector.setHost(hostHTTPS);
		}
		return httpsConnector;
	}
	
	
	/**
	 * Adds a WebbApp to the specified context by the specified war file.
	 *
	 * @param warFileLocationPath the war file location path
	 * @param contextPath the context path
	 * @return the web app context
	 */
	public WebAppContext addWebApp(String warFileLocationPath, String contextPath) {
	
		if (this.getServer()==null) return null;
		
		WebAppContext webApp = null;
		File warFile = new File(warFileLocationPath);
		if (warFile.exists()==true) {
			// --- Create the webApp ----------------------
			webApp = new WebAppContext();
			webApp.setContextPath(contextPath);
			webApp.setWar(warFile.getAbsolutePath());
			Thread.currentThread().setContextClassLoader(JettyManager.class.getClassLoader());
			// --- Add to the multiple server handler -----
			this.addToHandlerList(webApp);
		}
		return webApp;
	}

	
	
	/**
	 * Stops the Jetty server.
	 * @return true, if successful
	 */
	public void stopServer() {
		
		try {
			if (this.getServer()!=null) {
				this.getServer().stop();
				this.getServer().destroy();
				
				// --- Reset local variables ----
				this.setHandlerList(null);
				this.setServer(null);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
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
