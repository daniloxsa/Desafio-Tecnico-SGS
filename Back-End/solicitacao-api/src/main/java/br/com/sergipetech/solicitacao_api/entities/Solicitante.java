package br.com.sergipetech.solicitacao_api.entities;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Solicitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpfCnpj;

    @OneToMany(mappedBy = "solicitante")
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    public Solicitante() {
    }

    public Solicitante(String nome, String cpfCnpj) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public List<Solicitacao> getSolicitacoes() {
        return solicitacoes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Solicitante that = (Solicitante) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
