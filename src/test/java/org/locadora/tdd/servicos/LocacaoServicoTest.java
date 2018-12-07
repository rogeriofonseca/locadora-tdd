package org.locadora.tdd.servicos;

import org.junit.Test;
import org.locadora.tdd.exceptions.FilmeSemEstoqueException;

public class LocacaoServicoTest {
	
	@Test
	public void deveAlugarFilme() throws Exception {
		
		// cenario
		
		// acao

		// verificacao
                
                // verificar valor da locacao
                // data da locacao
                // data da devolucao atraves da diferenca de dias
	}

        @Test
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		// cenario
                // acao
                
                //verificar exception
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
                // acao
                
                //verificar exception
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws Exception {
		// cenario
                // acao
                
                //verificar exception
	}

	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
                // cenario
		
		// acao

		// verificacao
                
                // verificar quantidade de invocacoes no metodo
                // verificar usuario especifico
                // verificar usuario nunca notificado
	}

	@Test
	public void deveProrrogarUmaLocacao() {
		// cenario
		
		// acao

		// verificacao
                
                // verificar referencia de objeto
                // verificar valor final da locacao
	}

}
