package br.com.campominado.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import br.com.campominado.excecao.ExplosaoException;

public class Tabuleiro {
	
	//os elementos presentes no tabuleiro
	private int linhas;
	private int colunas;
	private int minas;
	
	//o tabuleiro possui uma lista de campos, fazendo com que fassa o efeito de
	//matriz das linhas e colunas
	private final List<Campo> campos = new ArrayList<>();
	
	public Tabuleiro(int linhas, int colunas, int minas) {
		this.linhas = linhas;
		this.colunas = colunas;
		this.minas = minas;
		
		//sempre que um tabuleiro for inicializado, serão gerados campos
		//associados vizinhos aos campos e sortear as minas
		gerarCampos();
		associarOsVizinhos();
		sortearMinas();
	}
	
	//metodo usado para abrir um determinado campo, recebendo os parametros
	//passados
	public void abrir(int linha, int coluna) {
		try {
			campos.parallelStream()
			.filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
			.findFirst()
			.ifPresent(c -> c.abrir());;
		} catch(ExplosaoException e) {
			campos.forEach(c -> c.setAberto(true));
			throw e;
		}
	}
	//
	public void alternarMarca(int linha, int coluna) {
		campos.parallelStream()
		.filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
		.findFirst()
		.ifPresent(c -> c.alternarMarcacao());;
	}

	//ira gerar os campos, para cada linha feita, sera feito uma coluna, quando tivermos um
	//elemento de cada um desses, ira totalizar um novo campo feito da linha e da coluna
	private void gerarCampos() {
		for (int linha = 0; linha < linhas; linha++) {
			for(int coluna = 0; coluna < colunas; coluna++) {
				campos.add(new Campo(linha, coluna));
			}
		}
	}
	
	//para cada campo ira associar um outro campo e adiciona-lo com vizinho
	private void associarOsVizinhos() {
		for(Campo c1: campos) {
			for(Campo c2: campos) {
				c1.adicionarVizinho(c2);
			}
		}
	}
	
	//metodo usado para sortear as minas no campo, pegando um numero aleatorio e multiplicando
	//ele pelo tamanho do campo, dps pegando o campo aleatorio que foi gerado e adicionando uma
	//mina.
	private void sortearMinas() {
		long minasAtivas = 0;
		Predicate<Campo> minado = c -> c.isMinado();
		do {
			int aleatorio = (int) (Math.random() * campos.size());
			campos.get(aleatorio).minar();
			minasAtivas = campos.stream().filter(minado).count();
		} while(minasAtivas < minas);
		}
	
	//ira verificar se o objetivo foi alcançado chamando o metodo 
	//objetivo alcançado da classe Campo e verificando atraves do allMatch
	//se todos os campos foram concluidos
	public boolean objetivoAlcancado() {
		Predicate<Campo> objetivoConcluido = c -> c.objetivoAlcancado();
		return campos.stream().allMatch(objetivoConcluido);
	}
	
	//ira reiniciar o jogo usado na classe TabuleiroConsole e chamando o
	//reiniciar da classe Campo, reiniciando cada campo e sorteando as minas
	public void reiniciar() {
		campos.stream().forEach(c -> c.reiniciar());
		sortearMinas();
	}
	
	//metodo usado para adicionar os indices de linha e coluna
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		//adicionando os indices das colunas
		sb.append("  ");
		for(int c = 0; c < colunas; c++) {
			sb.append(" ");
			sb.append(c);
			sb.append(" ");
		}
			/* quebrando a linha das colunas para nao atrapalhar a primeira linha
			*  como no exemplo abaixo 
			* 	   0  1  2  3  4  5 0  ?  ?  ?  ?  ?  ? 
			*	1  ?  ?  ?  ?  ?  ? 
			*   etc...
			*/
		sb.append("\n");
		
		//adicionando os indices das linhas
		int i = 0;
		for(int l = 0; l < linhas; l++) {
			sb.append(l);
			sb.append(" ");
			//desenhando os campos, para cada linha gerar um coluna, formando um campo
			for(int c = 0; c < colunas; c++) {
				sb.append(" ");
				sb.append(campos.get(i));
				sb.append(" ");
				i++;
			}
			//quebrando a linha para as colunas não ficarem com efeito de linha
			sb.append("\n");
		}
		//ira desenhar tudo usando o toString
		return sb.toString();
	}
	
	}


