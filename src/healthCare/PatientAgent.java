package healthCare;

import java.awt.Dimension;
import java.awt.Toolkit;

import jade.core.Agent;

public class PatientAgent extends Agent {
	private PatientGui myGui;
	// Typ pomocy
	private int helpKind;
	// Wiadomo�� pomocy
	private String helpMessage;
	// Koordynaty w kt�rych znajduje si� pacjent
	private int x, y;
	
	protected void setup() {
		System.out.println("Witam! Pacjent " + getAID().getLocalName() + " zosta� zarejestrowany.");
		myGui = new PatientGui(this);
		myGui.display();
		
		
	}
	
    protected void takeDown() {
		myGui.dispose();
		System.out.println("Agent pacjenta " + getAID().getName() + " zosta� wypisany ze szpitala.");
	}
    
    public void sendHelpRequest(String helpKind, String helpMessage) {
    	
    }
	

	
}
