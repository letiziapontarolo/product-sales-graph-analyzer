package it.polito.tdp.gosales.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.gosales.dao.GOsalesDAO;

public class Model {

	private Graph<Products, DefaultWeightedEdge> grafo;
	private GOsalesDAO dao;
	private Map<Integer, Products> productsIdMap;
	private List<Arco> archi;
	private List<Products> percorsoMigliore;
	private int numArchiMax;

	public Model() {
		dao = new GOsalesDAO();
	}

	public List<Products> percorso(int n) {

		percorsoMigliore = new ArrayList<Products>();
		numArchiMax = 0;
		Products verticePartenza = productsIdMap.get(n);

		List<Products> parziale = new ArrayList<Products>();
		parziale.add(verticePartenza);

		percorsoMigliore = new ArrayList<>(parziale);

		ricorsione(parziale);

		return percorsoMigliore;
	}

	public void ricorsione(List<Products> parziale) {

		Products verticeCorrente = parziale.get(parziale.size() - 1);
		List<Products> vicini = Graphs.neighborListOf(this.grafo, verticeCorrente);

		if (parziale.size() - 1 > numArchiMax) {
			numArchiMax = parziale.size() - 1;
			percorsoMigliore = new ArrayList<Products>(parziale);
		}

		for (Products p : vicini) {

			if (!parziale.contains(p)) {

				double pesoNuovo = grafo.getEdgeWeight(
						grafo.getEdge(verticeCorrente, p));

				boolean pesoValido = true;

				if (parziale.size() >= 2) {

					Products verticePrecedente = parziale.get(parziale.size() - 2);

					double pesoPrecedente = grafo.getEdgeWeight(
							grafo.getEdge(verticePrecedente, verticeCorrente));

					pesoValido = pesoNuovo >= pesoPrecedente;
				}

				if (pesoValido) {
					parziale.add(p);
					ricorsione(parziale);
					parziale.remove(parziale.size() - 1);
				}
			}
		}

	}

	public List<Integer> listaVertici() {

		List<Integer> lista = new ArrayList<Integer>();
		for (Products p : productsIdMap.values()) {
			lista.add(p.getNumber());
		}
		Collections.sort(lista);
		return lista;
	}

	public void creaGrafo(String brand, int anno) {

		productsIdMap = new HashMap<Integer, Products>();
		grafo = new SimpleWeightedGraph<Products, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		archi = new ArrayList<Arco>();

		this.dao.creaVertici(productsIdMap, brand);
		Graphs.addAllVertices(this.grafo, productsIdMap.values());

		this.dao.creaArchi(archi, brand, anno, productsIdMap);
		if (!archi.isEmpty()) {
			for (Arco a : archi) {
				Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
			}
		}

	}

	public String archiMaggiori() {

		String result = "Top 3 archi:\n";

		List<Arco> treArchi = new ArrayList<Arco>();
		Map<Products, Integer> conteggi = new HashMap<>();

		Collections.sort(this.archi, new Comparator<Arco>() {
			@Override
			public int compare(Arco a1, Arco a2) {
				return (int) (a1.getPeso() - a2.getPeso());
			}
		});

		Collections.reverse(archi);

		if (archi.size() >= 3) {
			treArchi.add(archi.get(0));
			treArchi.add(archi.get(1));
			treArchi.add(archi.get(2));
		}

		if (archi.size() == 2) {
			treArchi.add(archi.get(0));
			treArchi.add(archi.get(1));
		}

		if (archi.size() == 1) {
			treArchi.add(archi.get(0));
		}

		if (archi.size() == 0) {
			return "Non ci sono archi nel grafo";
		}

		for (Arco a : treArchi) {

			conteggi.put(
					a.getP1(),
					conteggi.getOrDefault(a.getP1(), 0) + 1);

			conteggi.put(
					a.getP2(),
					conteggi.getOrDefault(a.getP2(), 0) + 1);

			result += "Arco da " + a.getP1().getNumber()
					+ " a " + a.getP2().getNumber()
					+ ", peso = " + a.getPeso() + "\n";
		}

		result += "Nodi che compaiono almeno due volte:\n";

		for (Map.Entry<Products, Integer> entry : conteggi.entrySet()) {

			if (entry.getValue() >= 2) {
				result += entry.getKey() + "\n";
			}
		}
		return result;
	}

	public List<Integer> listaAnni() {
		return this.dao.listaAnni();
	}

	public List<String> listaBrand() {
		return this.dao.listaBrand();
	}

	public int numeroVertici() {
		return this.grafo.vertexSet().size();
	}

	public int numeroArchi() {
		return this.grafo.edgeSet().size();
	}

	public double getPesoArco(Products p1, Products p2) {
		DefaultWeightedEdge arco = grafo.getEdge(p1, p2);

		if (arco == null) {
			throw new IllegalArgumentException(
					"Non esiste un arco tra " + p1 + " e " + p2);
		}

		return grafo.getEdgeWeight(arco);
	}

}
