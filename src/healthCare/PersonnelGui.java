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
import javax.swing.JFrame;

public class PersonnelGui extends JFrame  {

	private PersonnelAgent myAgent;
	
	private JLabel currentStatusLabel;
	private JLabel currentRoomLabel;
	private JComboBox currentRoomComboBox;
	
	public PersonnelGui(PersonnelAgent a) {
		super(a.getLocalName());
		
		myAgent = a;
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 2));
		
		String[] list = HospitalMap.getRooms();
		
		p.add(new JLabel("Pokoj"));
		currentRoomComboBox = new JComboBox(list);
		p.add(currentRoomComboBox);
		
		
		getContentPane().add(p, BorderLayout.CENTER);
		
		JButton addButton = new JButton("Melduj");
		addButton.addActionListener( new ActionListener() {
                    
			public void actionPerformed(ActionEvent ev) {
				try {
                                    if(myAgent.getStatus() == PersonnelAgent.State.FREE) {
					String room = currentRoomComboBox.getSelectedItem().toString();
					myAgent.setRoomName(room);
                                    } else
                                    {
                                        JOptionPane.showMessageDialog(PersonnelGui.this, "Lekarz jest zajety pomocą pacjentowi.");
                                    }
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(PersonnelGui.this, "Nieprawid�owe warto�ci. " + e.getMessage(), "B��d", JOptionPane.ERROR_MESSAGE); 
				}
                                
                            updateStatus();
			}
		} );
		p = new JPanel();
		p.add(addButton);
                
		currentStatusLabel = new JLabel();
		p.add(currentStatusLabel);
		
                currentRoomLabel = new JLabel();
		p.add(currentRoomLabel);
		
		getContentPane().add(p, BorderLayout.SOUTH);
		
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );
		
		
		updateStatus();
	}
        
        public boolean displayHealthRequest(String patient) {
		int n = JOptionPane.showConfirmDialog(
                    null,
                    "Czy mozesz pomoc pacjentowi "+patient +"?",
                    "An Inane Question",
                    JOptionPane.YES_NO_OPTION);
                
		return n == 0? true:false;
        }
	
	public void updateStatus() {
		
		if(myAgent.getStatus() == PersonnelAgent.State.FREE) {
			currentStatusLabel.setText("Wolny");
		}
		else {
			currentStatusLabel.setText("Zajety");
		}
                currentRoomLabel.setText(myAgent.getRoomName());
			
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
