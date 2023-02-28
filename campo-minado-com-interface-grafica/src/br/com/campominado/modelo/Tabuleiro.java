package br.com.campominado.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObservador {
	
	//os elementos presentes no tabuleiro
	private final int linhas;
	private final int colunas;
	private final int minas;
	
	//o tabuleiro possui uma lista de campos, fazendo com que fassa o efeito de
	//matriz das linhas e colunas
	private final List<Campo> campos = new ArrayList<>();
	private final List<Consumer<ResultadoEvento>> observadores = new ArrayList<>();
	
	public Tabuleiro(int linhas, int colunas, int minas) {
		this.linhas = linhas;
		this.colunas = colunas;
		this.minas = minas;
		
		gerarCampos();
		associarOsVizinhos();
		sortearMinas();
	}
	
	public void paraCadaCampo(Consumer<Campo> funcao) {
		campos.forEach(funcao);
	}
	
	public int getLinhas() {
		return linhas;
	}

	public int getColunas() {
		return colunas;
	}

	public void registrarObservador(Consumer<ResultadoEvento> observador) {
		observadores.add(observador);
	}
	
	public void notificarObservador(boolean resultado) {
		observadores.stream().forEach(o -> o.accept(new ResultadoEvento(resultado)));
	}
	//metodo usado para abrir um determinado campo, recebendo os parametros
	//passados
	public void abrir(int linha, int coluna) {
			campos.parallelStream()
			.filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
			.findFirst()
			.ifPresent(c -> c.abrir());;
	}
	
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
				Campo campo = new Campo(linha, coluna);
				campo.registrarObservador(this);
				campos.add(campo);
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
	
	//metodo usado para sortear as minas no campo
	private void sortearMinas() {
		long minasAtivas = 0;
		Predicate<Campo> minado = c -> c.isMinado();
		do {
			int aleatorio = (int) (Math.random() * campos.size());
			campos.get(aleatorio).minar();
			minasAtivas = campos.stream().filter(minado).count();
		} while(minasAtivas < minas);
		}
	
	//ira verificar se o objetivo foi alcançado verificando todos campos no allMatch
	public boolean objetivoAlcancado() {
		Predicate<Campo> objetivoConcluido = c -> c.objetivoAlcancado();
		return campos.stream().allMatch(objetivoConcluido);
	}
	
	//ira reiniciar o jogo o usando reiniciar da classe Campo
	public void reiniciar() {
		campos.stream().forEach(c -> c.reiniciar());
		sortearMinas();
	}

	//observer que notifica o andar do jogo
	@Override
	public void eventoOcorreu(Campo campo, CampoEvento evento) {
		if(evento == CampoEvento.EXPLODIR) {
			mostrarMinas();
			notificarObservador(false);
		} else if(objetivoAlcancado()) {
			notificarObservador(true);
		}
	}
	
	private void mostrarMinas() {
		campos.stream()
		.filter(c -> c.isMinado())
		.filter(c -> !c.isMarcado())
		.forEach(c -> c.setAberto(true));
	}
	
}


