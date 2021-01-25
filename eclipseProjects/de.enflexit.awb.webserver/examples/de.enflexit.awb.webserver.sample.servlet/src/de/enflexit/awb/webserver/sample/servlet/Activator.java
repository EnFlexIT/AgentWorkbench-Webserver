package de.enflexit.awb.webserver.sample.servlet;

import java.util.Hashtable;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Class Activator.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class Activator implements BundleActivator {

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		
		//1. We create a Servlet Handler
		ServletHandler handler = new ServletHandler();

		//2. We register our Servlet and its URL mapping
		handler.addServletWithMapping(AWBSampleServlet.class, "/*");

		//3. We are creating a Servlet Context handler
		ServletContextHandler ch = new ServletContextHandler();
		
		//4. We are defining the context path
		ch.setContextPath("/awbSampleServlet");
		
		//5. We are attaching our servlet handler
		ch.setServletHandler(handler);
		
		//6. We are creating an empty Hashtable as the properties
		Hashtable<String, String> props = new Hashtable<>();
		
		// 7. Here we register the ServletContextHandler as the OSGi service
		bundleContext.registerService(ContextHandler.class.getName(), ch, props);
		
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		
	}

}