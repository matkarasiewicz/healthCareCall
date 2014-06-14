package healthCare;

import java.awt.Dimension;
import java.awt.Toolkit;

import jade.core.Agent;

public class PatientAgent extends Agent {
	private PatientGui myGui;
	// Typ pomocy
	private int helpKind;
	// Wiadomoœæ pomocy
	private String helpMessage;
	// Koordynaty w których znajduje siê pacjent
	private int x, y;
	
	protected void setup() {
		System.out.println("Witam! Pacjent " + getAID().getLocalName() + " zosta³ zarejestrowany.");
		myGui = new PatientGui(this);
		myGui.display();
		
		
	}
	
    protected void takeDown() {
		myGui.dispose();
		System.out.println("Agent pacjenta " + getAID().getName() + " zosta³ wypisany ze szpitala.");
	}
    
    public void sendHelpRequest(String helpKind, String helpMessage) {
    	
    }
	

	
}
