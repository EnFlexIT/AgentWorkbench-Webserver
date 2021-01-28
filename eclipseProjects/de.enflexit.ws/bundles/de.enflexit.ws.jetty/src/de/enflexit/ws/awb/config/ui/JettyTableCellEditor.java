package de.enflexit.ws.awb.config.ui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.BadLocationException;

import agentgui.core.project.Project;
import de.enflexit.common.swing.KeyAdapter4Numbers;
import de.enflexit.common.swing.TableCellColorHelper;
import de.enflexit.ws.awb.config.JettyParameterValue;

/**
 * The Class JettyTableCellEditor.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 4711118214607190127L;

	private Project project;
	private JettyParameterValue<?> parameterValue;
	
	
	/**
	 * Instantiates a new jetty table cell editor.
	 * @param project the current {@link Project}s instance
	 */
	public JettyTableCellEditor(Project project) {
		this.project = project;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

		Component displayComp = null;
		if (column==1) {
			// --- Get the right edit component  
			this.parameterValue = (JettyParameterValue<?>) value;
			if (parameterValue.getParameterType()==Boolean.class) {
				JettyParameterValue<Boolean> jpvBoolean = this.getJettyParameterValueBoolean();
				JCheckBox checkBox = this.getJCheckBox();
				checkBox.setSelected(jpvBoolean.getValue());
				displayComp = checkBox;
				
			} else if (parameterValue.getParameterType()==Integer.class) {
				JettyParameterValue<Integer> jpvInteger = this.getJettyParameterValueInteger();
				JTextField textField = this.getJTextFieldNumber();
				textField.setText(jpvInteger.getValue().toString());
				displayComp = textField;
				
			} else if (parameterValue.getParameterType()==String.class) {
				JettyParameterValue<String> jpvString = this.getJettyParameterValueString();
				JTextField textField = this.getJTextField();
				textField.setText(jpvString.getValue());
				displayComp = textField;
				
			}
		}
		TableCellColorHelper.setTableCellRendererColors((JComponent) displayComp, row, isSelected);
		return displayComp;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
		return this.parameterValue;
	}
	@SuppressWarnings("unchecked")
	private JettyParameterValue<Boolean> getJettyParameterValueBoolean() {
		return (JettyParameterValue<Boolean>) this.parameterValue;
	}
	@SuppressWarnings("unchecked")
	private JettyParameterValue<Integer> getJettyParameterValueInteger() {
		return (JettyParameterValue<Integer>) this.parameterValue;
	}
	@SuppressWarnings("unchecked")
	private JettyParameterValue<String> getJettyParameterValueString() {
		return (JettyParameterValue<String>) this.parameterValue;
	}
	
	
	private JCheckBox getJCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				JCheckBox checkBox = (JCheckBox) ae.getSource();
				getJettyParameterValueBoolean().setValue(checkBox.isSelected());
				JettyTableCellEditor.this.setProjectUnsaved();
			}
		});
		return checkBox;
	}
	
	private JTextField getJTextField() {
		JTextField tf = new JTextField();
		tf.setBorder(BorderFactory.createEmptyBorder());
		tf.setMargin(new Insets(0, 0, 0, 0));
		tf.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent de) {
				this.update(de);
			}
			@Override
			public void insertUpdate(DocumentEvent de) {
				this.update(de);
			}
			@Override
			public void changedUpdate(DocumentEvent de) {
				this.update(de);
			}
			private void update(DocumentEvent de) {
				String text = null;
				try {
					text = de.getDocument().getText(0, de.getDocument().getLength());
				} catch (BadLocationException blEx) {
					blEx.printStackTrace();
				}
				getJettyParameterValueString().setValue(text);
				JettyTableCellEditor.this.setProjectUnsaved();
			}
		});
		return tf;
	}
	
	private JTextField getJTextFieldNumber() {
		JTextField tf = new JTextField();
		tf.setBorder(BorderFactory.createEmptyBorder());
		tf.setMargin(new Insets(0, 0, 0, 0));
		tf.addKeyListener(new KeyAdapter4Numbers(false));
		tf.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent de) {
				this.update(de);
			}
			@Override
			public void insertUpdate(DocumentEvent de) {
				this.update(de);
			}
			@Override
			public void changedUpdate(DocumentEvent de) {
				this.update(de);
			}
			private void update(DocumentEvent de) {
				Integer intValue = 0;
				String text = null;
				try {
					text = de.getDocument().getText(0, de.getDocument().getLength());
					if (text!=null && text.length()>0) {
						intValue = Integer.parseInt(text);
					}
					
				} catch (BadLocationException blEx) {
					blEx.printStackTrace();
				}
				getJettyParameterValueInteger().setValue(intValue);
				JettyTableCellEditor.this.setProjectUnsaved();
			}
		});
		return tf;
	}
	
	/**
	 * Sets the current project unsaved.
	 */
	private void setProjectUnsaved() {
		if (this.project!=null) {
			this.project.setUnsaved(true);
		}
	}
	
}
