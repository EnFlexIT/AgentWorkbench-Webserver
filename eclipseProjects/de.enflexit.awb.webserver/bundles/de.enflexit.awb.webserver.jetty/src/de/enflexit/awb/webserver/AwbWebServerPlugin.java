package de.enflexit.awb.webserver;

import org.agentgui.gui.swing.project.ProjectWindowTab;

import agentgui.core.application.Application;
import agentgui.core.plugin.PlugIn;
import agentgui.core.project.Project;
import de.enflexit.awb.webserver.gui.JettyControlPanel;

/**
 * The Class AwbWebServerPlugin provides the web server control elements to Agent.Workbench.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class AwbWebServerPlugin extends PlugIn {

	private static JettyRuntime jettyRuntime;
	
	/**
	 * Instantiates the AWB web server plugin.
	 * @param currProject the current project
	 */
	public AwbWebServerPlugin(Project currProject) {
		super(currProject);
	}

	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#getName()
	 */
	@Override
	public String getName() {
		return Application.getApplicationTitle() + " - Jetty PlugIn";
	}

	
	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#onPlugIn()
	 */
	@Override
	public void onPlugIn() {
		AwbWebServerPlugin.getJettyRuntime();
		this.addJettyTab();
		super.onPlugIn();
	}
	
	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#onPlugOut()
	 */
	@Override
	public void onPlugOut() {
		AwbWebServerPlugin.getJettyRuntime().stopServer();
		AwbWebServerPlugin.setJettyRuntime(null);
		super.onPlugOut();
	}
	
	
	/**
	 * Adds the Jetty control panel tab.
	 */
	private void addJettyTab() {
		ProjectWindowTab configPWT = this.project.getProjectEditorWindow().getTabForSubPanels(ProjectWindowTab.TAB_4_SUB_PANES_Configuration);
		ProjectWindowTab pwt = new ProjectWindowTab(this.project, ProjectWindowTab.DISPLAY_4_END_USER, "Jetty Configuration", null, null, new JettyControlPanel(this.project), configPWT.getTitle());
		this.addProjectWindowTab(pwt);
	}
	
	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#onProjectSaved(boolean)
	 */
	@Override
	protected void onProjectSaved(boolean isExcludeSetup) {
		AwbWebServerPlugin.getJettyRuntime().getJettyConfiguration().save();		
	}
	
	
	/**
	 * Return the current {@link JettyRuntime} instance.
	 * @return the jetty runtime
	 */
	public static JettyRuntime getJettyRuntime() {
		if (jettyRuntime==null) {
			jettyRuntime = new JettyRuntime();
		}
		return jettyRuntime;
	}
	/**
	 * Sets the jetty runtime instance.
	 * @param jettyRuntime the new jetty runtime
	 */
	private static void setJettyRuntime(JettyRuntime jettyRuntime) {
		AwbWebServerPlugin.jettyRuntime = jettyRuntime;
	}
}
