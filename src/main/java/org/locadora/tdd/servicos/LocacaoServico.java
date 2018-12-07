package org.locadora.tdd.servicos;

import java.util.Date;
import java.util.List;

import org.locadora.tdd.daos.LocacaoDAO;
import org.locadora.tdd.entidades.Filme;
import org.locadora.tdd.entidades.Locacao;
import org.locadora.tdd.entidades.Usuario;
import org.locadora.tdd.exceptions.FilmeSemEstoqueException;
import org.locadora.tdd.exceptions.LocadoraException;
import org.locadora.tdd.utils.DataUtils;

public class LocacaoServico {

	private LocacaoDAO locacaoDAO;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

		if (usuario == null) {
			throw new LocadoraException("usuário não pode ser nulo");
		}

		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filmes não pode ser vazio");
		}

		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}

		Locacao locacao = new Locacao();
		locacao.setUsuario(usuario);
		locacao.setFilmes(filmes);
		locacao.setDataLocacao(new Date()); 

		Double valorTotal = 0d;

		for (Filme filme : filmes) {
			valorTotal += filme.getPrecoLocacao();
		}

		locacao.setValor(valorTotal);

		Date dataEntrega = new Date();
		dataEntrega = DataUtils.adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);

		return locacao;
	}

	public void notificarAtrasos() {
		List<Locacao> locacoes = locacaoDAO.obterLocacoesPendentes();
		
		for (Locacao locacao : locacoes) {
			if (locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
		
	}

	public void prorrogarLocacao(Locacao locacao, int dias) {
		
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes()); 
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);

		locacaoDAO.salvar(novaLocacao);
	}

}
