package healthCare;

import jade.core.AID;
import jade.core.Agent;

import healthCare.PatientBehaviours.PersonnelFinder;
import healthCare.PatientBehaviours.HelpCaller;
import healthCare.PatientBehaviours.HelpOfferReceiver;
import healthCare.PatientBehaviours.HelpAccepter;
import healthCare.PatientBehaviours.HelpAnswerReceiver;

public class PatientAgent extends Agent {

    private PatientGui myGui;
    private PersonnelAgent.Type personnelType;
    private String helpMessage;
    private String roomName;
    private AID[] personnelAgents;
    private boolean isWaitingForHelp;
    private PersonnelFinder personnelFinder;
    private HelpCaller helpCaller;
    private HelpOfferReceiver helpOfferReceiver;
    private HelpAccepter helpAccepter;
    private HelpAnswerReceiver helpAnswerReceiver;

    //private HelpAnswerReceiver helpAnswerReceiver;
    public AID[] getPersonnelAgents() {
        return personnelAgents;
    }

    public void setPersonnelAgents(AID[] personnelAgents) {
        this.personnelAgents = personnelAgents;
    }

    public boolean isWaitingForHelp() {
        return isWaitingForHelp;
    }

    public String getRoomName() {
        return roomName;
    }

    public double getDistanceToPersonel(String personelLocation) {
        return HospitalMap.getDistanceBetweenRooms(roomName, personelLocation);
    }

    public void sendHelpRequest(PersonnelAgent.Type personnelType, String helpMessage) {
        this.helpMessage = helpMessage;
        this.personnelType = personnelType;
        startCallHelp();
    }

    public void resendHelpRequest() {
        startCallHelp();
    }

    public void acceptHelp(AID nerestPersonnelAgent) {

        removeBehaviour(personnelFinder);
        removeBehaviour(helpCaller);
        removeBehaviour(helpOfferReceiver);

        helpAccepter = new HelpAccepter(this, nerestPersonnelAgent);
        addBehaviour(helpAccepter);
        helpAnswerReceiver = new HelpAnswerReceiver(this);
        addBehaviour(helpAnswerReceiver);
    }

    public void helpExecuting() {
        this.isWaitingForHelp = false;

        removeBehaviour(helpAccepter);
        removeBehaviour(helpAnswerReceiver);
    }

    protected void setup() {
        System.out.println("Witam! Pacjent " + getAID().getLocalName() + " zostal zarejestrowany.");

        initPatientAgent();
        myGui = new PatientGui(this);
        myGui.display();
    }

    protected void takeDown() {
        myGui.dispose();
        System.out.println("Agent pacjenta " + getAID().getName() + " zostal wypisany ze szpitala.");
    }

    private void initPatientAgent() {
        isWaitingForHelp = false;
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            roomName = args[0].toString();
        }

    }

    private void startCallHelp() {
        this.isWaitingForHelp = true;

        personnelFinder = new PersonnelFinder(this, personnelType);
        addBehaviour(personnelFinder);

        helpCaller = new HelpCaller(this, helpMessage);
        addBehaviour(helpCaller);

        helpOfferReceiver = new HelpOfferReceiver(this);
        addBehaviour(helpOfferReceiver);
    }
}
