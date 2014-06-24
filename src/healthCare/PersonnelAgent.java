package healthCare;

import java.awt.Point;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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

    public String getRoomName() {
        return roomName;
    }

    protected void setup() {

        initPersonnelAgent();
        // Tutaj trzeba typ pracownika wyci�gn��, z argumentu mo�e? Albo potem przez formatk�
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
            roomName = args[0].toString();
            this.type = Type.valueOf(args[1].toString());
        }

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
        System.out.println("Pracownik " + getAID().getName() + " opu�ci� prac�.");
    }

// <editor-fold defaultstate="collapsed" desc="Behaviours">
    private class OfferHelp extends CyclicBehaviour {

        public void action() {

            PersonnelAgent personnelAgent = (PersonnelAgent) myAgent;
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = personnelAgent.receive(mt);
            if (msg != null && personnelAgent.getStatus() == PersonnelAgent.State.FREE) {
                System.out.println("Pacjent z pokoju: " + msg.getOntology() + " " + msg.getContent());
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContent(personnelAgent.getRoomName());
                personnelAgent.send(reply);
            } else {
                block();
            }
        }
    }

    private class AcceptHelp extends CyclicBehaviour {

        PersonnelAgent personnelAgent;

        public void action() {
            personnelAgent = (PersonnelAgent) myAgent;
            //tylko zlecenia kupna, ktore stanowia akceptacje oferty
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = personnelAgent.receive(mt);
            if (msg != null) {
                String room = msg.getContent();
                ACLMessage reply = msg.createReply();
                if (personnelAgent.getStatus() == PersonnelAgent.State.FREE) {
                    personnelAgent.setStatus(PersonnelAgent.State.BUSY);
                    reply.setPerformative(ACLMessage.INFORM);
                    System.out.println(personnelAgent.getLocalName() + " pomaga " + msg.getSender().getLocalName() + " w pokoju " + room);
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("Lekarz jest zajety pomoca innemu pacjentowi");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }
    // </editor-fold>
}
