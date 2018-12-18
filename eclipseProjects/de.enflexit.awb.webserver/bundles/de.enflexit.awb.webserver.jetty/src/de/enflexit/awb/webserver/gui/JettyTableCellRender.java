package de.enflexit.awb.webserver.gui;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import de.enflexit.awb.webserver.JettyParameterValue;
import de.enflexit.common.swing.KeyAdapter4Numbers;
import de.enflexit.common.swing.TableCellColorHelper;

/**
 * The Class JettyTableCellRender.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyTableCellRender extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -6442949219445565071L;

	
	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component displayComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (column==1) {
			// --- Get the right edit component  
			JettyParameterValue<?> parameterValue = (JettyParameterValue<?>) value;
			if (parameterValue.getParameterType()==Boolean.class) {
				@SuppressWarnings("unchecked")
				JettyParameterValue<Boolean> jpvBoolean = (JettyParameterValue<Boolean>) parameterValue;
				JCheckBox checkBox = this.getJCheckBox();
				checkBox.setSelected(jpvBoolean.getValue());
				displayComp = checkBox;
				
			} else if (parameterValue.getParameterType()==Integer.class) {
				@SuppressWarnings("unchecked")
				JettyParameterValue<Integer> jpvInteger = (JettyParameterValue<Integer>) parameterValue;
				JTextField textField = this.getJTextFieldNumber();
				textField.setText(jpvInteger.getValue().toString());
				displayComp = textField;
				
			} else if (parameterValue.getParameterType()==String.class) {
				@SuppressWarnings("unchecked")
				JettyParameterValue<String> jpvString = (JettyParameterValue<String>) parameterValue;
				JTextField textField = this.getJTextField();
				textField.setText(jpvString.getValue());
				displayComp = textField;
				
			}
		}
		TableCellColorHelper.setTableCellRendererColors((JComponent) displayComp, row, isSelected);
		return displayComp;
	}
	
	private JCheckBox getJCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		return checkBox;
	}
	
	private JTextField getJTextField() {
		JTextField tf = new JTextField();
		tf.setBorder(BorderFactory.createEmptyBorder());
		tf.setMargin(new Insets(0, 0, 0, 0));
		return tf;
	}
	
	private JTextField getJTextFieldNumber() {
		JTextField tf = new JTextField();
		tf.setBorder(BorderFactory.createEmptyBorder());
		tf.setMargin(new Insets(0, 0, 0, 0));
		tf.addKeyListener(new KeyAdapter4Numbers(false));
		return tf;
	}
	
}
