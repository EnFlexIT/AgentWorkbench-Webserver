/**
 * ***************************************************************
 * Agent.GUI is a framework to develop Multi-agent based simulation 
 * applications based on the JADE - Framework in compliance with the 
 * FIPA specifications. 
 * Copyright (C) 2010 Christian Derksen and DAWIS
 * http://www.dawis.wiwi.uni-due.de
 * http://sourceforge.net/projects/agentgui/
 * http://www.agentgui.org 
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package de.enflexit.ws.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import de.enflexit.ws.core.JettyHomeContentProvider.FileToProvide;


/**
 * The Class LogbackConfiguration adjusts the configuration of logback for the Webserver.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class LogbackConfiguration {

	/**
	 * Reads the configuration.
	 */
	public static void readConfiguration() {
		try {
			configureLogback();
			doManualLoggerConfiguration();
			
		} catch (JoranException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Do a manual logger configuration as well.
	 */
	private static void doManualLoggerConfiguration() {

		// --- Set level of C3P0 logging output -----------
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		
	}
	
	/**
	 * Configure logback in bundle.
	 *
	 * @param bundle the bundle
	 * @throws JoranException the joran exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void configureLogback() throws JoranException, IOException {
		
		// --- Introduced due a bug under Mac OS --------------------
		ILoggerFactory logFactory = LoggerFactory.getILoggerFactory();
		if (!(logFactory instanceof LoggerContext)) {
			return;
		}
		
		// ----------------------------------------------------------
		// --- Check if the Logback configuration file was set ------
		// ----------------------------------------------------------
		String configFileName = System.getProperty("logback.configurationFile");
		if (configFileName!=null && configFileName.isEmpty()==false) {
			// --- Check if is file and available -------------------
			File configFile = new File(configFileName);
			if (configFile.exists()==true) {
				return;
			}
		}
		
		// --- Configure the logger ---------------------------------
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset();

		// --- Overwrite the log directory property programmatically
		// ??
//		String logDirProperty = WebServerGlobalInfo.getLoggingConfigurationFile(true);
//		context.putProperty("LOG_DIR", logDirProperty);
		
		// --- Extract internal configuration file ------------------
		boolean available = isExternalLogbackFileAvailable(); 
		if (available==false) {
			WebServerGlobalInfo.getJettyHomeContentProvider().checkAndProvideContent(FileToProvide.LOGBACK_CONFIGURATION);
		}
		
		// --- Check if configuration file is available now ---------   
		if (available==true) {
			// --- Open external logback.xml ------------------------
			jc.doConfigure(WebServerGlobalInfo.getLoggingConfigurationFile().getAbsolutePath());	
		} else {
			// --- This takes the logback.xml from the bundle root --
			URL logbackConfigFileUrl = getInternalLogbackFileURL();
			jc.doConfigure(logbackConfigFileUrl.openStream());
		}
		//StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}
	
	/**
	 * Checks if the external logback configuration file is available.
	 * @return true, if the file is available
	 */
	private static boolean isExternalLogbackFileAvailable() {
		File logbackFile = WebServerGlobalInfo.getLoggingConfigurationFile();
		return logbackFile.exists();
	}
	
	
	/**
	 * Returns the internal logback file URL.
	 * @return the internal logback file URL
	 */
	private static URL getInternalLogbackFileURL() {
		String bundleFile = (WebServerGlobalInfo.JETTY_HOME_RELATIVE_PATH + FileToProvide.LOGBACK_CONFIGURATION.toString()).replace("\\", "/");;
		Bundle bundle = FrameworkUtil.getBundle(LogbackConfiguration.class);
		return bundle.getResource(bundleFile);
	}
	
}
