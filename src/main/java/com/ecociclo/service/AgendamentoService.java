package com.ecociclo.service;

import com.ecociclo.model.Agendamento;
import com.ecociclo.model.StatusAgendamento;
import com.ecociclo.model.TipoUsuario;
import com.ecociclo.model.Usuario;
import com.ecociclo.repository.AgendamentoRepository;
import com.ecociclo.repository.UsuarioRepository;
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

    // @Autowired
    // private PontoColetaRepository pontoColetaRepository;

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
    public void transicionarStatus(String id, StatusAgendamento novoStatus) throws ExecutionException, InterruptedException{

        if (novoStatus == null) {
            throw new IllegalArgumentException("novoStatus é obrigatório.");
        }

        Agendamento ag = agendamentoRepository.buscarPorId(id);

        if (ag == null){
            throw new IllegalArgumentException(
                    "Agendamento " + id + " nao encontrado"
            );
        }

        // Valida se a transição é permitida
        validarTransicao(ag.getStatus(), novoStatus);

        // Atualiza o status
        ag.setStatus(novoStatus);

        if (novoStatus == StatusAgendamento.CONCLUIDO) {

            // Regra simples: cada material vale 10 pontos
            int pontos = ag.getMateriais().size() * 10;

            ag.setPontosGerados(pontos);

            Usuario usuario = usuarioRepository.buscarPorId(ag.getDoadorId());
            if (usuario == null)
                throw new IllegalArgumentException(
                        "Usuário doador '" + ag.getDoadorId() + "' não encontrado ao concluir agendamento.");

            usuario.setPontuacao(usuario.getPontuacao() + pontos);
            usuarioRepository.atualizar(usuario.getId(), usuario);
            // recompensaService.creditarPontos(ag.getDoadorId(), pontos, id);
        }

        // Atualiza agendamento no Firestore
        agendamentoRepository.atualizar(id, ag);
    }

    //Validar o agendamento
    private void validar(Agendamento ag) throws ExecutionException, InterruptedException{
        if(ag.getDoadorId() == null || ag.getDoadorId().isBlank())
            throw new IllegalArgumentException("Campo 'doadorId' é obrigatório.");

        if (ag.getDataHora() == null || ag.getDataHora().isBlank())
            throw new IllegalArgumentException("Campo 'dataHora' é obrigatório.");

        if (ag.getMateriais() == null || ag.getMateriais().isEmpty())
            throw new IllegalArgumentException("Campo 'materiais' não pode ser vazio.");

        // Valida que a data não é no passado
        LocalDateTime dataHora = LocalDateTime.parse(ag.getDataHora());
        if (!dataHora.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("'dataHora' não pode ser no passado.");

        // Valida que doadorId aponta para um DOADOR — igual ao Eduardo valida RECEPTOR
        Usuario doador = usuarioRepository.buscarPorId(ag.getDoadorId());
        if (doador == null || doador.getTipo() != TipoUsuario.DOADOR)
            throw new IllegalArgumentException(
                    "doadorId '" + ag.getDoadorId() + "' não aponta para um usuário do tipo DOADOR.");

        // if (ag.getPontoColetaId() != null && !ag.getPontoColetaId().isBlank()) {
        //     PontoColeta pc = pontoColetaRepository.buscarPorId(ag.getPontoColetaId());
        //     if (pc == null || !pc.isAtivo())
        //         throw new IllegalArgumentException(
        //                 "pontoColetaId '" + ag.getPontoColetaId() + "' não existe ou está inativo.");
        // }
    }
}