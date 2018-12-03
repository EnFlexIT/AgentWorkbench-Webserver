package de.enflexit.awb.webserver;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.equinox.http.jetty.JettyConstants;

/**
 * The Class JettyConfiguration .
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyConfiguration extends Hashtable<String, Object> {

	private static final long serialVersionUID = 1098203559452315494L;

	private List<String> jettyConfigurationKeys;
	private List<String> jettyExcludeConstants;

	private IEclipsePreferences bundlePreferences;

	
	/**
	 * Instantiates a new jetty configuration.
	 */
	public JettyConfiguration() {
		this.load();
	}

	/**
	 * Loads the jetty configuration from the eclipse preferences.
	 */
	public void load() {
		
		List<String> jettyConfigKeys = this.getJettyConfigurationKeys();
		IEclipsePreferences bundlePrefs = this.getBundlePreferences();
		for (int i = 0; i < jettyConfigKeys.size(); i++) {

			String jettyConfigKey = jettyConfigKeys.get(i);
			Object jettyConfigValue = null;
			
			switch (jettyConfigKey) {
			case JettyConstants.HTTP_ENABLED:
				jettyConfigValue = bundlePrefs.getBoolean(jettyConfigKey, true);
				break;
			case JettyConstants.HTTP_PORT:
				jettyConfigValue = bundlePrefs.getInt(jettyConfigKey, 8080);
				break;
			case JettyConstants.HTTP_HOST:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "0.0.0.0");
				break;
			case JettyConstants.HTTP_NIO:
				jettyConfigValue = bundlePrefs.getBoolean(jettyConfigKey, true);
				break;
				
			case JettyConstants.HTTPS_ENABLED:
				jettyConfigValue = bundlePrefs.getBoolean(jettyConfigKey, false);
				break;
			case JettyConstants.HTTPS_PORT:
				jettyConfigValue = bundlePrefs.getInt(jettyConfigKey, 8443);
				break;
			case JettyConstants.HTTPS_HOST:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "0.0.0.0");
				break;
				
			case JettyConstants.HTTP_MINTHREADS:
				jettyConfigValue = bundlePrefs.getInt(jettyConfigKey, 8);
				break;				
			case JettyConstants.HTTP_MAXTHREADS:
				jettyConfigValue = bundlePrefs.getInt(jettyConfigKey, 200);
				break;
				
			case JettyConstants.SSL_KEYSTORE:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "");
				break;
			case JettyConstants.SSL_PASSWORD:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "");
				break;
			case JettyConstants.SSL_KEYPASSWORD:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "");
				break;

			case JettyConstants.SSL_NEEDCLIENTAUTH:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "");
				break;
			case JettyConstants.SSL_WANTCLIENTAUTH:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "");
				break;
				
			case JettyConstants.SSL_PROTOCOL:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "");
				break;
			case JettyConstants.SSL_ALGORITHM:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "");
				break;
			case JettyConstants.SSL_KEYSTORETYPE:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "");
				break;
				
			case JettyConstants.CONTEXT_PATH:
				jettyConfigValue = bundlePrefs.get(jettyConfigKey, "");
				break;
			case JettyConstants.CONTEXT_SESSIONINACTIVEINTERVAL:
				jettyConfigValue = bundlePrefs.getInt(jettyConfigKey, 300);
				break;
			}

			// --- save in local hash table ---------------
			if (jettyConfigValue==null) continue;
			try {
				this.put(jettyConfigKey, jettyConfigValue);
			} catch (Exception ex) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Error setting jetty configuration! -  key: " + jettyConfigKey + ", value: " + jettyConfigValue);
			}
		}
		
	}
	
	/**
	 * Saves the current jetty configuration to the eclipse preferences.
	 */
	public void save() {
		
		List<String> jettyConfigKeys = this.getJettyConfigurationKeys();
		for (int i = 0; i < jettyConfigKeys.size(); i++) {

			String jettyConfigKey = jettyConfigKeys.get(i);
			Object jettyConfigValue = this.get(jettyConfigKey);
			if (jettyConfigValue==null) continue;
			
			switch (jettyConfigKey) {
			case JettyConstants.HTTP_ENABLED:
				this.putBoolean(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTP_PORT:
				this.putInt(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTP_HOST:
				this.putString(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTP_NIO:
				this.putBoolean(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.HTTPS_ENABLED:
				this.putBoolean(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTPS_PORT:
				this.putInt(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTPS_HOST:
				this.putString(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.HTTP_MINTHREADS:
				this.putInt(jettyConfigKey, jettyConfigValue);
				break;				
			case JettyConstants.HTTP_MAXTHREADS:
				this.putInt(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.SSL_KEYSTORE:
				this.putString(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_PASSWORD:
				this.putString(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_KEYPASSWORD:
				this.putString(jettyConfigKey, jettyConfigValue);
				break;

			case JettyConstants.SSL_NEEDCLIENTAUTH:
				this.putBoolean(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_WANTCLIENTAUTH:
				this.putBoolean(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.SSL_PROTOCOL:
				this.putString(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_ALGORITHM:
				this.putString(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_KEYSTORETYPE:
				this.putString(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.CONTEXT_PATH:
				this.putString(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.CONTEXT_SESSIONINACTIVEINTERVAL:
				this.putInt(jettyConfigKey, jettyConfigValue);
				break;
			}
		} // end for 
	
		// --- Save the overall eclipse preferences -------
		JettyRuntime.getInstance().saveEclipsePreferences();
	}
	
	
	private void putString(String jettyConfigKey, Object value) {
		if (value!=null) {
			if (value instanceof String) {
				this.getBundlePreferences().put(jettyConfigKey, (String)value);
			} else {
				this.getBundlePreferences().put(jettyConfigKey, value.toString());
			}
		}
	}
	private void putInt(String jettyConfigKey, Object value) {
		if (value!=null) {
			if (value instanceof Integer) {
				this.getBundlePreferences().putInt(jettyConfigKey, (int)value);
			}
		}
	}
	private void putBoolean(String jettyConfigKey, Object value) {
		if (value!=null) {
			if (value instanceof Boolean) {
				this.getBundlePreferences().putBoolean(jettyConfigKey, (boolean)value);
			}
		}
	}

	private IEclipsePreferences getBundlePreferences() {
		if (bundlePreferences==null) {
			bundlePreferences = JettyRuntime.getInstance().getEclipsePreferences();
		}
		return bundlePreferences;
	}

	
	/**
	 * Gets the jetty configuration keys.
	 * @return the jetty configuration keys
	 */
	private List<String> getJettyConfigurationKeys() {
		if (jettyConfigurationKeys==null) {
			jettyConfigurationKeys = new ArrayList<>();
			
			Field[] fields = JettyConstants.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				
				Field field = fields[i];
				String fieldName = field.getName();
				if (Modifier.isStatic(field.getModifiers()) && field.getType()==String.class && getJettyExcludeConstants().contains(fieldName)==false) {
					try {
						String fieldValue = (String) field.get(null);
						jettyConfigurationKeys.add(fieldValue);
						
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} 
			}
			
		}
		return jettyConfigurationKeys;
	}
	/**
	 * Gets the jetty exclude constants.
	 * @return the jetty exclude constants
	 */
	private List<String> getJettyExcludeConstants() {
		if (jettyExcludeConstants==null) {
			jettyExcludeConstants = new ArrayList<>();
			jettyExcludeConstants.add("MULTIPART_FILESIZETHRESHOLD");
			jettyExcludeConstants.add("MULTIPART_LOCATION");
			jettyExcludeConstants.add("MULTIPART_MAXFILESIZE");
			jettyExcludeConstants.add("MULTIPART_MAXREQUESTSIZE");
			jettyExcludeConstants.add("CUSTOMIZER_CLASS");
			jettyExcludeConstants.add("OTHER_INFO");
			jettyExcludeConstants.add("PROPERTY_PREFIX");
		}
		return jettyExcludeConstants;
	}
	
}
