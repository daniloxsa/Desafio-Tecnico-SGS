package br.com.sergipetech.solicitacao_api.repositories;

import br.com.sergipetech.solicitacao_api.entities.Solicitante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitanteRepository extends JpaRepository<Solicitante, Long> {

    boolean existsByCpfCnpj(String cpfCnpj);


}
