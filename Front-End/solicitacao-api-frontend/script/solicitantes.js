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



function abrirModalCriacaoSolicitante() {
    const form = document.getElementById('formCriarSolicitante');
    form?.reset();

    const modal = document.getElementById('createSolicitanteModal');
    if (modal) modal.style.display = 'flex';
}

function fecharModalCriacaoSolicitante() {
    const modal = document.getElementById('createSolicitanteModal');
    if (modal) modal.style.display = 'none';
}

function configurarFormularioCriacaoSolicitante() {
    const form = document.getElementById('formCriarSolicitante');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const nome = document.getElementById('createNomeSolicitante').value.trim();
        const cpfCnpj = document.getElementById('createCpfCnpjSolicitante').value.trim();

        if (!nome || !cpfCnpj) {
            alert('Por favor, preencha todos os campos!');
            return;
        }

        const solicitanteDTO = { nome, cpfCnpj };

        await enviarSolicitante(solicitanteDTO, form);
    });
}

async function enviarSolicitante(solicitanteDTO, form) {
    try {
        const resposta = await fetch(`${API_URL}/solicitantes`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(solicitanteDTO)
        });

        if (resposta.ok) {
            fecharModalCriacaoSolicitante();
            form.reset();
            carregarSolicitantes(); 
            return;
        }

        const erroData = await resposta.json().catch(() => null);
        alert(erroData?.message || 'Erro ao cadastrar solicitante. Verifique o CPF/CNPJ informado.');

    } catch (erro) {
        console.error('Erro de conexão ao criar solicitante:', erro);
        alert('Erro de conexão com o servidor.');
    }
}

function configurarEventosSolicitante() {
    // Abrir e fechar modal
    document.getElementById('btnAbrirCriarSolicitante')?.addEventListener('click', abrirModalCriacaoSolicitante);
    document.getElementById('btnCancelarCriarSolicitante')?.addEventListener('click', fecharModalCriacaoSolicitante);

    // Fechar modal clicando fora dele
    const modal = document.getElementById('createSolicitanteModal');
    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) fecharModalCriacaoSolicitante();
        });
    }
}

// load

document.addEventListener('DOMContentLoaded', () => {
    carregarSolicitantes();
    configurarFormularioCriacaoSolicitante();
    configurarEventosSolicitante();
});
