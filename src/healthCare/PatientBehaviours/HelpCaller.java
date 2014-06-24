/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package healthCare.PatientBehaviours;

import healthCare.PatientAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Mateusz
 */
public class HelpCaller extends CyclicBehaviour {

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
            message.setOntology(patientAgent.getRoomName());
            message.setConversationId("help-service");
            message.setReplyWith("cfp" + System.currentTimeMillis()); //unikalna wartosc
            patientAgent.send(message);
        }
    }
}
