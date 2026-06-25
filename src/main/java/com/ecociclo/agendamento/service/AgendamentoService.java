package com.ecociclo.agendamento.service;

import com.ecociclo.agendamento.model.Agendamento;
import com.ecociclo.agendamento.model.Doacao;
import com.ecociclo.agendamento.model.StatusAgendamento;
import com.ecociclo.agendamento.repository.AgendamentoRepository;
import com.ecociclo.pontoColeta.model.PontoColeta;
import com.ecociclo.pontoColeta.repository.PontoColetaRepository;
import com.ecociclo.usuario.model.TipoUsuario;
import com.ecociclo.usuario.model.Usuario;
import com.ecociclo.usuario.repository.UsuarioRepository;
import com.ecociclo.recompensa.service.RecompensaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PontoColetaRepository pontoColetaRepository;

    @Autowired
    private RecompensaService recompensaService;

    //CRUD
    //Criar agendamento
    public String criar(Agendamento agendamento) throws ExecutionException, InterruptedException {
        validar(agendamento);
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        agendamento.setDataCriacao(LocalDateTime.now().toString());
        agendamento.setPontosGerados(0);

        return agendamentoRepository.salvar(agendamento);
    }

    //Buscar
    public Agendamento buscarPorId(String id) throws ExecutionException, InterruptedException{
        return agendamentoRepository.buscarPorId(id);
    }

    //Listar todos os agendamentos
    public List<Agendamento> listarTodos() throws ExecutionException, InterruptedException{
        return agendamentoRepository.listarTodos();
    }

    //Listar por filtro
    public List<Agendamento> listarPorFiltros(String doadorId, String receptorId, StatusAgendamento status, String dataInicio, String dataFim) throws ExecutionException, InterruptedException{
        return agendamentoRepository.listarPorFiltros(doadorId, receptorId, status, dataInicio, dataFim);
    }

    //Deletar agendamento
    public void deletar(String id) throws ExecutionException, InterruptedException{
        Agendamento ag = agendamentoRepository.buscarPorId(id);

        if (ag == null) throw new IllegalArgumentException("Agendamento " + id + " não encontrado");
        validarTransicao(ag.getStatus(), StatusAgendamento.CANCELADO);
        ag.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.atualizar(id, ag);
    }



    //Validar a transição
    private void validarTransicao(StatusAgendamento atual, StatusAgendamento novo){
        if(atual == StatusAgendamento.CONCLUIDO || atual == StatusAgendamento.CANCELADO)
            throw new IllegalArgumentException("Status "+ atual + " é terminal. Nenhuma transicao permitida.");

        boolean valida = switch (atual){
            case PENDENTE -> novo == StatusAgendamento.CONFIRMADO || novo == StatusAgendamento.CANCELADO;
            case CONFIRMADO -> novo == StatusAgendamento.CONCLUIDO || novo == StatusAgendamento.CANCELADO;
            default -> false;
        };

        if (!valida)
            throw new IllegalArgumentException("Transicao invalida: " + atual + " -> " + novo);
    }

    //Transiciona o status de agendamento
    public void transicionarStatus(String id, StatusAgendamento novoStatus)
            throws ExecutionException, InterruptedException {

        if (novoStatus == null) {
            throw new IllegalArgumentException("novoStatus é obrigatório.");
        }

        Agendamento ag = agendamentoRepository.buscarPorId(id);

        if (ag == null) {
            throw new IllegalArgumentException(
                    "Agendamento " + id + " nao encontrado"
            );
        }

        // Valida se a transição é permitida
        validarTransicao(ag.getStatus(), novoStatus);

        // Atualiza o status
        ag.setStatus(novoStatus);

        // Se concluir o agendamento
        if (novoStatus == StatusAgendamento.CONCLUIDO) {


            int pontos = (ag.getDoacoes() == null ? 0 :
                    ag.getDoacoes()
                            .stream()
                            .mapToInt(Doacao::getQuantidade)
                            .sum()) * 10;

            // Salva quantos pontos o agendamento gerou
            ag.setPontosGerados(pontos);

            // Credita os pontos no usuário
            recompensaService.creditarPontos(
                    ag.getDoadorId(),
                    pontos,
                    id
            );
        }

        // Atualiza o agendamento no Firestore
        agendamentoRepository.atualizar(id, ag);
    }

    //Validar o agendamento
    private void validar(Agendamento ag) throws ExecutionException, InterruptedException{
        if(ag.getDoadorId() == null || ag.getDoadorId().isBlank())
            throw new IllegalArgumentException("Campo 'doadorId' é obrigatório.");

        if (ag.getDataHora() == null || ag.getDataHora().isBlank())
            throw new IllegalArgumentException("Campo 'dataHora' é obrigatório.");

        if (ag.getDoacoes() == null || ag.getDoacoes().isEmpty())
            throw new IllegalArgumentException("Campo 'doacoes' não pode ser vazio.");

        for (Doacao d : ag.getDoacoes()) {

            if (d == null)
                throw new IllegalArgumentException("Doação inválida.");

            if (d.getNome() == null || d.getNome().isBlank())
                throw new IllegalArgumentException("Doação com nome inválido.");

            if (d.getQuantidade() <= 0)
                throw new IllegalArgumentException("Quantidade deve ser maior que 0.");

            if (d.getFoto() == null || d.getFoto().isBlank())
                throw new IllegalArgumentException("Foto da doação é obrigatória.");
        }

        // Valida que a data não é no passado
        LocalDateTime dataHora = LocalDateTime.parse(ag.getDataHora());
        if (!dataHora.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("'dataHora' não pode ser no passado.");

        // Valida que doadorId aponta para um DOADOR — igual ao Eduardo valida RECEPTOR
        Usuario doador = usuarioRepository.buscarPorId(ag.getDoadorId());
        if (doador == null || doador.getTipo() != TipoUsuario.DOADOR)
            throw new IllegalArgumentException(
                    "doadorId '" + ag.getDoadorId() + "' não aponta para um usuário do tipo DOADOR.");

        //Valida ponto de coleta
        if (ag.getPontoColetaId() == null || ag.getPontoColetaId().isBlank())
            throw new IllegalArgumentException("Campo 'pontoColetaId' é obrigatório.");

        PontoColeta pc = pontoColetaRepository.buscarPorId(ag.getPontoColetaId());

        if (pc == null)
            throw new IllegalArgumentException(
                    "pontoColetaId '" + ag.getPontoColetaId() + "' não encontrado.");

        if (!pc.getAtivo())
            throw new IllegalArgumentException(
                    "Ponto de coleta está inativo.");
    }

    public void atualizar(String id, Agendamento novo)
            throws ExecutionException, InterruptedException {

        Agendamento existente = agendamentoRepository.buscarPorId(id);

        if (existente == null)
            throw new IllegalArgumentException("Agendamento não encontrado.");

        //CAMPOS QUE PODEM SER ATUALIZADOS
        existente.setDataHora(novo.getDataHora());
        existente.setDoacoes(novo.getDoacoes());
        existente.setObservacoes(novo.getObservacoes());
        existente.setPontoColetaId(novo.getPontoColetaId());

        // Revalida dados
        validar(existente);

        agendamentoRepository.atualizar(id, existente);
    }
}

