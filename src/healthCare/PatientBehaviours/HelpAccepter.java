/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package healthCare.PatientBehaviours;

import healthCare.PatientAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Mateusz
 */
public class HelpAccepter extends OneShotBehaviour {

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