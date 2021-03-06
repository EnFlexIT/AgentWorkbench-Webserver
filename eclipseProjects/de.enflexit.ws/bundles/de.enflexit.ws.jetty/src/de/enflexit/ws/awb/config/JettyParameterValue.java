package de.enflexit.ws.awb.config;

/**
 * The Class JettyParameterValue.
 * @param <T> the generic type
 */
public class JettyParameterValue<T> {
	
	private String parameterKey;
	private T value;
	private T[] possibleValues;
	
	/**
	 * Instantiates a new jetty parameter value.
	 *
	 * @param parameterKey the parameter key
	 * @param value the value
	 * @param possibleValues the possible values
	 */
	public JettyParameterValue(String parameterKey, T value, T[] possibleValues) {
		this.setParameterKey(parameterKey);
		this.setValue(value);
		this.setPossibleValues(possibleValues);
	}
	
	/**
	 * Sets the parameter key.
	 * @param parameterKey the new parameter key
	 */
	public void setParameterKey(String parameterKey) {
		this.parameterKey = parameterKey;
	}
	/**
	 * Gets the parameter key.
	 * @return the parameter key
	 */
	public String getParameterKey() {
		return parameterKey;
	}
	
	/**
	 * Return the parameter class.
	 * @return the parameter class
	 */
	public Class<?> getParameterType() {
		if (value==null) return null;
		return value.getClass();
	}
	
	/**
	 * Sets the value.
	 * @param value the new value
	 */
	public void setValue(T value) {
		this.value = value;
	}
	/**
	 * Gets the value.
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Sets the possible values.
	 * @param possibleValues the new possible value
	 */
	public void setPossibleValues(T[] possibleValue) {
		this.possibleValues = possibleValue;
	}
	/**
	 * Returns the possible value or null.
	 * @return the possible value
	 */
	public T[] getPossibleValues() {
		return possibleValues;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getValue().toString();
	}
	
}