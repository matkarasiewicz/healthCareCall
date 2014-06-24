package healthCare;

import java.awt.Dimension;
import java.awt.Toolkit;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.awt.Point;

public class PatientAgent extends Agent {

    final private int HELP_REQUEST_INTERVAL;
    private PatientGui myGui;
    // Typ pomocy
    private PersonnelAgent.Type helpKind;
    // Wiadomo�� pomocy
    private String helpMessage;
    // Koordynaty w kt�rych znajduje si� pacjent
    private String roomName;
    //list`a znanych lekarzy
    private AID[] personnelAgents;

    public PatientAgent() {
        this.HELP_REQUEST_INTERVAL = 2000;
    }

    public String getRoomName() {
        return roomName;
    }

    public double getDistanceToPersonel(String personelLocation) {
        return HospitalMap.getDistanceBetweenRooms(roomName, personelLocation);
    }

    public void sendHelpRequest(PersonnelAgent.Type personnelType, String helpMessage) {
        this.helpMessage = helpMessage;
        PersonnelFinder personnelFinder = new PersonnelFinder(this, personnelType);
        addBehaviour(personnelFinder);

    }

    public void runHelpCall(AID[] personnelAgents) {
        HelpCaller helpCaller = new HelpCaller(this, personnelAgents, helpMessage);
        addBehaviour(helpCaller);
        HelpReceiver helpReciver = new HelpReceiver();
        addBehaviour(helpReciver);
//                HelpAccepter helpAccepter = new HelpAccepter((PatientAgent)myAgent, helpReciver.getNerestPersonnelAgent());
//                addBehaviour(helpAccepter);
    }

    protected void setup() {
        System.out.println("Witam! Pacjent " + getAID().getLocalName() + " zosta� zarejestrowany.");

        initPatientAgent();
        myGui = new PatientGui(this);
        myGui.display();
    }

    protected void takeDown() {
        myGui.dispose();
        System.out.println("Agent pacjenta " + getAID().getName() + " zosta� wypisany ze szpitala.");
    }

    private void initPatientAgent() {

        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            roomName = args[0].toString();
        }

    }

// <editor-fold defaultstate="collapsed" desc="Behaviours">
    private class PersonnelFinder extends OneShotBehaviour {

        private PersonnelAgent.Type personnelType;
        private AID[] personnelAgents;

        public AID[] getPersonnelAgents() {
            return personnelAgents;
        }
        private PatientAgent myAgent;

        public PersonnelAgent.Type getPersonnelType() {
            return personnelType;
        }

        public PersonnelFinder(PatientAgent myAgent, PersonnelAgent.Type personnelType) {
            super(myAgent);
            this.myAgent = myAgent;
            this.personnelType = personnelType;
        }

        public void action() {

            System.out.println("Potrzebuje personelu " + personnelType.toString());
            //aktualizuj liste znanych sprzedawcow
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("help-service");
            template.addServices(sd);
            template.addOntologies(personnelType.toString());
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                System.out.println("Znaleziono sprzedajacych:");
                personnelAgents = new AID[result.length];
                for (int i = 0; i < result.length; ++i) {
                    personnelAgents[i] = result[i].getName();
                    System.out.println(personnelAgents[i].getLocalName());
                }
                myAgent.runHelpCall(personnelAgents);

            } catch (FIPAException fe) {
                fe.printStackTrace();
            }

        }
    }

    private class HelpCaller extends OneShotBehaviour {

        private MessageTemplate mt;
        private AID[] personnelAgents;
        private String helpMessage;

        public HelpCaller(Agent myAgent, AID[] personnelAgents, String helpMessage) {
            super(myAgent);
            this.myAgent = myAgent;
            this.personnelAgents = personnelAgents;
            this.helpMessage = helpMessage;
        }

        public void action() {
            ACLMessage message = new ACLMessage(ACLMessage.CFP);
            for (int i = 0; i < personnelAgents.length; ++i) {
                message.addReceiver(personnelAgents[i]);
            }
            message.setContent(helpMessage);
            message.setOntology(roomName);
            message.setConversationId("help-service");
            message.setReplyWith("cfp" + System.currentTimeMillis()); //unikalna wartosc
            myAgent.send(message);
            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("help-service"),
                    MessageTemplate.MatchInReplyTo(message.getReplyWith()));
        }
    }

    private class HelpReceiver extends CyclicBehaviour {

        private MessageTemplate mt;
        private int repliesCnt;
        private AID nerestPersonnelAgent;

        public AID getNerestPersonnelAgent() {
            return nerestPersonnelAgent;
        }
        private double nerestPersonnelDistance;
        PatientAgent pateintAgent;

        public void action() {
            //odbior ofert od sprzedajacych
            pateintAgent = (PatientAgent)myAgent;
            ACLMessage reply = myAgent.receive(mt);
            if (reply != null) {
                if (reply.getPerformative() == ACLMessage.PROPOSE) {

                    double ditanceToRespondetPersonnel = pateintAgent.getDistanceToPersonel(reply.getContent());
                    if (nerestPersonnelAgent == null || ditanceToRespondetPersonnel < nerestPersonnelDistance) {
                        //jak na razie to najlepsza oferta
                        nerestPersonnelDistance = ditanceToRespondetPersonnel;
                        nerestPersonnelAgent = reply.getSender();
                        //zliczyc ilosc odebranych odpowiedzi od personelu i wywolac help accepter
                    }
                }
            } else {
                block();
            }
        }
    }

    private class HelpAccepter extends OneShotBehaviour {

        private MessageTemplate mt;
        private int repliesCnt;
        private AID nerestPersonnelAgent;

        public AID getNerestPersonnelAgent() {
            return nerestPersonnelAgent;
        }
        private double nerestPersonnelDistance;
        PatientAgent myAgent;

        public HelpAccepter(PatientAgent myAgent, AID nerestPersonnelAgent) {
            super(myAgent);
            this.myAgent = myAgent;
            this.nerestPersonnelAgent = nerestPersonnelAgent;
        }

        public void action() {
            ACLMessage helpRequest = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            helpRequest.addReceiver(nerestPersonnelAgent);
            helpRequest.setContent(myAgent.getRoomName());
            helpRequest.setConversationId("help-service");
            helpRequest.setReplyWith("help" + System.currentTimeMillis());
            myAgent.send(helpRequest);
            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("help-service"),
                    MessageTemplate.MatchInReplyTo(helpRequest.getReplyWith()));
        }
    }
    // </editor-fold>
}
