package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

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
//		System.out.println(this.graph);
	}
	
	/**
	 * visita il grafo con la strategia Breath first e ritorna l'insieme dei veritici incontrati;
	 * @param f vertice di partenza 
	 * @return lista dei cammini dal quel virtice di partenza
	 */
	public List<Fermata> visitaInAmpiezza(Fermata f) {
		List <Fermata> visita = new ArrayList<Fermata>();
	
		BreadthFirstIterator<Fermata,DefaultEdge> bfv = new BreadthFirstIterator<>(graph,f);
		
		while(bfv.hasNext()) {
			
			visita.add(bfv.next());
			
		}
		
		
		
		return visita;
	}
	
	public List<Fermata> visitaInProfondita(Fermata f) {
		List <Fermata> visita = new ArrayList<Fermata>();
	
		
		DepthFirstIterator<Fermata,DefaultEdge> dfv = new DepthFirstIterator<>(graph,f);
		
		while(dfv.hasNext()) {
			
			visita.add(dfv.next());
			
		}
		
		
		
		return visita;
	}
	
	public Map<Fermata,Fermata> alberoVisita(Fermata source) {
		
		Map<Fermata,Fermata> result = new HashMap<>();
		result.put(source, null);
		
		// <verticeNuovo - verticePrecedente>
		
		BreadthFirstIterator<Fermata,DefaultEdge> bfv = new BreadthFirstIterator<>(graph,source);
		
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				//la visita sta considerando un arco 
				//questo arco ha scoperto un nuovo vertice?
				//SE SI da dove provenivo?
				DefaultEdge g = e.getEdge();
				Fermata a = graph.getEdgeSource(g);
				Fermata b = graph.getEdgeTarget(g);
				if(result.containsKey(a)) {
					result.put(b, a);
				}else {
					result.put(a, b);
				}
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		while(bfv.hasNext()) {
			bfv.next();
		}
		
		return result;
	}
	
	public List<Fermata> camminiMinimi(Fermata partenza, Fermata arrivo) {
		
		DijkstraShortestPath<Fermata,DefaultEdge>  dij = new DijkstraShortestPath<>(graph);
		
		GraphPath<Fermata,DefaultEdge> cammino =  dij.getPath(partenza, arrivo);
		
		
		return cammino.getVertexList();
		
	}
	
	public static void main (String args[]) {
		
		Model m = new Model();
		
//		List<Fermata> visita = m.visitaInAmpiezza(m.fermate.get(0));
//		
//		System.out.println("\\\\\\\\\\\\\\\\\\");
//		System.out.println(visita);
//		
//		List<Fermata> vis = m.visitaInProfondita(m.fermate.get(0));
//		
//		System.out.println("\\\\\\\\\\\\\\\\\\");
//		System.out.println(vis);
//		
//		Map <Fermata,Fermata> a = m.alberoVisita(m.fermate.get(0));
//		System.out.println("\\\\\\\\\\\\\\\\\\");
//		
//		for(Fermata f : a.keySet()) {
//			System.out.format("%s -> %s \n", f, a.get(f));
//		}
		
		List <Fermata> cammino = m.camminiMinimi(m.fermate.get(0), m.fermate.get(1));
		
		System.out.println(cammino);
		
				
		
	}
	
	
	
}
