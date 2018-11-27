package de.enflexit.awb.webserver.gui;

import javax.swing.JPanel;
import javax.swing.JSeparator;

import agentgui.core.project.Project;
import de.enflexit.awb.webserver.jetty.JettyRuntime;
import de.enflexit.common.swing.KeyAdapter4Numbers;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jetty.server.Server;

/**
 * The Class JettyControlPanel.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyControlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 5025152245197566098L;

	private Project project;

	private JPanel jPanelJettyConfig;
	private JLabel jLabelJettyHeader;
	private JLabel jLabelPort;
	private JTextField jTextFieldJettyPort;

	private JPanel jPanelControl;
	private JButton jButtonStartJetty;
	private JSeparator jSeparatorControl;
	private JCheckBox jCheckBoxStartWithJade;
	
	
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
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gridBagLayout);
		
		GridBagConstraints gbc_jLabelJettyHeader = new GridBagConstraints();
		gbc_jLabelJettyHeader.anchor = GridBagConstraints.WEST;
		gbc_jLabelJettyHeader.insets = new Insets(10, 10, 0, 0);
		gbc_jLabelJettyHeader.gridx = 0;
		gbc_jLabelJettyHeader.gridy = 0;
		this.add(getJLabelJettyHeader(), gbc_jLabelJettyHeader);

		GridBagConstraints gbc_jPanelJettyConfig = new GridBagConstraints();
		gbc_jPanelJettyConfig.anchor = GridBagConstraints.NORTHWEST;
		gbc_jPanelJettyConfig.insets = new Insets(10, 10, 0, 0);
		gbc_jPanelJettyConfig.gridx = 0;
		gbc_jPanelJettyConfig.gridy = 1;
		this.add(getJPanelJettyConfig(), gbc_jPanelJettyConfig);
		
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
	private JPanel getJPanelJettyConfig() {
		if (jPanelJettyConfig == null) {
			jPanelJettyConfig = new JPanel();
			GridBagLayout gbl_jPanelJettyConfig = new GridBagLayout();
			gbl_jPanelJettyConfig.columnWidths = new int[]{0, 0, 0};
			gbl_jPanelJettyConfig.rowHeights = new int[]{0, 0, 0};
			gbl_jPanelJettyConfig.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			gbl_jPanelJettyConfig.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			jPanelJettyConfig.setLayout(gbl_jPanelJettyConfig);
			GridBagConstraints gbc_jLabelPort = new GridBagConstraints();
			gbc_jLabelPort.anchor = GridBagConstraints.WEST;
			gbc_jLabelPort.gridx = 0;
			gbc_jLabelPort.gridy = 0;
			jPanelJettyConfig.add(getJLabelPort(), gbc_jLabelPort);
			GridBagConstraints gbc_jTextFieldJettyPort = new GridBagConstraints();
			gbc_jTextFieldJettyPort.insets = new Insets(0, 10, 0, 0);
			gbc_jTextFieldJettyPort.gridx = 1;
			gbc_jTextFieldJettyPort.gridy = 0;
			jPanelJettyConfig.add(getJTextFieldJettyPort(), gbc_jTextFieldJettyPort);
		}
		return jPanelJettyConfig;
	}
	private JLabel getJLabelPort() {
		if (jLabelPort == null) {
			jLabelPort = new JLabel("Jetty-Port:");
			jLabelPort.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelPort;
	}
	private JTextField getJTextFieldJettyPort() {
		if (jTextFieldJettyPort == null) {
			jTextFieldJettyPort = new JTextField();
			jTextFieldJettyPort.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTextFieldJettyPort.setPreferredSize(new Dimension(80, 26));
			jTextFieldJettyPort.addKeyListener(new KeyAdapter4Numbers(false));
			jTextFieldJettyPort.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent de) {
					this.setPortToPreferences(de);
				}
				@Override
				public void insertUpdate(DocumentEvent de) {
					this.setPortToPreferences(de);
				}
				@Override
				public void changedUpdate(DocumentEvent de) {
					this.setPortToPreferences(de);
				}
				private void setPortToPreferences(DocumentEvent de) {
					
					Integer port = 8080; // as default
					try {
						String portString = de.getDocument().getText(0, de.getDocument().getLength());
						if (portString!=null && portString.isEmpty()==false) {
							port = Integer.parseInt(portString);
						}
						
					} catch (BadLocationException blEx) {
						blEx.printStackTrace();
					}
					JettyRuntime.getInstance().getEclipsePreferences().putInt(JettyRuntime.JETTY_CONFIG_PORT, port);
					JettyControlPanel.this.setProjectUnsaved();
				}
			});
		}
		return jTextFieldJettyPort;
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
			jPanelControl.add(getJSeparator02(), gbc_jSeparatorControl);
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
			jButtonStartJetty.addActionListener(this);
		}
		return jButtonStartJetty;
	}
	private JSeparator getJSeparator02() {
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
	 * Loads the settings from the eclipse preferences.
	 */
	private void loadSettingsFromPreferences() {
		
		IEclipsePreferences prefs = JettyRuntime.getInstance().getEclipsePreferences();
		
		int port = prefs.getInt(JettyRuntime.JETTY_CONFIG_PORT, 8080);
		this.getJTextFieldJettyPort().setText("" + port);

		boolean isStartWithJade = prefs.getBoolean(JettyRuntime.JETTY_CONFIG_START_WITH_JADE, false);
		this.getJCheckBoxStartWithJade().setSelected(isStartWithJade);
		
	}
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource()==this.getJButtonStartJetty()) {
			// --- Start or Stop Jetty -------------------- 
			JettyRuntime jettyRuntime = JettyRuntime.getInstance();
			if (jettyRuntime.isServerExecuted()==false) {
				// --- Start Jetty ------------------------
				Server server = JettyRuntime.getInstance().startServer();
				if (server!=null) {
					this.getJButtonStartJetty().setText("Stop Jetty");
					this.getJButtonStartJetty().setForeground(new Color(153, 0, 0));
				}
				
			} else {
				// --- Stop Jetty -------------------------
				if (JettyRuntime.getInstance().stopServer()==true) {
					this.getJButtonStartJetty().setText("Start Jetty");
					this.getJButtonStartJetty().setForeground(new Color(0, 153, 0));
				}
				
			}
			
		} else if (ae.getSource()==this.getJCheckBoxStartWithJade()) {
			// --- Set Configuration ----------------------
			JettyRuntime.getInstance().getEclipsePreferences().putBoolean(JettyRuntime.JETTY_CONFIG_START_WITH_JADE, this.getJCheckBoxStartWithJade().isSelected());
			this.setProjectUnsaved();
			
		}
		
	}
	
}
