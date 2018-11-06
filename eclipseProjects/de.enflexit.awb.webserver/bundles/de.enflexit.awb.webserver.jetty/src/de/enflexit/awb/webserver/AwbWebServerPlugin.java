package de.enflexit.awb.webserver;

import agentgui.core.application.Application;
import agentgui.core.plugin.PlugIn;
import agentgui.core.project.Project;

/**
 * The Class AwbWebServerPlugin provides the web server control elements to Agent.Workbench.
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class AwbWebServerPlugin extends PlugIn {

	/**
	 * Instantiates the Agent.Workbench web server plugin.
	 * @param currProject the current project that uses this PlugIn
	 */
	public AwbWebServerPlugin(Project currProject) {
		super(currProject);
	}

	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#getName()
	 */
	@Override
	public String getName() {
		return Application.getApplicationTitle() + " - Webserver PlugIn for Jetty";
	}

}
