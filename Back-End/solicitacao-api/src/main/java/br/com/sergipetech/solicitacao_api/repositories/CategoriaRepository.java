package br.com.sergipetech.solicitacao_api.repositories;

import br.com.sergipetech.solicitacao_api.entities.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoriaRepository extends JpaRepository<Categoria, Long> {



}
