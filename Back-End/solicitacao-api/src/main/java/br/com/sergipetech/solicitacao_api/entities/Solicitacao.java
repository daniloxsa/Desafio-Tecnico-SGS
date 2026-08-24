package br.com.sergipetech.solicitacao_api.entities;

import br.com.sergipetech.solicitacao_api.enums.StatusSolicitacao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private BigDecimal valor;

    private LocalDateTime data_solicitacao;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao statusSolicitacao;

    @ManyToOne
    @JoinColumn(name = "id_solicitante")
    private Solicitante solicitante;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    public Solicitacao() {
    }

    public Solicitacao(String descricao, BigDecimal valor, LocalDateTime data_solicitacao, StatusSolicitacao statusSolicitacao) {
        this.descricao = descricao;
        this.valor = valor;
        this.data_solicitacao = data_solicitacao;
        this.statusSolicitacao = statusSolicitacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getData_solicitacao() {
        return data_solicitacao;
    }

    public void setData_solicitacao(LocalDateTime data_solicitacao) {
        this.data_solicitacao = data_solicitacao;
    }

    public StatusSolicitacao getStatusSolicitacao() {
        return statusSolicitacao;
    }

    public void setStatusSolicitacao(StatusSolicitacao statusSolicitacao) {
        this.statusSolicitacao = statusSolicitacao;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Solicitacao that = (Solicitacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
