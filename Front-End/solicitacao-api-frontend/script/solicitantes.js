const API_URL = 'http://localhost:8080';

let listaSolicitantes = [];
let solicitanteSelecionadoId = null;


// recursos solicitantes

async function carregarSolicitantes() {
    try {
        const resposta = await fetch(`${API_URL}/solicitantes`);
        listaSolicitantes = await resposta.json();

        renderizarSolicitantes();
    } catch (erro) {
        console.error('Erro ao carregar solicitantes:', erro);
    }
}

function renderizarSolicitantes() {
    const tabelaBody = document.getElementById('tableSolicitantesBody');
    if (!tabelaBody) return;

    tabelaBody.innerHTML = '';

    listaSolicitantes.forEach(solicitante => {
        const linha = criarLinhaSolicitante(solicitante);
        tabelaBody.appendChild(linha);
    });
}

function criarLinhaSolicitante(solicitante) {
    const linha = document.createElement('tr');

    linha.innerHTML = `
        <td>${solicitante.id}</td>
        <td>${solicitante.nome}</td>
        <td>${solicitante.cpfCnpj}</td>
        <td>
            <button class="btn-filter" onclick="carregarSolicitacoesDoSolicitante(${solicitante.id})">
                Ver mais
            </button>
        </td>
    `;

    return linha;
}

// recursos solicitações do solicitantes

async function carregarSolicitacoesDoSolicitante(solicitanteId) {
    solicitanteSelecionadoId = solicitanteId;

    try {
        const resposta = await fetch(`${API_URL}/solicitantes/${solicitanteId}/solicitacoes`);

        if (!resposta.ok) {
            alert('Erro ao buscar as solicitações do solicitante.');
            return;
        }

        const solicitacoes = await resposta.json();
        renderizarPainelSolicitacoes(solicitacoes);

    } catch (erro) {
        console.error('Erro de conexão ao carregar solicitações:', erro);
        alert('Erro de conexão com o servidor.');
    }
}

function renderizarPainelSolicitacoes(solicitacoes) {
    const painelContainer = document.getElementById('panelSolicitacoes');
    if (!painelContainer) return;

    painelContainer.innerHTML = '';

    if (solicitacoes.length === 0) {
        painelContainer.innerHTML = '<p class="panel-empty-message">Nenhuma solicitação encontrada para este solicitante.</p>';
        return;
    }

    solicitacoes.forEach(solicitacao => {
        const card = criarCardSolicitacao(solicitacao);
        painelContainer.appendChild(card);
    });
}

function criarCardSolicitacao(solicitacao) {
    const card = document.createElement('div');
    card.className = 'panel-item-card';

    // Suporta tanto solicitacao.status quanto solicitacao.statusSolicitacao
    const statusTexto = solicitacao.status || solicitacao.statusSolicitacao || '';
    const statusClass = statusTexto ? statusTexto.toLowerCase() : '';

    card.innerHTML = `
        <div class="card-header">
            <span class="card-id">#${solicitacao.id}</span>
            <span class="status ${statusClass}">${statusTexto}</span>
        </div>
        <div class="card-body">
            <p><strong>Categoria:</strong> ${solicitacao.categoriaNome}</p>
            <p><strong>Valor:</strong> R$ ${solicitacao.valor}</p>
        </div>
    `;

    return card;
}

// load

document.addEventListener('DOMContentLoaded', () => {
    carregarSolicitantes();
});