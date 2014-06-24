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
public class AcceptHelp extends CyclicBehaviour {

    PersonnelAgent personnelAgent;

    @Override
    public void action() {
        personnelAgent = (PersonnelAgent) myAgent;
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
        ACLMessage msg = personnelAgent.receive(mt);
        if (msg != null) {
            String room = msg.getContent();
            ACLMessage reply = msg.createReply();
            if (personnelAgent.getStatus() == PersonnelAgent.State.FREE) {
                personnelAgent.helpPatient();
                reply.setPerformative(ACLMessage.INFORM);
                System.out.println(personnelAgent.getLocalName() + " pomaga " + msg.getSender().getLocalName() + " w pokoju " + room);
            } else {
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("Lekarz jest zajety pomoca innemu pacjentowi");
            }
            personnelAgent.send(reply);
        } else {
            block();
        }
    }
}