package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.yelp.model.Event.EventType;

public class Simulatore {
	
	//Dati in ingresso
	private int x1;
	private int x2;
	
	//Dati in uscita: per ciascun giornalista dimmi quanti utenti intervista
					//come identifichiamo i giornalisti? O da un int (compreso tra 0 e x1-1), 
					//oppure creo x1 oggetti di classe Intervistatore
	private List<Giornalista> giornalisti;
	private int numeroGiorni;
					
	//Modello del mondo: persone già intervistate che quindi non verranno più intervistate
	private Set<User> intervistati; 
	private Graph<User, DefaultWeightedEdge> grafo;
	
	//Coda degli eventi
	private PriorityQueue <Event> queue;
	
	public Simulatore(Graph<User, DefaultWeightedEdge> grafo) {
		this.grafo=grafo;
	}

	public void init(int x1, int x2) {
		this.x1=x1;
		this.x2=x2;
		
		this.intervistati= new HashSet<User>();
		this.numeroGiorni=0;
		
		this.giornalisti= new ArrayList<Giornalista>();
		for(int id=0; id<this.x1; id++) {
			this.giornalisti.add(new Giornalista(id));
		}
		//pre-carico la coda
		for(Giornalista g: this.giornalisti) {
			User intervistato= selezionaIntervistato(this.grafo.vertexSet());
			
			this.intervistati.add(intervistato);//così non lo scelgo più
			g.incrementaINtervistati();
			
			this.queue.add(new Event(1,intervistato, g, EventType.DA_INTERVISTARE));
		}
		
	}
	
	public void run() {
		while(this.queue.isEmpty() && this.intervistati.size()<x2) {
			Event e= this.queue.poll();
			this.numeroGiorni=e.getGiorno();
			processEvent(e);			
		}
		
	}


	private void processEvent(Event e) {
		switch(e.getType()) {
		case DA_INTERVISTARE:
			
			double caso=Math.random();
			if( caso<0.6) {
				//casoI
				User vicino= selezionaAdiacente(e.getIntervistato());
				if(vicino==null) {
					vicino=selezionaIntervistato(this.grafo.vertexSet());
				}
				this.queue.add(new Event(e.getGiorno()+1,vicino, e.getGiornalista(), EventType.DA_INTERVISTARE));
				this.intervistati.add(vicino);
				e.getGiornalista().incrementaINtervistati();
			}else if(caso<0.8) {
				//casoII
				this.queue.add(new Event(e.getGiorno()+1, e.getIntervistato(), e.getGiornalista(), EventType.FERIE));
				
			}else {
				//casoIII:domani continuo con lo stesso utente
				this.queue.add(new Event(e.getGiorno()+1, e.getIntervistato(), e.getGiornalista(), EventType.DA_INTERVISTARE));
			}
			break;
		case FERIE:
			break;
		}
		
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public List<Giornalista> getGiornalisti() {
		return giornalisti;
	}

	public int getNumeroGiorni() {
		return numeroGiorni;
	}
	
	/**
	 * Seleziona un intervistato dalla lista specificata, evitando di selezionare 
	 * coloro che sono già in this.intervistati
	 * @param lista
	 * @return
	 */
	private User selezionaIntervistato(Collection<User> lista) {
		//insieme dei potenziali candidati
		Set<User> candidati=new HashSet<User>(lista);
		candidati.removeAll(this.intervistati);
		
		int scelto=	(int)(Math.random()*candidati.size());
		List<User> listUtenti= new ArrayList<User>(candidati);
		return listUtenti.get(scelto);
	
	}
	
	private User selezionaAdiacente(User u) {
		List<User> vicini= Graphs.neighborListOf(this.grafo, u);
		vicini.removeAll(this.intervistati);
		
		if(vicini.size()==0) {
			return null;
		}
		double max =0;
		for(User v: vicini) {
			double peso=this.grafo.getEdgeWeight(this.grafo.getEdge(u, v));
			if(peso>max) {
				max=peso;
				
			}
		}
		List<User> migliori= new ArrayList<>();
		for(User v: vicini) {
			double peso=this.grafo.getEdgeWeight(this.grafo.getEdge(u, v));
			if(peso==max) {
				migliori.add(v);
			}
		}
		int scelto= (int)(Math.random()*migliori.size());
		return migliori.get(scelto);
	}



}
