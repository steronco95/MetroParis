package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private Graph<Fermata,DefaultEdge> graph;
	private List<Fermata> fermate;
	private Map<Integer,Fermata> fermateIdMap;
	
	
	public Model() {
		this.graph = new SimpleDirectedGraph<>(DefaultEdge.class); //ho creato il grafo ma devo ancora popolarlo;
		
		MetroDAO dao = new MetroDAO();
		
		this.fermate = dao.getAllFermate();
		this.fermateIdMap = new HashMap<>();
		
		for(Fermata f : this.fermate) {
			fermateIdMap.put(f.getIdFermata(),f);
		}
		
		//fermate è una lista di tutti i vertici del grafo!
		
		Graphs.addAllVertices(this.graph,this.fermate);
		
//		System.out.println(this.graph);
		
		//CREAZIONE ARCHI ---- METODO 1 (COPPIE DI VERTICI) [adatto a grafi piccoli];
//		
//		for(Fermata fp : this.fermate) {
//			for(Fermata fa : this.fermate) {
//				if(dao.fermateConnesse(fp, fa)) {
//					this.graph.addEdge(fp, fa);
//				}
//			}
//		}
		
//		System.out.println(this.graph);
		
		//CREAZIONE DEGLI ARCHI ---- METODO 2 (da un vertice trova tutti i connessi)
		
//		for(Fermata fp : this.fermate) {
//			List<Fermata> connesse = dao.fermateSuccessive(fp, fermateIdMap);
//					
//			
//			for(Fermata fa : connesse) {
//				this.graph.addEdge(fp, fa);
//			}
//		}
//		
		

		//CREAZIONE ARCHI ----- METODO 3(chiedo al db l'elenco degli archi)
		
		List<CoppiaFermate> coppie = dao.coppieFermate(fermateIdMap);
		
		for(CoppiaFermate c : coppie) {
			this.graph.addEdge(c.getFp(), c.getFa());
		}
		System.out.printf("Grafo caricato con %d vertici e %d archi\n" , this.graph.vertexSet().size(), this.graph.edgeSet().size());
		System.out.println(this.graph);
	}
	
	public static void main (String args[]) {
		
		Model m = new Model();
		
	}
	
	
	
}
