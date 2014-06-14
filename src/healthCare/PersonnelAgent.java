package healthCare;

import java.awt.Point;

import jade.core.Agent;

public class PersonnelAgent extends Agent {
	
	private PersonnelGui myGui;
	
	private State status;
	
	public enum State {
	    FREE, BUSY 
	}
	
	// Typ pracownika
	private String personelKind;
	// Koordynaty w których znajduje siê pracownik
	private String roomName;
	
  protected void setup() {
	  
	  initPersonnelAgent();
	  // Tutaj trzeba typ pracownika wyci¹gn¹æ, z argumentu mo¿e? Albo potem przez formatkê
	  System.out.println("Witam! Pracownik " + getAID().getLocalName() + " przyszed³ do pracy.");
	    myGui = new PersonnelGui(this);
	    myGui.display();
	    
  }
  
  private void initPersonnelAgent()
  {
	  this.status = State.FREE;
	  

	  Object[] args = getArguments();

	  if (args != null && args.length > 0){
		  roomName = args[0].toString();
	  }
	 
  }
  
  private Point getCords() {
	  return HospitalMap.getCordsByRoom(this.roomName);
  }
  
  protected void takeDown() {
		myGui.dispose();
		System.out.println("Pracownik " + getAID().getName() + " opuœci³ pracê.");
	}
  
  public State getStatus() {
	  return this.status;
  }
	
}
