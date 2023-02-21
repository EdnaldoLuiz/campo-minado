package br.com.campominado.modelo;

import java.util.ArrayList;
import java.util.List;

import br.com.campominado.excecao.ExplosaoException;

public class Campo {
	
	private final int linha;
	private final int coluna;
	
	private boolean minado = false;
	private boolean aberto = false;
	private boolean marcado = false;
	
	//lista para representar os vizinhos no campo
	private List<Campo> vizinhos = new ArrayList<>();
	
	//um campo é feito de uma linha e uma coluna
	public Campo(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;
	}
	
	/*se estiver na horizontal e vertical e a distancia for 1, sera um vizinho
	* ex: L1,C2 - L2,C3 = L1,C1 , são vizinhos
	* se estiver na diagonal e a distancia for 2, sera um vizinho
	* ex: L3,C3 - L1,C1 = L2,C2, são vizinhos
	*/
	 
	public boolean adicionarVizinho(Campo vizinho) {
		//se os valores forem diferentes dos valores passados, resultara em uma diagonal
		boolean linhaDiferente = linha != vizinho.linha;
		boolean colunaDiferente = coluna != vizinho.coluna;
		boolean diagonal = linhaDiferente && colunaDiferente;
		
		//calcula a diferença das distancias
		//Math.abs foi usado para inverter os valores negativos em positivos
		int deltaLinha = Math.abs(linha - vizinho.linha);
		int deltaColuna = Math.abs(coluna - vizinho.coluna);
		int deltaGeral = deltaColuna + deltaLinha;
		
		//ira adicionar os vizinhos se estiverem na "cruz" com distancia de 1 ou na diagonal de 2
		if(deltaGeral == 1 && !diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else if(deltaGeral == 2 && diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else {
			return false;
		}
	}
	
	//ira desmarcar, podendo marcar um novo campo
	public void alternarMarcacao() {
		if(!aberto) {
			marcado = !marcado;
		}
	}

	/*
	 * logica de só poder abrir se não estiver aberto e nem estiver marcado
	 * se estiver minado, ira causar uma explosão
	 * se os blocos vizinhos estiverem seguros, eles serão abertos
	*/
	public boolean abrir() {
		if(!aberto && !marcado) {
			aberto = true;
			
			if(minado) {
				throw new ExplosaoException();
			}
			
			if(vizinhancaSegura()) {
				vizinhos.forEach(v -> v.abrir());
			}
		
			return true;
		} else {
			return false;
		}
	}
	
	//se a vizinhança estiver segura, abrir as areas em volta
	boolean vizinhancaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado);
	}
	
	//ira adicionar uma mina no campo que por padrão esta em false
	public void minar() {
		minado = true;
	}
	
	public boolean isMinado() {
		return minado;
	}
	
	public boolean isMarcado() {
		return marcado;
	}
	
	void setAberto(boolean aberto) {
		this.aberto = aberto;
	}
	
	public boolean isAberto() {
		return aberto;
	}
	
	public boolean isFechado() {
		return !aberto;
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}
	
	//desvendado = quando não esta minado e aberto
	//protegido = quando estiver minado e marcado
	//o objetivo sera alcançado em um campo
	boolean objetivoAlcancado() {
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		return desvendado || protegido;
	}
	
	//filtrar apenas os vizinhos que tem minas e usando o count para
	//mostrar no jogo a quantidade de minas na vizinhança
	long minasNaVizinhanca() {
		return vizinhos.stream().filter(v -> v.minado).count();
	}
	
	//ira reiniciar zerando os atributos
	void reiniciar() {
		aberto = false;
		minado = false;
		marcado = false;
	}
	
	/*é reponsavel por definir os icones, 
	* se marcado sera um "x"
	* se estiver aberto e minado sera um "*"
	* se estiver aberto e sem minas na vizinhança ira mostrar a quantidade de minas por perto
	* se estiver aberto e sem minas " " (vazio)
	* se não estiver marcado, nem aberto e nem for vizinho "?"
	*/
	public String toString() {
		if(marcado) {
			return "x";
		} else if(aberto && minado) {
			return "*";
		} else if(aberto && minasNaVizinhanca() > 0) {
			return Long.toString(minasNaVizinhanca());
		} else if(aberto) {
			return " ";
		} else {
			return "?";
		}
 	}
}
