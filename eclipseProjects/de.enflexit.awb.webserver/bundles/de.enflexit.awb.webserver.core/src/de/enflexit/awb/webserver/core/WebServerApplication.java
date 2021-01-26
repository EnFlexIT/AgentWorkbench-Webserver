package de.enflexit.awb.webserver.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.osgi.framework.Bundle;

/**
 * The Class WebServerApplication.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class WebServerApplication implements IApplication {

	private static final Logger LOG = Log.getLogger(WebServerApplication.class);
	
	private Bundle localBundle;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Object start(IApplicationContext context) throws Exception {
		
		// --- Remind local bundle ------------------------ 
		this.localBundle = context.getBrandingBundle();

		// --- Check for available configuration files ----
		WebServerGlobalInfo.getJettyHomeContentProvider().checkAndProvideFullContent();
		
		// --- Do the Logging configuration ---------------
		LogbackConfiguration.readConfiguration();
		LOG.warn("Agent.Workbench Webserver " + this.localBundle.getVersion().toString());
		
		// --- Start the OSGI based Jetty -----------------
		OsgiJetty.start();
		OsgiJetty.stop();
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		
		System.out.println(this.localBundle.getSymbolicName() + ": Stop");
		OsgiJetty.stop();
		
	}

	
	
	
}
