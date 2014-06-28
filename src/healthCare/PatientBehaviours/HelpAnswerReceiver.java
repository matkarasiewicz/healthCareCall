/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package healthCare.PatientBehaviours;

import healthCare.PatientAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author Mateusz
 */
public class HelpAnswerReceiver extends CyclicBehaviour {

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
                System.out.println(patientAgent.getLocalName() + " otrzymal pomoc od " + reply.getSender().getLocalName());

            } else {
                patientAgent.resendHelpRequest();
                String asd = reply.getSender().getLocalName();
                if(reply.getSender().getLocalName() == "ams") {
                    System.out.println(patientAgent.getLocalName() + "zmarl");
                    patientAgent.doDelete();
                } else {
                System.out.println(reply.getSender().getLocalName() + " nie pomogl pacjentowi " + patientAgent.getLocalName());
                }
            }
        } else {
            block();
        }
    }
}