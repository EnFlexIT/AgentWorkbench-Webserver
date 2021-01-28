package de.enflexit.ws.core;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.osgi.framework.Bundle;

/**
 * The Class WebServerGlobalInfo provides (static) help methods for the current environment.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class WebServerGlobalInfo {

	private static final Logger LOG = Log.getLogger(WebServerGlobalInfo.class);
	
	public static final String SYS_PROP_JETTY_HOME = "jetty.home";
	
	public static final String JETTY_HOME_RELATIVE_PATH = "jetty";
	public static final String JETTY_ETC_RELATIVE_PATH = "etc";
	public static final String JETTY_RESOURCES_RELATIVE_PATH = "resources";
	public static final String JETTY_WEBAPPS_RELATIVE_PATH = "webapps";
	
	public static final String LOGGING_CONFIGURATION_FILE = "logback.xml";
	
	
	private static JettyHomeContentProvider jettyHomeContentProvider;
	
	/**
	 * Prints the return values of the local methods into the local logger.
	 */
	public static void debugDirectoryInfo() {
		
		LOG.debug("Execution base directory is " + getExecutionBaseDirectory());
		LOG.debug("'" + SYS_PROP_JETTY_HOME + "' is " + getJettyHomeDirectory());
		LOG.debug("Jetty etc directory is " + getJettyEtcDirectory());
		
		LOG.debug("Logging configuration directory is " + getLoggingConfigurationDirectory());
		LOG.debug("Logging configuration file is " + getLoggingConfigurationFile());
	}
	
	/**
	 * Returns the execution base directory.
	 * @return the execution base directory
	 */
	public static File getExecutionBaseDirectory() {
		
		File baseDir = null;
		try {
			// ----------------------------------------------------------------			
			// --- Get initial base directory by checking this class location -
			// ----------------------------------------------------------------
			baseDir = new File(WebServerGlobalInfo.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			if (baseDir.getAbsolutePath().contains("%20")==true) {
				try {
					baseDir = new File(WebServerGlobalInfo.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
				} catch (URISyntaxException uriEx) {
					uriEx.printStackTrace();
				}
			}
		
			// ----------------------------------------------------------------
			// --- Examine the path reference found and possibly correct ------
			// ----------------------------------------------------------------
			String pathFound = baseDir.getAbsolutePath();
			String baseDirPath = null;
			if (pathFound.endsWith(".jar") && pathFound.contains(File.separator + "plugins" + File.separator)) {
				// --- OSGI runtime environment -------------------------------
				int cutAt = pathFound.indexOf("plugins" + File.separator);
				baseDirPath = pathFound.substring(0, cutAt);
				baseDir = new File(baseDirPath);
				
			} else {
				// --- IDE environment ----------------------------------------
				baseDirPath = baseDir + File.separator;
				if (baseDir.getAbsolutePath().endsWith("bin")) {
					baseDirPath = baseDir.getParent() + File.separator;
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return baseDir;
	}

	/**
	 * Returns the plugin directory of the current runtime.
	 * @return the plugin directory of the current runtime
	 */
	public static File getPluginDirectory() {
		
		// --- Resolve the bundle 'org.eclipse.core.runtime' --------
		Bundle eqBundle = Platform.getBundle("org.eclipse.core.runtime");

		File pluginDir = null;
		try {
			// --- Get URL of runtime bundle ------------------------
			URL resolvedURL = FileLocator.resolve(eqBundle.getEntry("/"));
			
			// --- Extract 'file:' indicator ------------------------
			String filePathName = resolvedURL.toExternalForm();
			int cutAt = filePathName.indexOf("file:/") + "file:/".length();
			if (cutAt>-1) {
				filePathName = filePathName.substring(cutAt);
			}
			
			// --- Get file object ----------------------------------
			File eqFile = new File(filePathName);
			pluginDir = eqFile.getParentFile();
			
		} catch (Exception ex) { 
			ex.printStackTrace();
		}		
		return pluginDir;
	}
	
	/**
	 * Returns the system property jetty home directory. If not yet set in the   
	 * system properties (VM arguments), the value will automatically be set.
	 * @return the system property jetty home directory
	 */
	public static File getJettyHomeDirectory() {
		return getJettyHomeDirectory(false);
	}
	/**
	 * Returns the system property jetty home directory. If not yet set in the   
	 * system properties (VM arguments), the value will automatically be set.
	 * @param checkCreation the indicator to check and possibly create the directory
	 * @return the system property jetty home directory
	 */
	public static File getJettyHomeDirectory(boolean checkCreation) {
		
		String jettyHomeDirPath = System.getProperty(SYS_PROP_JETTY_HOME);
		if (jettyHomeDirPath==null || jettyHomeDirPath.isEmpty()==true) {

			File execBaseDir = getExecutionBaseDirectory();
			jettyHomeDirPath = execBaseDir.getAbsolutePath() + File.separator + JETTY_HOME_RELATIVE_PATH;;
			System.setProperty(SYS_PROP_JETTY_HOME, jettyHomeDirPath);
			LOG.info("System property '" + SYS_PROP_JETTY_HOME + "' was automatically set to " + jettyHomeDirPath);
		}
		
		File jettyHomeDir = new File(jettyHomeDirPath);
		if (checkCreation==true) {
			checkDirectoryCreation(jettyHomeDir);
		}
		return jettyHomeDir;
	}
	

	/**
	 * Returns the jetty etc directory.
	 * @return the jetty etc directory
	 */
	public static File getJettyEtcDirectory() {
		return getJettyEtcDirectory(false);
	}
	/**
	 * Returns the jetty etc directory.
	 * @param checkCreation the indicator to check and possibly create the directory
	 * @return the jetty etc directory
	 */
	public static File getJettyEtcDirectory(boolean checkCreation) {
		
		File jettyHomeDir = getJettyHomeDirectory();
		String jettyEtcDirPath = jettyHomeDir.getAbsolutePath() + File.separator + JETTY_ETC_RELATIVE_PATH;
		File jettyEtcDir = new File(jettyEtcDirPath);
		if (checkCreation==true) {
			checkDirectoryCreation(jettyEtcDir);
		}
		return jettyEtcDir;
	}
	
	
	/**
	 * Returns the logging configuration directory.
	 * @return the logging configuration directory
	 */
	public static File getLoggingConfigurationDirectory() {
		return getLoggingConfigurationDirectory(false);
	}
	/**
	 * Returns the logging configuration directory.
	 * @param checkCreation the indicator to check and possibly create the directory
	 * @return the logging configuration directory
	 */
	public static File getLoggingConfigurationDirectory(boolean checkCreation) {
		File jettyHomeDir = getJettyHomeDirectory();
		String logConfigDirPath = jettyHomeDir.getAbsolutePath() + File.separator + JETTY_RESOURCES_RELATIVE_PATH;
		File logConfigDir = new File(logConfigDirPath);
		if (checkCreation==true) {
			checkDirectoryCreation(logConfigDir);
		}
		return logConfigDir;
	}
	
	/**
	 * Returns the logging configuration file.
	 * @return the logging configuration file
	 */
	public static File getLoggingConfigurationFile() {
		File logConfigDir = getLoggingConfigurationDirectory();
		String logConfigDirPath = logConfigDir.getAbsolutePath() + File.separator + LOGGING_CONFIGURATION_FILE; 
		return new File(logConfigDirPath);
	}
	
	
	/**
	 * Returns the webApp directory.
	 * @return the webApp directory
	 */
	public static File getWebAppDirectory() {
		return getWebAppDirectory(false);
	}
	/**
	 * Returns the webApp directory.
	 * @param checkCreation the indicator to check and possibly create the directory
	 * @return the webApp directory
	 */
	public static File getWebAppDirectory(boolean checkCreation) {
		File jettyHomeDir = getJettyHomeDirectory();
		String webAppDirPath = jettyHomeDir.getAbsolutePath() + File.separator + JETTY_WEBAPPS_RELATIVE_PATH;
		File webAppDir = new File(webAppDirPath);
		if (checkCreation==true) {
			checkDirectoryCreation(webAppDir);
		}
		return webAppDir;
	}
	
	
	/**
	 * Checks if the specified directory is created. If not, it will be tried to create the directory.
	 * @param directory the directory
	 */
	public static void checkDirectoryCreation(File directory) {
		
		if (directory==null || directory.isFile()==true) {
			LOG.warn("The specified file object is not a direcory!");
		} else {
			if (directory.exists()==false) {
				try {
					directory.mkdirs();
				} catch (Exception ex) {
					LOG.warn(ex);
				}
			}
		}
	}
	
	/**
	 * Returns the jetty home content provider.
	 * @return the jetty home content provider
	 */
	public static JettyHomeContentProvider getJettyHomeContentProvider() {
		if (jettyHomeContentProvider==null) {
			jettyHomeContentProvider = new JettyHomeContentProvider(getJettyHomeDirectory(true));
		}
		return jettyHomeContentProvider;
	}
	
}
