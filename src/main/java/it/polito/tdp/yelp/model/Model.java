package it.polito.tdp.yelp.model;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	
	private Graph<User, DefaultWeightedEdge> grafo;
	private List<User> users;

	
	public Model() {
		this.dao= new YelpDao();

		
		
	}
	
	public List<User> getUsersVertici(int n){
		return this.dao.getVertici(n);
	}
	
	
	public void creaGrafo(int n, int year) {
		this.grafo= new SimpleWeightedGraph<User, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		users= new LinkedList<User>(this.dao.getVertici(n));
		List<Adiacenza> archi= new LinkedList<Adiacenza>();
		
		Graphs.addAllVertices(this.grafo, users);
		
		for(User u1: users) {
			for(User u2: users) {
				if(!u1.equals(u2) && u1.getUserId().compareTo(u2.getUserId())<0) {
					archi.addAll(this.dao.getArchi(u1, u2, year));
				}
			}
		}
		for(Adiacenza a: archi) {
			Graphs.addEdgeWithVertices(this.grafo, a.getUser1(), a.getUser2(), a.getPeso());
		}
		
	}
	
	public String nVertici() {
		return "Grafo creato! " + "\n"+ "#vertici: "+this.grafo.vertexSet().size()+"\n";
	}
	
	public String nArchi() {
		return "#archi: "+this.grafo.edgeSet().size();
	}
	
	public List<User> getMaxCompatibility(User u){
		
		List<User> list= new LinkedList<User>();
		int pesoMax=0;
		
		for(User user: Graphs.neighborListOf(this.grafo, u)) {
			DefaultWeightedEdge e= this.grafo.getEdge(user, u);
			int peso=(int) this.grafo.getEdgeWeight(e);
			if(peso>=pesoMax) {
				pesoMax=peso;
			}	
		}
		
		for(DefaultWeightedEdge e: this.grafo.edgesOf(u)) {
			if((int) this.grafo.getEdgeWeight(e)== pesoMax) {
				User u2= Graphs.getOppositeVertex(this.grafo, e, u);
				list.add(u2);
			}
		}
		
		return list;
		
	}
	
	
}
