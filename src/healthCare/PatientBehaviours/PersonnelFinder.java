/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package healthCare.PatientBehaviours;

import healthCare.PatientAgent;
import healthCare.PersonnelAgent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author Mateusz
 */
public class PersonnelFinder extends TickerBehaviour {

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
        super(myAgent, 2000);
        this.patientAgent = myAgent;
        this.personnelType = personnelType;
    }

    @Override
    protected void onTick() {
         if (patientAgent.isWaitingForHelp()) {
            System.out.println(patientAgent.getLocalName() +" potrzebuje personelu " + personnelType.toString());
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("help-service");
            template.addServices(sd);
            template.addOntologies(personnelType.toString());
            try {
                DFAgentDescription[] result = DFService.search(patientAgent, template);
                if(result.length > 0) {
                System.out.println("Znaleziono personel:");
                personnelAgents = new AID[result.length];
                for (int i = 0; i < result.length; ++i) {
                    personnelAgents[i] = result[i].getName();
                    System.out.println(personnelAgents[i].getLocalName());
                }
                patientAgent.setPersonnelAgents(personnelAgents);
                } else {
                    System.out.println("Nie znaleziono personelu.");
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        } else {
            block();
        }
    }
}
