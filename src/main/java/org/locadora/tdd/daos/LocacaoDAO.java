package org.locadora.tdd.daos;

import java.util.List;
import org.locadora.tdd.entidades.Locacao;

public interface LocacaoDAO {

	public void salvar(Locacao locacao);

	public List<Locacao> obterLocacoesPendentes();
}
