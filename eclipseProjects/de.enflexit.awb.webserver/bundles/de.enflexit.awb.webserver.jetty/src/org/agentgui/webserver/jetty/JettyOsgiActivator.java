package org.agentgui.webserver.jetty;

import org.agentgui.PlugInActivator;
import org.agentgui.webserver.jetty.install.JettyConfigurationProvider;
import org.eclipse.jetty.osgi.boot.JettyBootstrapActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * The Class JettyOsgiActivator will bridges to the {@link JettyBootstrapActivator} and will activate it.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyOsgiActivator implements BundleActivator, BundleListener {

	private BundleContext myBundleContext;
	private JettyBootstrapActivator jettyActivator;
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.myBundleContext = context;
		this.myBundleContext.addBundleListener(this);
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		this.myBundleContext.removeBundleListener(this);
		this.myBundleContext = null;
		this.getJettyActivator().stop(context);
		this.setJettyActivator(null);
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
	 */
	@Override
	public void bundleChanged(BundleEvent event) {

		// --- Wait until the core Agent.Workbench bundle started ---
		Bundle bundle = event.getBundle();
		if (bundle.getSymbolicName().equals(PlugInActivator.PLUGIN_ID) && event.getType()==BundleEvent.STARTED) {
			try {
				this.checkJettyConfigurationFiles();
				
				// --- TODO ---
				// Have a look to https://examples.javacodegeeks.com/enterprise-java/jetty/jetty-osgi-example/
				// to ensure that the jetty-osgi-boot is properly located
				
				this.getJettyActivator().start(this.myBundleContext);
				
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the jetty configuration provider.
	 * @return the jetty configuration provider
	 */
	private void checkJettyConfigurationFiles() {
		JettyConfigurationProvider configProvider = new JettyConfigurationProvider();
		configProvider.checkAndProvideFullConfigurationContent();
	}
	
	/**
	 * Returns the jetty activator.
	 * @return the jetty activator
	 */
	private JettyBootstrapActivator getJettyActivator() {
		if (jettyActivator==null) {
			jettyActivator = new JettyBootstrapActivator();
		}
		return jettyActivator;
	}
	/**
	 * Sets the local instance of the jetty activator.
	 * @param jettyActivator the new jetty activator
	 */
	private void setJettyActivator(JettyBootstrapActivator jettyActivator) {
		this.jettyActivator = jettyActivator;
	}

}
