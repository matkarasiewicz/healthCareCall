/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package healthCare.PersonnelAgentBehaviours;

import healthCare.PersonnelAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author Mateusz
 */
public class OfferHelp extends CyclicBehaviour {

    @Override
    public void action() {

        PersonnelAgent personnelAgent = (PersonnelAgent) myAgent;
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        ACLMessage msg = personnelAgent.receive(mt);
        if (msg != null && personnelAgent.getStatus() == PersonnelAgent.State.FREE) {
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent(personnelAgent.getRoomName());
            personnelAgent.send(reply);
        } else {
            block();
        }
    }
}