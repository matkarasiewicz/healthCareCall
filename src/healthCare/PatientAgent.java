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
        this.isWaitingForHelp = true;

        personnelFinder = new PersonnelFinder(this, personnelType);
        addBehaviour(personnelFinder);

        helpCaller = new HelpCaller(this, helpMessage);
        addBehaviour(helpCaller);

        helpOfferReceiver = new HelpOfferReceiver(this);
        addBehaviour(helpOfferReceiver);
    }

    public void resendHelpRequest() {
        this.isWaitingForHelp = true;

        personnelFinder = new PersonnelFinder(this, personnelType);
        addBehaviour(personnelFinder);

        helpCaller = new HelpCaller(this, helpMessage);
        addBehaviour(helpCaller);

        helpOfferReceiver = new HelpOfferReceiver(this);
        addBehaviour(helpOfferReceiver);
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
        isWaitingForHelp = false;
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            roomName = args[0].toString();
        }

    }

// <editor-fold defaultstate="collapsed" desc="Behaviours">
    private class PersonnelFinder extends CyclicBehaviour {

        private PersonnelAgent.Type personnelType;
        private AID[] personnelAgents;

        public AID[] getPersonnelAgents() {
            return personnelAgents;
        }
        private PatientAgent patientAgent;

        public PersonnelAgent.Type getPersonnelType() {
            return personnelType;
        }

        public PersonnelFinder(PatientAgent myAgent, PersonnelAgent.Type personnelType) {
            super(myAgent);
            this.patientAgent = myAgent;
            this.personnelType = personnelType;
        }

        @Override
        public void action() {
            if (patientAgent.isWaitingForHelp()) {
                System.out.println("Potrzebuje personelu " + personnelType.toString());
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("help-service");
                template.addServices(sd);
                template.addOntologies(personnelType.toString());
                try {
                    DFAgentDescription[] result = DFService.search(patientAgent, template);
                    System.out.println("Znaleziono personel:");
                    personnelAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        personnelAgents[i] = result[i].getName();
                        System.out.println(personnelAgents[i].getLocalName());
                    }
                    patientAgent.setPersonnelAgents(personnelAgents);

                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        }
    }

    private class HelpCaller extends CyclicBehaviour {

        private AID[] personnelAgents;
        private String helpMessage;
        private PatientAgent patientAgent;

        public HelpCaller(Agent myAgent, String helpMessage) {
            super(myAgent);
            this.patientAgent = (PatientAgent) myAgent;
            this.helpMessage = helpMessage;
        }

        @Override
        public void action() {
            personnelAgents = patientAgent.getPersonnelAgents();

            if (patientAgent.isWaitingForHelp() && personnelAgents != null && personnelAgents.length > 0) {
                ACLMessage message = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < personnelAgents.length; ++i) {
                    message.addReceiver(personnelAgents[i]);
                }
                message.setContent(helpMessage);
                message.setOntology(roomName);
                message.setConversationId("help-service");
                message.setReplyWith("cfp" + System.currentTimeMillis()); //unikalna wartosc
                patientAgent.send(message);
            }
        }
    }

    private class HelpOfferReceiver extends CyclicBehaviour {

        private MessageTemplate mt;
        private int repliesCnt;
        private AID nerestPersonnelAgent;
        private AID[] personnelAgents;

        public AID getNerestPersonnelAgent() {
            return nerestPersonnelAgent;
        }
        private double nerestPersonnelDistance;
        PatientAgent patientAgent;

        public HelpOfferReceiver(Agent myAgent) {
            super(myAgent);
            this.patientAgent = (PatientAgent) myAgent;
            this.personnelAgents = patientAgent.getPersonnelAgents();
        }
        //TODO: zliczyc ilosc odp od personelu

        public void action() {
            patientAgent = (PatientAgent) myAgent;
            personnelAgents = patientAgent.getPersonnelAgents();
            mt = MessageTemplate.MatchConversationId("help-service");
            ACLMessage reply = myAgent.receive(mt);
            if (reply != null) {
                if (reply.getPerformative() == ACLMessage.PROPOSE) {

                    double ditanceToRespondetPersonnel = patientAgent.getDistanceToPersonel(reply.getContent());
                    if (nerestPersonnelAgent == null || ditanceToRespondetPersonnel < nerestPersonnelDistance) {
                        //jak na razie to najlepsza oferta
                        nerestPersonnelDistance = ditanceToRespondetPersonnel;
                        nerestPersonnelAgent = reply.getSender();
                        //zliczyc ilosc odebranych odpowiedzi od personelu i wywolac help accepter
                    }

                    repliesCnt++;
                    if (personnelAgents != null && repliesCnt >= personnelAgents.length) {
                        patientAgent.acceptHelp(nerestPersonnelAgent);
                    }
                }
            } else {
                block();
            }
        }
    }

    private class HelpAccepter extends OneShotBehaviour {

        private AID nerestPersonnelAgent;

        public AID getNerestPersonnelAgent() {
            return nerestPersonnelAgent;
        }
        PatientAgent myAgent;

        public HelpAccepter(PatientAgent myAgent, AID nerestPersonnelAgent) {
            super(myAgent);
            this.myAgent = myAgent;
            this.nerestPersonnelAgent = nerestPersonnelAgent;
        }

        @Override
        public void action() {
            ACLMessage helpRequest = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            helpRequest.addReceiver(nerestPersonnelAgent);
            helpRequest.setContent(myAgent.getRoomName());
            helpRequest.setConversationId("help-service");
            helpRequest.setOntology("help-acceptance");
            helpRequest.setReplyWith("help" + System.currentTimeMillis());
            myAgent.send(helpRequest);
        }
    }

    private class HelpAnswerReceiver extends CyclicBehaviour {

        private MessageTemplate mt;
        PatientAgent patientAgent;

        public HelpAnswerReceiver(PatientAgent myAgent) {
            super(myAgent);
            this.patientAgent = myAgent;
            mt = MessageTemplate.MatchOntology("help-acceptance");
        }
        
        @Override
        public void action() {
            ACLMessage reply = patientAgent.receive(mt);
            if (reply != null) {
                if (reply.getPerformative() == ACLMessage.INFORM) {
                    patientAgent.helpExecuting();
                    System.out.println(patientAgent.getLocalName() + " otrzymuje pomoc od " + reply.getSender().getLocalName());
                    
                } else {
                    patientAgent.resendHelpRequest();
                    System.out.println(reply.getSender().getLocalName() + " nie pomogl pacjentowi " + patientAgent.getLocalName() );
                }
            } else {
                block();
            }
        }
    }
    // </editor-fold>
}
