package de.enflexit.awb.webserver;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jetty.osgi.boot.OSGiServerConstants;

/**
 * The Class JettyConfiguration .
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyConfiguration extends Hashtable<String, JettyParameterValue<?>> {

	private static final long serialVersionUID = 1098203559452315494L;

	private static final Boolean[] valueRangeBoolean = new Boolean[] {true, false};
	
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
			JettyParameterValue<?> jettyParamValue = null;
			
			//OSGiServerConstants.JETTY_BASE
			
//			switch (jettyConfigKey) {
//			case JettyConstants.HTTP_ENABLED:
//				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, true), valueRangeBoolean);
//				break;
//			case JettyConstants.HTTP_PORT:
//				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 8080), null);
//				break;
//			case JettyConstants.HTTP_HOST:
//				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, "0.0.0.0"), null);
//				break;
//			case JettyConstants.HTTP_NIO:
//				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, true), valueRangeBoolean);
//				break;
//				
//			case JettyConstants.HTTP_MINTHREADS:
//				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 8), null);
//				break;				
//			case JettyConstants.HTTP_MAXTHREADS:
//				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 200), null);
//				break;
//				
//			case JettyConstants.HTTPS_ENABLED:
//				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, false), valueRangeBoolean);
//				break;
//			case JettyConstants.HTTPS_PORT:
//				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 8443), null);
//				break;
//			case JettyConstants.HTTPS_HOST:
//				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, "0.0.0.0"), null);
//				break;
//				
//			case JettyConstants.SSL_KEYSTORE:
//				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
//				break;
//			case JettyConstants.SSL_PASSWORD:
//				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
//				break;
//			case JettyConstants.SSL_KEYPASSWORD:
//				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
//				break;
//
//			case JettyConstants.SSL_NEEDCLIENTAUTH:
//				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, false), valueRangeBoolean);
//				break;
//			case JettyConstants.SSL_WANTCLIENTAUTH:
//				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, false), valueRangeBoolean);
//				break;
//				
//			case JettyConstants.SSL_PROTOCOL:
//				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
//				break;
//			case JettyConstants.SSL_ALGORITHM:
//				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
//				break;
//			case JettyConstants.SSL_KEYSTORETYPE:
//				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
//				break;
//				
//			case JettyConstants.CONTEXT_PATH:
//				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
//				break;
//			case JettyConstants.CONTEXT_SESSIONINACTIVEINTERVAL:
//				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 300), null);
//				break;
//				
//			case JettyConstants.CUSTOMIZER_CLASS:
//				// TODO
//				break;
//			}

			// --- save in local hash table ---------------
			if (jettyParamValue==null) continue;
			try {
				this.put(jettyConfigKey, jettyParamValue);
			} catch (Exception ex) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Error setting jetty configuration! -  key: " + jettyConfigKey + ", value: " + jettyParamValue.getValue());
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
			
			Object jettyConfigValue = null;
			JettyParameterValue<?> jpv = this.get(jettyConfigKey);
			if (jpv!=null) jettyConfigValue = jpv.getValue();

			if (jettyConfigValue==null) continue;
			
//			switch (jettyConfigKey) {
//			case JettyConstants.HTTP_ENABLED:
//				this.putBoolean(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.HTTP_PORT:
//				this.putInt(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.HTTP_HOST:
//				this.putString(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.HTTP_NIO:
//				this.putBoolean(jettyConfigKey, jettyConfigValue);
//				break;
//				
//			case JettyConstants.HTTP_MINTHREADS:
//				this.putInt(jettyConfigKey, jettyConfigValue);
//				break;				
//			case JettyConstants.HTTP_MAXTHREADS:
//				this.putInt(jettyConfigKey, jettyConfigValue);
//				break;
//				
//			case JettyConstants.HTTPS_ENABLED:
//				this.putBoolean(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.HTTPS_PORT:
//				this.putInt(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.HTTPS_HOST:
//				this.putString(jettyConfigKey, jettyConfigValue);
//				break;
//				
//			case JettyConstants.SSL_KEYSTORE:
//				this.putString(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.SSL_PASSWORD:
//				this.putString(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.SSL_KEYPASSWORD:
//				this.putString(jettyConfigKey, jettyConfigValue);
//				break;
//
//			case JettyConstants.SSL_NEEDCLIENTAUTH:
//				this.putBoolean(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.SSL_WANTCLIENTAUTH:
//				this.putBoolean(jettyConfigKey, jettyConfigValue);
//				break;
//				
//			case JettyConstants.SSL_PROTOCOL:
//				this.putString(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.SSL_ALGORITHM:
//				this.putString(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.SSL_KEYSTORETYPE:
//				this.putString(jettyConfigKey, jettyConfigValue);
//				break;
//				
//			case JettyConstants.CONTEXT_PATH:
//				this.putString(jettyConfigKey, jettyConfigValue);
//				break;
//			case JettyConstants.CONTEXT_SESSIONINACTIVEINTERVAL:
//				this.putInt(jettyConfigKey, jettyConfigValue);
//				break;
//				
//			case JettyConstants.CUSTOMIZER_CLASS:
//				// TODO
//				break;
//			}
		} // end for 
	
		// --- Save the overall eclipse preferences -------
		AwbWebServerPlugin.getJettyRuntime().saveEclipsePreferences();
	}
	
	
	/**
	 * Puts a string value to the bundle preferences.
	 *
	 * @param jettyConfigKey the jetty configuration key
	 * @param value the value
	 */
	private void putString(String jettyConfigKey, Object value) {
		if (value!=null) {
			if (value instanceof String) {
				this.getBundlePreferences().put(jettyConfigKey, (String)value);
			} else {
				this.getBundlePreferences().put(jettyConfigKey, value.toString());
			}
		}
	}
	/**
	 * Puts a Integer value to the bundle preferences.
	 *
	 * @param jettyConfigKey the jetty configuration key
	 * @param value the value
	 */
	private void putInt(String jettyConfigKey, Object value) {
		if (value!=null) {
			if (value instanceof Integer) {
				this.getBundlePreferences().putInt(jettyConfigKey, (int)value);
			}
		}
	}
	/**
	 * Puts a Boolean value to the bundle preferences.
	 *
	 * @param jettyConfigKey the jetty configuration key
	 * @param value the value
	 */
	private void putBoolean(String jettyConfigKey, Object value) {
		if (value!=null) {
			if (value instanceof Boolean) {
				this.getBundlePreferences().putBoolean(jettyConfigKey, (boolean)value);
			}
		}
	}
	/**
	 * Gets the bundle preferences.
	 * @return the bundle preferences
	 */
	private IEclipsePreferences getBundlePreferences() {
		if (bundlePreferences==null) {
			bundlePreferences = AwbWebServerPlugin.getJettyRuntime().getEclipsePreferences();
		}
		return bundlePreferences;
	}

	
	/**
	 * Gets the jetty configuration keys.
	 * @return the jetty configuration keys
	 */
	public List<String> getJettyConfigurationKeys() {
		if (jettyConfigurationKeys==null) {
			jettyConfigurationKeys = new ArrayList<>();
			
//			Field[] fields = JettyConstants.class.getDeclaredFields();
			Field[] fields = OSGiServerConstants.class.getDeclaredFields();
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
			//jettyExcludeConstants.add("CUSTOMIZER_CLASS");
			jettyExcludeConstants.add("OTHER_INFO");
			jettyExcludeConstants.add("PROPERTY_PREFIX");
		}
		return jettyExcludeConstants;
	}
	
	
	/**
	 * Return a jetty activator conform configuration dictionary.
	 * @return the jetty activator configuration
	 */
	public Dictionary<String, Object> getJettyActivatorConfiguration() {
		
		Dictionary<String, Object> jettyDictionary = new Hashtable<String, Object>();
		List<String> keys = new ArrayList<>(this.keySet());
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Object value = this.get(key).getValue();
			jettyDictionary.put(key, value);
		}
		return jettyDictionary;
	}
	
}
