package org.locadora.tdd.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.locadora.tdd.builders.FilmeBuilder.umFilme;
import static org.locadora.tdd.builders.FilmeBuilder.umFilmeSemEstoque;
import static org.locadora.tdd.builders.LocacaoBuilder.umLocacao;
import static org.locadora.tdd.builders.UsuarioBuilder.umUsuario;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.locadora.tdd.daos.LocacaoDAO;
import org.locadora.tdd.entidades.Filme;
import org.locadora.tdd.entidades.Locacao;
import org.locadora.tdd.entidades.Usuario;
import org.locadora.tdd.exceptions.FilmeSemEstoqueException;
import org.locadora.tdd.exceptions.LocadoraException;
import org.locadora.tdd.utils.DataUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class LocacaoServicoTest {
	
	@InjectMocks
	private LocacaoServico locacaoServico;
	
	@Mock
	private LocacaoDAO locacaoDAO;
	
	@Mock
	private EmailService emailService;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void deveAlugarFilme() throws Exception {
		
		// cenario
		Usuario usuario = umUsuario().agora();
		
		List<Filme> filmes  = Arrays.asList(umFilme().comValor(5.0).agora());
		
		// acao
		Locacao locacao = locacaoServico.alugarFilme(usuario, filmes); 
		

		// verificacao
		assertEquals(5, locacao.getValor(), 0.1);
		assertTrue(DataUtils.isMesmaData(new Date(), locacao.getDataLocacao()));
		assertTrue(DataUtils.isMesmaData(DataUtils.obterDataComDiferencaDias(1), locacao.getDataRetorno()));
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		Usuario usuario = umUsuario().agora();
		
		List<Filme> filmes  = Arrays.asList(umFilmeSemEstoque().agora());
		
		locacaoServico.alugarFilme(usuario, filmes);
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {

		List<Filme> filmes  = Arrays.asList(umFilme().comValor(5.0).agora());
		
		try {
			locacaoServico.alugarFilme(null, filmes);
			fail();
		} catch (LocadoraException e) {
			assertEquals("usuário não pode ser nulo", e.getMessage());
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws Exception {
		
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filmes não pode ser vazio");

		locacaoServico.alugarFilme(usuario, null);
	}

	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {

		Usuario usuario1 = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("José").agora();
		Usuario usuario3 = umUsuario().comNome("Maria").agora();
		
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().atrasada().comUsuario(usuario1).agora(),
				umLocacao().comUsuario(usuario2).agora(),
				umLocacao().atrasada().comUsuario(usuario3).agora(),
				umLocacao().atrasada().comUsuario(usuario3).agora());
		
		when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacoes);
		
		locacaoServico.notificarAtrasos();
		
		verify(emailService, times(3)).notificarAtraso(Mockito.any(Usuario.class));
		verify(emailService).notificarAtraso(usuario1);
		verify(emailService, never()).notificarAtraso(usuario2);
		
	}

	@Test
	public void deveProrrogarUmaLocacao() {
		
		Locacao locacao = umLocacao().agora();
		
		locacaoServico.prorrogarLocacao(locacao, 3);
		
		ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
		
		verify(locacaoDAO).salvar(argumentCaptor.capture());
		
		Locacao locacaoRetornada = argumentCaptor.getValue();
		
		assertEquals(12, locacaoRetornada.getValor(), 0.1);
	}

}
