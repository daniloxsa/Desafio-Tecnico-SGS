const API_URL = 'http://localhost:8080';

let listaSolicitacoes = [];
let listaSolicitantes = [];


// recurso categorias

async function carregarCategorias() {
    try {
        const resposta = await fetch(`${API_URL}/categorias`);
        const categorias = await resposta.json();

        preencherSelectCategoria('filterCategoria', categorias, 'Categoria');
        preencherSelectCategoria('createCategoria', categorias, 'Categoria');
    } catch (erro) {
        console.error('Erro ao carregar categorias:', erro);
    }
}

function preencherSelectCategoria(idSelect, categorias, textoPadrao) {
    const select = document.getElementById(idSelect);
    if (!select) return;

    select.innerHTML = `<option value="">${textoPadrao}</option>`;

    categorias.forEach(categoria => {
        const option = document.createElement('option');
        option.value = categoria.id;
        option.textContent = categoria.nome;

        select.appendChild(option);
    });
}


// recursos solicitantes

async function carregarSolicitantes() {
    try {
        const resposta = await fetch(`${API_URL}/solicitantes`);
        listaSolicitantes = await resposta.json();

        preencherDatalistSolicitantes();
    } catch (erro) {
        console.error('Erro ao carregar solicitantes:', erro);
    }
}

function preencherDatalistSolicitantes() {
    const datalist = document.getElementById('solicitantesList');
    if (!datalist) return;

    datalist.innerHTML = '';

    listaSolicitantes.forEach(solicitante => {
        const option = document.createElement('option');
        option.value = `${solicitante.nome} (${solicitante.cpfCnpj})`;

        datalist.appendChild(option);
    });
}

// recursos solicitações

async function carregarSolicitacoes() {
    try {
        const params = obterFiltros();
        const resposta = await fetch(`${API_URL}/solicitacoes?${params.toString()}`);

        listaSolicitacoes = await resposta.json();

        renderizarSolicitacoes();
    } catch (erro) {
        console.error('Erro ao carregar solicitações:', erro);
    }
}

function obterFiltros() {
    const params = new URLSearchParams();

    const status = document.getElementById('filterStatus')?.value;
    const categoriaId = document.getElementById('filterCategoria')?.value;
    const dataInicio = document.getElementById('filterDataInicio')?.value;
    const dataFim = document.getElementById('filterDataFim')?.value;
    const valorMin = document.getElementById('filterValorMin')?.value;
    const valorMax = document.getElementById('filterValorMax')?.value;

    if (status) params.append('status', status);
    if (categoriaId) params.append('categoriaId', categoriaId);
    if (dataInicio) params.append('dataInicio', `${dataInicio}T00:00:00`);
    if (dataFim) params.append('dataFim', `${dataFim}T23:59:59`);
    if (valorMin) params.append('valorMin', valorMin);
    if (valorMax) params.append('valorMax', valorMax);

    return params;
}

function renderizarSolicitacoes() {
    const tabela = document.getElementById('tableContainer');
    if (!tabela) return;

    tabela.innerHTML = '';

    listaSolicitacoes.forEach(solicitacao => {
        const linha = criarLinhaSolicitacao(solicitacao);
        tabela.appendChild(linha);
    });
}

function criarLinhaSolicitacao(solicitacao) {
    const linha = document.createElement('tr');
    const status = solicitacao.status ? solicitacao.status.toLowerCase() : '';

    linha.innerHTML = `
        <td>${solicitacao.id}</td>
        <td>${solicitacao.solicitanteNome}</td>
        <td>${solicitacao.dataSolicitacao}</td>
        <td>${solicitacao.categoriaNome}</td>
        <td>
            <span class="status ${status}">
                ${solicitacao.status}
            </span>
        </td>
        <td>R$ ${solicitacao.valor}</td>
        <td>
            <button class="btn-details" onclick="abrirModalDetalhes(${solicitacao.id})">
                Ver mais
            </button>
        </td>
    `;

    return linha;
}

// filtros

function limparFiltros() {
    document.getElementById('filterStatus').value = '';
    document.getElementById('filterCategoria').value = '';
    document.getElementById('filterDataInicio').value = '';
    document.getElementById('filterDataFim').value = '';
    document.getElementById('filterValorMin').value = '';
    document.getElementById('filterValorMax').value = '';

    carregarSolicitacoes();
}


// modal detalhes

function abrirModalDetalhes(id) {
    const solicitacao = listaSolicitacoes.find(
        solicitacao => solicitacao.id === id
    );

    if (!solicitacao) return;

    document.getElementById('modalCpfCnpj').innerText = solicitacao.solicitanteCpfCnpj || 'Não informado';
    document.getElementById('modalDescricao').innerText = solicitacao.descricao || 'Sem descrição';

    const modal = document.getElementById('detailsModal');
    if (modal) {
        modal.style.display = 'flex';
    }
}

function fecharModalDetalhes() {
    const modal = document.getElementById('detailsModal');
    if (modal) {
        modal.style.display = 'none';
    }
}



// modal criação

function abrirModalCriacao() {
    const form = document.getElementById('formCriarSolicitacao');
    form?.reset();

    const modal = document.getElementById('createModal');
    if (modal) {
        modal.style.display = 'flex';
    }
}

function fecharModalCriacao() {
    const modal = document.getElementById('createModal');
    if (modal) {
        modal.style.display = 'none';
    }
}


// formualario solicitação
function configurarFormularioCriacao() {
    const form = document.getElementById('formCriarSolicitacao');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const dados = obterDadosFormulario();

        if (!validarFormulario(dados)) {
            return;
        }

        const solicitante = encontrarSolicitante(dados.solicitanteTexto);

        if (!solicitante) {
            alert('Solicitante não encontrado! Por favor, selecione uma opção válida da lista.');
            return;
        }

        const solicitacaoDTO = {
            solicitanteId: solicitante.id,
            categoriaId: parseInt(dados.categoriaId),
            descricao: dados.descricao.trim(),
            valor: parseFloat(dados.valor)
        };

        await enviarSolicitacao(solicitacaoDTO, form);
    });
}

function obterDadosFormulario() {
    return {
        valor: document.getElementById('createValor').value,
        categoriaId: document.getElementById('createCategoria').value,
        solicitanteTexto: document.getElementById('createSolicitanteInput').value,
        descricao: document.getElementById('createDescricao').value
    };
}

function validarFormulario(dados) {
    if (
        !dados.valor ||
        !dados.categoriaId ||
        !dados.solicitanteTexto.trim() ||
        !dados.descricao.trim()
    ) {
        alert('Por favor, preencha todos os campos obrigatórios!');
        return false;
    }

    return true;
}

function encontrarSolicitante(texto) {
    return listaSolicitantes.find(solicitante =>
        `${solicitante.nome} (${solicitante.cpfCnpj})` === texto ||
        solicitante.nome === texto
    );
}

async function enviarSolicitacao(solicitacaoDTO, form) {
    try {
        const resposta = await fetch(`${API_URL}/solicitacoes`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(solicitacaoDTO)
        });

        if (resposta.ok) {
            fecharModalCriacao();
            form.reset();
            carregarSolicitacoes();
            return;
        }

        const erroData = await resposta.json().catch(() => null);
        console.error('Resposta da API:', erroData);

        alert('Erro ao cadastrar solicitação. Verifique os dados inseridos.');
    } catch (erro) {
        console.error('Erro de conexão ao criar solicitação:', erro);
        alert('Erro de conexão com o servidor.');
    }
}


function configurarEventos() {

    // Modal de criação
    document.getElementById('btnAbrirCriar')?.addEventListener('click', abrirModalCriacao);
    document.getElementById('btnCancelarCriar')?.addEventListener('click', fecharModalCriacao);

    // Fechar modal
    const modalCriar = document.getElementById('createModal');
    if (modalCriar) {
        modalCriar.addEventListener('click', (e) => {
            if (e.target === modalCriar) {
                fecharModalCriacao();
            }
        });
    }
    // Filtros
    document.getElementById('btnFiltrar')?.addEventListener('click', carregarSolicitacoes);
    document.getElementById('btnLimpar')?.addEventListener('click', limparFiltros);

    // Modal de detalhes
    document.getElementById('btnCloseDetails')?.addEventListener('click', fecharModalDetalhes);
}


// load

document.addEventListener('DOMContentLoaded', () => {
    carregarCategorias();
    carregarSolicitantes();
    carregarSolicitacoes();
    configurarFormularioCriacao();
    configurarEventos();
});