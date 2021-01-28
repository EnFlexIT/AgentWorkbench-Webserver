package de.enflexit.ws.awb.config;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.equinox.http.jetty.JettyConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The Class JettyConfiguration.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyConfiguration extends Hashtable<String, JettyParameterValue<?>> {

	private static final long serialVersionUID = 1098203559452315494L;

	public enum JettyLifeCycleState {
		Stopped(new Color(0, 153, 0)),
		Stopping(new Color(153, 153, 0)),
		Started(new Color(153, 0, 0)),
		Starting(new Color(153, 153, 0));
		
		private final Color color;
		private JettyLifeCycleState(final Color color) {
			this.color = color;
		}
		/**
		 * Returns the color for the current Jetty state.
		 * @return the color
		 */
		public Color getColor() {
			return this.color;
		}
	}
	
	private static final Boolean[] valueRangeBoolean = new Boolean[] {true, false};

	
	private List<String> jettyConfigurationKeys;
	private List<String> jettyExcludeConstants;

	private IEclipsePreferences eclipsePreferences;
	
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
		IEclipsePreferences bundlePrefs = this.getEclipsePreferences();
		for (int i = 0; i < jettyConfigKeys.size(); i++) {

			String jettyConfigKey = jettyConfigKeys.get(i);
			JettyParameterValue<?> jettyParamValue = null;
			
			switch (jettyConfigKey) {
			case JettyConstants.HTTP_ENABLED:
				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, true), valueRangeBoolean);
				break;
			case JettyConstants.HTTP_PORT:
				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 8080), null);
				break;
			case JettyConstants.HTTP_HOST:
				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, "0.0.0.0"), null);
				break;
			case JettyConstants.HTTP_NIO:
				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, true), valueRangeBoolean);
				break;
				
			case JettyConstants.HTTP_MINTHREADS:
				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 8), null);
				break;				
			case JettyConstants.HTTP_MAXTHREADS:
				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 200), null);
				break;
				
			case JettyConstants.HTTPS_ENABLED:
				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, false), valueRangeBoolean);
				break;
			case JettyConstants.HTTPS_PORT:
				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 8443), null);
				break;
			case JettyConstants.HTTPS_HOST:
				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, "0.0.0.0"), null);
				break;
				
			case JettyConstants.SSL_KEYSTORE:
				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
				break;
			case JettyConstants.SSL_PASSWORD:
				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
				break;
			case JettyConstants.SSL_KEYPASSWORD:
				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
				break;

			case JettyConstants.SSL_NEEDCLIENTAUTH:
				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, false), valueRangeBoolean);
				break;
			case JettyConstants.SSL_WANTCLIENTAUTH:
				jettyParamValue = new JettyParameterValue<Boolean>(jettyConfigKey, bundlePrefs.getBoolean(jettyConfigKey, false), valueRangeBoolean);
				break;
				
			case JettyConstants.SSL_PROTOCOL:
				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
				break;
			case JettyConstants.SSL_ALGORITHM:
				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
				break;
			case JettyConstants.SSL_KEYSTORETYPE:
				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
				break;
				
			case JettyConstants.CONTEXT_PATH:
				jettyParamValue = new JettyParameterValue<String>(jettyConfigKey, bundlePrefs.get(jettyConfigKey, ""), null);
				break;
			case JettyConstants.CONTEXT_SESSIONINACTIVEINTERVAL:
				jettyParamValue = new JettyParameterValue<Integer>(jettyConfigKey, bundlePrefs.getInt(jettyConfigKey, 300), null);
				break;
				
			case JettyConstants.CUSTOMIZER_CLASS:
				// TODO
				break;
			}

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
			
			switch (jettyConfigKey) {
			case JettyConstants.HTTP_ENABLED:
				this.putBooleanToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTP_PORT:
				this.putIntToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTP_HOST:
				this.putStringToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTP_NIO:
				this.putBooleanToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.HTTP_MINTHREADS:
				this.putIntToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;				
			case JettyConstants.HTTP_MAXTHREADS:
				this.putIntToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.HTTPS_ENABLED:
				this.putBooleanToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTPS_PORT:
				this.putIntToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.HTTPS_HOST:
				this.putStringToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.SSL_KEYSTORE:
				this.putStringToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_PASSWORD:
				this.putStringToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_KEYPASSWORD:
				this.putStringToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;

			case JettyConstants.SSL_NEEDCLIENTAUTH:
				this.putBooleanToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_WANTCLIENTAUTH:
				this.putBooleanToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.SSL_PROTOCOL:
				this.putStringToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_ALGORITHM:
				this.putStringToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.SSL_KEYSTORETYPE:
				this.putStringToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.CONTEXT_PATH:
				this.putStringToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
			case JettyConstants.CONTEXT_SESSIONINACTIVEINTERVAL:
				this.putIntToBundlePreferences(jettyConfigKey, jettyConfigValue);
				break;
				
			case JettyConstants.CUSTOMIZER_CLASS:
				// TODO
				break;
			}
		} // end for 
	
		// --- Save the overall eclipse preferences -------
		this.saveEclipsePreferences();
	}
	
	
	/**
	 * Returns the configured string that can be found in the local instance.
	 *
	 * @param configKey the configuration key
	 * @param defaultValue the default value
	 * @return the configured or default string 
	 */
	public String getConfiguredString(String configKey, String defaultValue) {
		String value = null;
		Object valueObject = this.get(configKey)!=null ? this.get(configKey).getValue() : null;
		if (valueObject instanceof String) {
			value = (String) valueObject;
		}
		if (value==null) {
			value = defaultValue;
		}
		return value;
	}
	/**
	 * Returns the configured boolean that can be found in the local instance.
	 *
	 * @param configKey the configuration key
	 * @param defaultValue the default value
	 * @return the configured or default boolean value 
	 */
	public boolean getConfiguredBoolean(String configKey, boolean defaultValue) {
		Boolean value = null;
		Object valueObject = this.get(configKey)!=null ? this.get(configKey).getValue() : null;
		if (valueObject instanceof Boolean) {
			value = (Boolean) valueObject;
		}
		if (value==null) {
			value = defaultValue;
		}
		return value;
	}
	/**
	 * Returns the configured Integer that can be found in the local instance.
	 *
	 * @param configKey the configuration key
	 * @param defaultValue the default value
	 * @return the configured or default int value 
	 */
	public int getConfiguredInt(String configKey, int defaultValue) {
		Integer value = null;
		Object valueObject = this.get(configKey)!=null ? this.get(configKey).getValue() : null;
		if (valueObject instanceof Integer) {
			value = (Integer) valueObject;
		}
		if (value==null) {
			value = defaultValue;
		}
		return value;
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
	 * Puts a string value to the bundle preferences.
	 *
	 * @param jettyConfigKey the jetty configuration key
	 * @param value the value
	 */
	private void putStringToBundlePreferences(String jettyConfigKey, Object value) {
		if (value!=null) {
			if (value instanceof String) {
				this.getEclipsePreferences().put(jettyConfigKey, (String)value);
			} else {
				this.getEclipsePreferences().put(jettyConfigKey, value.toString());
			}
		}
	}
	/**
	 * Puts a Integer value to the bundle preferences.
	 *
	 * @param jettyConfigKey the jetty configuration key
	 * @param value the value
	 */
	private void putIntToBundlePreferences(String jettyConfigKey, Object value) {
		if (value instanceof Integer) {
			this.getEclipsePreferences().putInt(jettyConfigKey, (int)value);
		}
	}
	/**
	 * Puts a Boolean value to the bundle preferences.
	 *
	 * @param jettyConfigKey the jetty configuration key
	 * @param value the value
	 */
	private void putBooleanToBundlePreferences(String jettyConfigKey, Object value) {
		if (value instanceof Boolean) {
			this.getEclipsePreferences().putBoolean(jettyConfigKey, (boolean)value);
		}
	}
	
	
	/**
	 * Gets the jetty configuration keys.
	 * @return the jetty configuration keys
	 */
	public List<String> getJettyConfigurationKeys() {
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
	 * Returns the jetty configuration constants to exclude with the local handling.
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
	
}
