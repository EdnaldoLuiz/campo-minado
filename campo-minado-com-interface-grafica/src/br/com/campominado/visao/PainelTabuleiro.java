package br.com.campominado.visao;

import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import br.com.campominado.modelo.Tabuleiro;

public class PainelTabuleiro extends JPanel {

	private static final long serialVersionUID = 1L;

	public PainelTabuleiro(Tabuleiro tabuleiro) {
		//layout em grade
		setLayout(new GridLayout(tabuleiro.getLinhas(), tabuleiro.getColunas()));
		//adicionar botão para cada campo
		tabuleiro.paraCadaCampo(c -> add(new BotaoCampo(c)));
		//observador que sabera quando ganhou ou perdeu
		tabuleiro.registrarObservador(e -> {
			
			SwingUtilities.invokeLater(() -> {
			if(e.isGanhou()) {
				JOptionPane.showMessageDialog(this, "Ganhou!! :)");
			} else {
				JOptionPane.showMessageDialog(this, "Perdeu!! :(");
			}
			
			tabuleiro.reiniciar();
		});
	});
}

}
