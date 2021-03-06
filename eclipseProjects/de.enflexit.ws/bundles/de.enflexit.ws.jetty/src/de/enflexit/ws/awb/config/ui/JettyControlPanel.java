package de.enflexit.ws.awb.config.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;

import agentgui.core.project.Project;
import de.enflexit.ws.awb.AwbWebServerPlugin;
import de.enflexit.ws.awb.JettyManager;
import de.enflexit.ws.awb.config.JettyConfiguration;
import de.enflexit.ws.awb.config.JettyParameterValue;
import de.enflexit.ws.awb.config.JettyConfiguration.JettyLifeCycleState;

/**
 * The Class JettyControlPanel.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyControlPanel extends JPanel implements ActionListener, Listener {

	private static final long serialVersionUID = 5025152245197566098L;

	private Project project;
	private JLabel jLabelJettyHeader;

	private JPanel jPanelControl;
	private JButton jButtonStartJetty;
	private JSeparator jSeparatorControl;
	private JCheckBox jCheckBoxStartWithJade;
	
	private JScrollPane jScrollPaneConfigTable;
	private JTable jTableJettyConfiguration;
	private DefaultTableModel tableModel;
	
	
	/**
	 * Instantiates a new jetty control panel.
	 * @param project the current project instance
	 */
	public JettyControlPanel(Project project) {
		this.project = project;
		this.initialize();
		this.loadSettingsFromPreferences();
	}
	/**
	 * Initialize.
	 */
	private void initialize() {
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gridBagLayout);
		
		GridBagConstraints gbc_jLabelJettyHeader = new GridBagConstraints();
		gbc_jLabelJettyHeader.anchor = GridBagConstraints.WEST;
		gbc_jLabelJettyHeader.insets = new Insets(10, 10, 0, 0);
		gbc_jLabelJettyHeader.gridx = 0;
		gbc_jLabelJettyHeader.gridy = 0;
		this.add(getJLabelJettyHeader(), gbc_jLabelJettyHeader);

		GridBagConstraints gbc_jScrollPaneConfigTable = new GridBagConstraints();
		gbc_jScrollPaneConfigTable.insets = new Insets(10, 10, 0, 0);
		gbc_jScrollPaneConfigTable.fill = GridBagConstraints.BOTH;
		gbc_jScrollPaneConfigTable.gridx = 0;
		gbc_jScrollPaneConfigTable.gridy = 1;
		add(getJScrollPaneConfigTable(), gbc_jScrollPaneConfigTable);
		
		GridBagConstraints gbc_jPanelControl = new GridBagConstraints();
		gbc_jPanelControl.insets = new Insets(10, 10, 0, 10);
		gbc_jPanelControl.anchor = GridBagConstraints.NORTHWEST;
		gbc_jPanelControl.gridx = 1;
		gbc_jPanelControl.gridy = 1;
		add(getJPanelControl(), gbc_jPanelControl);
	}

	private JLabel getJLabelJettyHeader() {
		if (jLabelJettyHeader == null) {
			jLabelJettyHeader = new JLabel("Jetty-Configuration");
			jLabelJettyHeader.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelJettyHeader;
	}
	
	private JScrollPane getJScrollPaneConfigTable() {
		if (jScrollPaneConfigTable == null) {
			jScrollPaneConfigTable = new JScrollPane();
			jScrollPaneConfigTable.setViewportView(this.getJTableJettyConfiguration());
		}
		return jScrollPaneConfigTable;
	}
	private JTable getJTableJettyConfiguration() {
		if (jTableJettyConfiguration == null) {
			jTableJettyConfiguration = new JTable(this.getTableModel());
			jTableJettyConfiguration.setFillsViewportHeight(true);
			jTableJettyConfiguration.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTableJettyConfiguration.getTableHeader().setReorderingAllowed(false);
			for (int i=0; i<=1; i++) {
				jTableJettyConfiguration.getColumnModel().getColumn(i).setCellRenderer(new JettyTableCellRender());
				jTableJettyConfiguration.getColumnModel().getColumn(i).setCellEditor(new JettyTableCellEditor(this.project));
			}
		}
		return jTableJettyConfiguration;
	}
	private DefaultTableModel getTableModel() {
		if (tableModel==null) {
			Vector<String> header = new Vector<>();
			header.add("Property");
			header.add("Value");
			tableModel = new DefaultTableModel(header, 0) {
				private static final long serialVersionUID = 149099724841552026L;
				@Override
				public boolean isCellEditable(int row, int column) {
					if (column==1) {
						return true;
					}
					return false;
				}
			};
		}
		return tableModel;
	}
	private void addTableRow(JettyParameterValue<?> jettyParameterValue) {
		
		if (jettyParameterValue==null) return;
		
		Vector<Object> row = new Vector<>();
		row.add(jettyParameterValue.getParameterKey());
		row.add(jettyParameterValue);
		this.getTableModel().addRow(row);
	}
	
	private JPanel getJPanelControl() {
		if (jPanelControl == null) {
			jPanelControl = new JPanel();
			jPanelControl.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			GridBagLayout gbl_jPanelControl = new GridBagLayout();
			gbl_jPanelControl.columnWidths = new int[]{185, 0};
			gbl_jPanelControl.rowHeights = new int[]{25, 2, 25, 0};
			gbl_jPanelControl.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_jPanelControl.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
			jPanelControl.setLayout(gbl_jPanelControl);
			GridBagConstraints gbc_jButtonStartJetty = new GridBagConstraints();
			gbc_jButtonStartJetty.insets = new Insets(10, 0, 10, 0);
			gbc_jButtonStartJetty.gridx = 0;
			gbc_jButtonStartJetty.gridy = 0;
			jPanelControl.add(getJButtonStartJetty(), gbc_jButtonStartJetty);
			GridBagConstraints gbc_jSeparatorControl = new GridBagConstraints();
			gbc_jSeparatorControl.insets = new Insets(0, 5, 0, 5);
			gbc_jSeparatorControl.fill = GridBagConstraints.HORIZONTAL;
			gbc_jSeparatorControl.gridx = 0;
			gbc_jSeparatorControl.gridy = 1;
			jPanelControl.add(getJSeparatorControl(), gbc_jSeparatorControl);
			GridBagConstraints gbc_jCheckBoxStartWithJade = new GridBagConstraints();
			gbc_jCheckBoxStartWithJade.insets = new Insets(5, 5, 10, 5);
			gbc_jCheckBoxStartWithJade.anchor = GridBagConstraints.WEST;
			gbc_jCheckBoxStartWithJade.gridx = 0;
			gbc_jCheckBoxStartWithJade.gridy = 2;
			jPanelControl.add(getJCheckBoxStartWithJade(), gbc_jCheckBoxStartWithJade);
		}
		return jPanelControl;
	}
	private JButton getJButtonStartJetty() {
		if (jButtonStartJetty == null) {
			jButtonStartJetty = new JButton("Start Jetty");
			jButtonStartJetty.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonStartJetty.setForeground(new Color(0, 153, 0));
			jButtonStartJetty.setPreferredSize(new Dimension(140, 28));
			jButtonStartJetty.addActionListener(this);
		}
		return jButtonStartJetty;
	}
	/**
	 * Sets the caption and the fore color of the jetty button.
	 *
	 * @param buttonText the button text
	 * @param foregroundColor the foreground color
	 */
	private void setJettyButton(final String buttonText, final JettyLifeCycleState lifeCycle) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JettyControlPanel.this.getJButtonStartJetty().setText(buttonText);
				JettyControlPanel.this.getJButtonStartJetty().setForeground(lifeCycle.getColor());
			}
		});
	}
	
	private JSeparator getJSeparatorControl() {
		if (jSeparatorControl==null) {
			jSeparatorControl = new JSeparator();
		}
		return jSeparatorControl;
	}
	private JCheckBox getJCheckBoxStartWithJade() {
		if (jCheckBoxStartWithJade == null) {
			jCheckBoxStartWithJade = new JCheckBox("Start Jetty with JADE Platform");
			jCheckBoxStartWithJade.setFont(new Font("Dialog", Font.PLAIN, 12));
			jCheckBoxStartWithJade.addActionListener(this);
		}
		return jCheckBoxStartWithJade;
	}
	
	/**
	 * Sets the current project unsaved.
	 */
	private void setProjectUnsaved() {
		if (this.project!=null) {
			this.project.setUnsaved(true);
		}
	}
	
	/**
	 * Returns the current {@link JettyManager} instance.
	 * @return the jetty runtime
	 */
	private JettyManager getJettyManager() {
		JettyManager jManager = AwbWebServerPlugin.getJettyManager();
		jManager.addLifeCycleListener(this);
		return jManager;
	}
	
	/**
	 * Loads the settings from the eclipse preferences.
	 */
	private void loadSettingsFromPreferences() {
		
		JettyManager jManager = this.getJettyManager();
		if (jManager!=null) {
			// --- Load the eclipse preferences ---------------------
			IEclipsePreferences prefs = jManager.getJettyConfiguration().getEclipsePreferences();
			
			// --- Get start with JADE parameter --------------------
			boolean isStartWithJade = prefs.getBoolean(JettyManager.JETTY_CONFIG_START_WITH_JADE, false);
			this.getJCheckBoxStartWithJade().setSelected(isStartWithJade);

			// --- Load the jetty configuration to the table --------
			JettyConfiguration jConfig = jManager.getJettyConfiguration();
			for (int i = 0; i < jConfig.getJettyConfigurationKeys().size(); i++) {
				String jettyKey = jConfig.getJettyConfigurationKeys().get(i);
				this.addTableRow(jConfig.get(jettyKey));
			}	
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {

		JettyManager jManager = this.getJettyManager();
		if (jManager!=null) {
			
			if (ae.getSource()==this.getJButtonStartJetty()) {
				// --- Start or Stop Jetty ---------------- 
				if (jManager.getServer()==null || jManager.getServer().isStopped()==true) {
					// --- Start Jetty --------------------
					jManager.startServer();
				} else if (jManager.getServer().isStarting()==true || jManager.getServer().isStopping()==true) {
					// --- Nothing to do here -------------
				} else if (jManager.getServer().isRunning()==true) {
					// --- Stop Jetty ---------------------
					jManager.stopServer();
				}
				
			} else if (ae.getSource()==this.getJCheckBoxStartWithJade()) {
				// --- Set Configuration ------------------
				jManager.getJettyConfiguration().getEclipsePreferences().putBoolean(JettyManager.JETTY_CONFIG_START_WITH_JADE, this.getJCheckBoxStartWithJade().isSelected());
				this.setProjectUnsaved();
			}
		}
	}
	

	// ----------------------------------------------------------------------------------
	// --- From here, the life cycle listener methods for the server are located -------- 
	// ----------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.jetty.util.component.LifeCycle.Listener#lifeCycleStarting(org.eclipse.jetty.util.component.LifeCycle)
	 */
	@Override
	public void lifeCycleStarting(LifeCycle event) {
		this.setJettyButton("Starting Jetty ...", JettyLifeCycleState.Starting);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jetty.util.component.LifeCycle.Listener#lifeCycleStarted(org.eclipse.jetty.util.component.LifeCycle)
	 */
	@Override
	public void lifeCycleStarted(LifeCycle event) {
		this.setJettyButton("Stop Jetty", JettyLifeCycleState.Started);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jetty.util.component.LifeCycle.Listener#lifeCycleStopping(org.eclipse.jetty.util.component.LifeCycle)
	 */
	@Override
	public void lifeCycleStopping(LifeCycle event) {
		this.setJettyButton("Stopping Jetty ...", JettyLifeCycleState.Stopping);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jetty.util.component.LifeCycle.Listener#lifeCycleStopped(org.eclipse.jetty.util.component.LifeCycle)
	 */
	@Override
	public void lifeCycleStopped(LifeCycle event) {
		this.setJettyButton("Start Jetty", JettyLifeCycleState.Stopped);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jetty.util.component.LifeCycle.Listener#lifeCycleFailure(org.eclipse.jetty.util.component.LifeCycle, java.lang.Throwable)
	 */
	@Override
	public void lifeCycleFailure(LifeCycle event, Throwable cause) {
		
	}
	
}
