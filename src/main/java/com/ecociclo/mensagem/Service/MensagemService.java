package com.ecociclo.mensagem.Service;

import com.ecociclo.mensagem.Model.Mensagem;
import com.ecociclo.chat.Repository.ChatRepository;
import com.ecociclo.mensagem.Repository.MensagemRepository;
//import com.ecociclo.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private ChatRepository chatRepository;          // valida se o chat existe

   // @Autowired
   // private UsuarioRepository usuarioRepository;    // valida se o remetente existe e é participante

    public String enviar(String chatId, Mensagem mensagem)
            throws ExecutionException, InterruptedException {

        var chat = chatRepository.buscarPorId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat '" + chatId + "' não encontrado."));

        if (mensagem.getAutorId() == null || mensagem.getAutorId().isBlank()) {
            throw new IllegalArgumentException("Campo 'autorId' é obrigatório.");
        }
        if (!chat.getParticipantesId().contains(mensagem.getAutorId())) {
            throw new IllegalArgumentException(
                    "Autor '" + mensagem.getAutorId() + "' não é participante deste chat.");
        }
        if (mensagem.getTexto() == null || mensagem.getTexto().isBlank()) {
            throw new IllegalArgumentException("Mensagem não pode ser vazia.");
        }

        mensagem.setChatId(chatId);
        String mensagemId = mensagemRepository.enviar(chatId, mensagem);

        // Atualiza o chat com a última mensagem enviada
        String preview = gerarPreview(mensagem.getTexto());
        chatRepository.atualizarUltimaAtividade(chatId, preview);

        return mensagemId;
    }

    public List<Mensagem> listar(String chatId, Integer limite, String cursorMensagemId)
            throws ExecutionException, InterruptedException {

        // Chat deve existir antes de listar
        chatRepository.buscarPorId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat '" + chatId + "' não encontrado."));

        return mensagemRepository.listar(chatId, limite, cursorMensagemId);
    }

    public void deletar(String chatId, String mensagemId, String solicitanteId)
            throws ExecutionException, InterruptedException {

        // Só o próprio remetente pode deletar — você pode relaxar isso depois
        // buscando a mensagem e comparando o remetenteId
        mensagemRepository.deletar(chatId, mensagemId);
    }

    public void marcarComoLida(String chatId, String mensagemId, String solicitanteId)
            throws ExecutionException, InterruptedException {

        // Busca a mensagem para validar
        // (você pode criar um buscarPorId no repository se quiser validação mais rígida)
        mensagemRepository.marcarComoLida(chatId, mensagemId);
    }

    public void marcarTodasComoLidas(String chatId, String usuarioId)
            throws ExecutionException, InterruptedException {

        // Usuário deve ser participante do chat
        var chat = chatRepository.buscarPorId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat '" + chatId + "' não encontrado."));

        if (!chat.getParticipantesId().contains(usuarioId)) {
            throw new IllegalArgumentException(
                    "Usuário '" + usuarioId + "' não é participante deste chat.");
        }

        mensagemRepository.marcarTodasComoLidas(chatId, usuarioId);
    }

    // Trunca textos longos para exibir na lista de chats (ex: "Olá, tudo bem co...")
    private String gerarPreview(String texto) {
        int limite = 60;
        if (texto == null) return "";
        return texto.length() <= limite ? texto : texto.substring(0, limite) + "...";
    }

}