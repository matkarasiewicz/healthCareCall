package healthCare;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import healthCare.PersonnelAgentBehaviours.AcceptHelp;
import healthCare.PersonnelAgentBehaviours.OfferHelp;

public class PersonnelAgent extends Agent {

    public enum State {

        FREE, BUSY
    }

    public enum Type {

        DOCTOR, NURSE
    }
    private PersonnelGui myGui;
    private Type type;
    private State status;
    private String roomName;

    public State getStatus() {
        return status;
    }

    public void setStatus(State status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    /**
     * @return the roomName
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * @param roomName the roomName to set
     */
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean askToAcceptHelp(String patient) {
        return myGui.displayHealthRequest(patient);
    }

    public void helpPatient() {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < 5000) {
            this.status = State.BUSY;

            myGui.updateStatus();
        }

        this.status = State.FREE;
        myGui.updateStatus();
    }

    protected void setup() {

        initPersonnelAgent();

        System.out.println("Witam! Pracownik " + getAID().getLocalName() + " przyszed� do pracy.");
        initGUI();
        registerServices();
        addBehaviour(new OfferHelp());
        addBehaviour(new AcceptHelp());
    }

    private void initPersonnelAgent() {
        this.status = State.FREE;

        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            try {
                this.type = Type.valueOf(args[0].toString());
                if (type == Type.DOCTOR) {
                    this.roomName = "Pokoj lekarzy";

                } else {
                    this.roomName = "Pokoj pielegniarek";
                }
            } catch (Exception e) {
                setDefaultType();
            }
        } else {
            setDefaultType();
        }

    }

    private void setDefaultType() {
        this.type = Type.DOCTOR;
        this.roomName = "Pokoj lekarzy";
    }

    private void initGUI() {
        myGui = new PersonnelGui(this);
        myGui.display();
    }

    private void registerServices() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("help-service");
        sd.setName("JADE-health-care-call");
        dfd.addOntologies(getType().toString());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    protected void takeDown() {
        myGui.dispose();
        System.out.println("Pracownik " + getAID().getName() + " opuscil prace.");
    }
}
