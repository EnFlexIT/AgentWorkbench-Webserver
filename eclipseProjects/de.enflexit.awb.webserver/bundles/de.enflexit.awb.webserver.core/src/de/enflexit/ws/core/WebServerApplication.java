package de.enflexit.ws.core;

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
	
	private Object waitLock;
	private Integer exitState;
	
	/**
	 * Returns the exit state.
	 * @return the exit state
	 */
	private Integer getExitState() {
		if (exitState==null) {
			exitState = IApplication.EXIT_OK;
		}
		return exitState;
	}
	/**
	 * Sets the exit state.
	 * @param exitState the new exit state
	 */
	private void setExitState(int exitState) {
		this.exitState = exitState;
		synchronized (this.getWaitLock()) {
			this.getWaitLock().notifyAll();
		}
	}
	
	/**
	 * Returns the wait lock.
	 * @return the wait lock
	 */
	private Object getWaitLock() {
		if (waitLock==null) {
			waitLock = new Object();
		}
		return waitLock;
	}
	
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
		LOG.info("Starting EnFlex.IT  Webserver (Jetty) " + this.localBundle.getVersion().toString() + " ...");
		LogbackConfiguration.readConfiguration();
		
		// --- Start the OSGI based Jetty -----------------
		OsgiJetty.start();
		
		// --- Start wait process -------------------------
		synchronized (this.getWaitLock()) {
			try {
				this.getWaitLock().wait();
			} catch (Exception ex) {
				LOG.warn(ex);
			}
		}
		return this.getExitState();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		
		LOG.warn("Stopping EnFlex.IT Webserver (Jetty) " + this.localBundle.getVersion().toString() + " ... ");
		
		// --- Stop Jetty ---------------------------------
		OsgiJetty.stop();
		// --- Set exit state -----------------------------
		this.setExitState(IApplication.EXIT_OK);
	}
	/**
	 * Restart.
	 */
	public void restart() {
		this.setExitState(IApplication.EXIT_RESTART);
	}
	/**
	 * Relaunch.
	 */
	public void relaunch() {
		this.setExitState(IApplication.EXIT_OK);
	}
	
}
