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
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author Mateusz
 */
public class HelpOfferReceiver extends CyclicBehaviour {

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

    public void action() {
        patientAgent = (PatientAgent) myAgent;
        personnelAgents = patientAgent.getPersonnelAgents();
        mt = MessageTemplate.MatchConversationId("help-service");
        ACLMessage reply = myAgent.receive(mt);
        if (reply != null) {
            if (reply.getPerformative() == ACLMessage.PROPOSE) {

                double ditanceToRespondetPersonnel = patientAgent.getDistanceToPersonel(reply.getContent());
                if (nerestPersonnelAgent == null || ditanceToRespondetPersonnel < nerestPersonnelDistance) {
                    nerestPersonnelDistance = ditanceToRespondetPersonnel;
                    nerestPersonnelAgent = reply.getSender();
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
