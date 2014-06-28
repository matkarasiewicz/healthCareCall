package healthCare;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class PatientGui extends JFrame {
	private PatientAgent myAgent;
	
	private JTextField helpMessageField;
	private JComboBox helpKindComboBox;
	
	public PatientGui(PatientAgent a) {
		super(a.getLocalName());
		
		myAgent = a;
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 2));
		
		String[] list = {
				"DOCTOR",
				"NURSE",			
		};
		
		p.add(new JLabel("Typ pomocy:"));
		helpKindComboBox = new JComboBox(list);
		p.add(helpKindComboBox);
		
		p.add(new JLabel("Wiadomosc:"));
		helpMessageField = new JTextField(15);
		p.add(helpMessageField);
		
		getContentPane().add(p, BorderLayout.CENTER);
		
		JButton addButton = new JButton("Wyslij");
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String helpMessage = helpMessageField.getText().trim();
					String helpKind = helpKindComboBox.getSelectedItem().toString();
					
					
					myAgent.sendHelpRequest(PersonnelAgent.Type.valueOf(helpKind), helpMessage);
					helpMessageField.setText("");
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(PatientGui.this, "Nieprawidlowe wartosci. " + e.getMessage(), "B��d", JOptionPane.ERROR_MESSAGE); 
				}
			}
		} );
		p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);
		
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );
		
		setResizable(false);
		
	}
	
	public void display() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		setVisible(true);
	}	
}
