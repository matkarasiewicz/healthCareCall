package healthCare;

import java.awt.Dimension;
import java.awt.Toolkit;

import bookTrading.BookBuyerAgent.RequestPerformer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PatientAgent extends Agent {
	private PatientGui myGui;
	// Typ pomocy
	private int helpKind;
	// Wiadomoœæ pomocy
	private String helpMessage;
	// Koordynaty w których znajduje siê pacjent
	private String roomName;
	
	//lista znanych lekarzy
	  private AID[] personnelAgents;
	
	protected void setup() {
		System.out.println("Witam! Pacjent " + getAID().getLocalName() + " zosta³ zarejestrowany.");
		
		
		myGui = new PatientGui(this);
		myGui.display();
		
		//interwal czasowy dla kupujacego pomiedzy wysylaniem kolejnych cfp
				//przekazywany jako argument linii polecen
				int interval = 20000;
				Object[] args = getArguments();
				if (args != null && args.length > 0) interval = Integer.parseInt(args[0].toString());
			  addBehaviour(new TickerBehaviour(this, interval)
			  {
				  protected void onTick()
				  {
					//szukaj tylko jesli zlecony zostal tytul pozycji
					  if (helpKind != null)
					  {
						  System.out.println("Potrzebuje personelu " + helpKind);
						  //aktualizuj liste znanych sprzedawcow
						  DFAgentDescription template = new DFAgentDescription();
						  ServiceDescription sd = new ServiceDescription();
						  sd.setType("help-service");
						  template.addServices(sd);
						  try
						  {
							  DFAgentDescription[] result = DFService.search(myAgent, template);
							  System.out.println("Znaleziono sprzedajacych:");
							  personnelAgents = new AID[result.length];
							  for (int i = 0; i < result.length; ++i)
							  {
								  personnelAgents[i] = result[i].getName();
								  System.out.println(personnelAgents[i].getLocalName());
							  }
						  }
						  catch (FIPAException fe)
						  {
							  fe.printStackTrace();
						  }

						  myAgent.addBehaviour(new RequestPerformer());
					  }
				  }
			  });
		
		
	}
	
    protected void takeDown() {
		myGui.dispose();
		System.out.println("Agent pacjenta " + getAID().getName() + " zosta³ wypisany ze szpitala.");
	}
    
    public void sendHelpRequest(String helpKind, String helpMessage) {
    	
    }
    
    private class RequestPerformer extends Behaviour {
  	  private AID bestSeller;
  	  private int bestPrice;
  	  private int repliesCnt = 0;
  	  private MessageTemplate mt;
  	  private int step = 0;
  	
  	  public void action() {
  	    switch (step) {
  	    case 0:
  	      //call for proposal (cfp) do znalezionych lekarzy
  	      ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
  	      for (int i = 0; i < personnelAgents.length; ++i) {
  	        cfp.addReceiver(personnelAgents[i]);
  	      } 
  	      cfp.setContent("0");
  	      cfp.setConversationId("help-request");
  	      cfp.setReplyWith("cfp"+System.currentTimeMillis()); //unikalna wartosc
  	      myAgent.send(cfp);
  	      mt = MessageTemplate.and(MessageTemplate.MatchConversationId("help-request"),
  	                               MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
  	      step = 1;
  	      break;
  	    case 1:
  	      //odbior ofert od sprzedajacych
  	      ACLMessage reply = myAgent.receive(mt);
  	      if (reply != null) {
  	        if (reply.getPerformative() == ACLMessage.PROPOSE) {
  	          //otrzymano oferte
  	          int price = Integer.parseInt(reply.getContent());
  	          if (bestSeller == null || price < bestPrice) {
  	            //jak na razie to najlepsza oferta
  	            bestPrice = price;
  	            bestSeller = reply.getSender();
  	          }
  	        }
  	        repliesCnt++;
  	        if (repliesCnt >= personnelAgents.length) {
  	          //otrzymano wszystkie oferty -> nastepny krok
  	          step = 2; 
  	        }
  	      }
  	      else {
  	        block();
  	      }
  	      break;
  	    case 2:
  	      //zakup najlepszej oferty
  	      ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            order.addReceiver(bestSeller);
  	      order.setContent(helpKind.toString());
  	      order.setConversationId("book-trade");
  	      order.setReplyWith("order"+System.currentTimeMillis());
  	      myAgent.send(order);
  	      mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
  	                               MessageTemplate.MatchInReplyTo(order.getReplyWith()));
  	      step = 3;
  	      break;
  	    case 3:      
  	      //potwierdzenie zakupu przez agenta sprzedajacego
  	      reply = myAgent.receive(mt);
  	      if (reply != null) {
  	        if (reply.getPerformative() == ACLMessage.INFORM) {
  	          //zakup zakonczony powodzeniem
  	          System.out.println(helpKind+" kupiona za "+bestPrice+" od "+reply.getSender().getLocalName());
  			  System.out.println("Czekam na nowa dyspozycje kupna.");
  			  helpKind = "";
  	          //myAgent.doDelete();
  	        }
  	        else {
  	          System.out.println("Zakup nieudany. "+helpKind+" zostala sprzedana w miedzyczasie.");
  	        }
  	        step = 4;	//konczy cala interakcje, ktorej celem jest kupno
  	      }
  	      else {
  	        block();
  	      }
  	      break;
  	    }        
  	  }

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}
	

	
}
