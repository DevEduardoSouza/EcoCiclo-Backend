package com.ecociclo.config;

import com.ecociclo.model.TipoUsuario;
import com.ecociclo.model.Usuario;
import com.ecociclo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public DataSeeder(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!usuarioRepository.listarTodos().isEmpty()) {
            System.out.println("[Seeder] Coleção 'usuarios' já possui dados — nada a inserir.");
            return;
        }

        System.out.println("[Seeder] Inserindo usuários de teste...");

        Usuario admin = new Usuario(null, "IFBA Administrador", "admin@ifba.edu.br",
                "(74) 0000-0000", TipoUsuario.ADMIN, null, 0);
        usuarioRepository.salvar(admin);

        Usuario assoc1 = new Usuario(null, "Cooperativa CooperLimpa", "contato@cooperlimpa.org",
                "(74) 1111-1111", TipoUsuario.ASSOCIACAO, null, 0);
        String assoc1Id = usuarioRepository.salvar(assoc1);

        Usuario assoc2 = new Usuario(null, "Associação Recicla Vale", "contato@reciclavale.org",
                "(74) 2222-2222", TipoUsuario.ASSOCIACAO, null, 0);
        String assoc2Id = usuarioRepository.salvar(assoc2);

        Usuario doador1 = new Usuario(null, "Maria Silva", "maria@email.com",
                "(74) 9000-0001", TipoUsuario.DOADOR, null, 120);
        usuarioRepository.salvar(doador1);

        Usuario doador2 = new Usuario(null, "João Souza", "joao@email.com",
                "(74) 9000-0002", TipoUsuario.DOADOR, null, 50);
        usuarioRepository.salvar(doador2);

        Usuario receptor1 = new Usuario(null, "Pedro Coletor", "pedro@cooperlimpa.org",
                "(74) 9111-0001", TipoUsuario.RECEPTOR, assoc1Id, 0);
        usuarioRepository.salvar(receptor1);

        Usuario receptor2 = new Usuario(null, "Ana Coletora", "ana@cooperlimpa.org",
                "(74) 9111-0002", TipoUsuario.RECEPTOR, assoc1Id, 0);
        usuarioRepository.salvar(receptor2);

        Usuario receptor3 = new Usuario(null, "Carlos Coletor", "carlos@reciclavale.org",
                "(74) 9222-0001", TipoUsuario.RECEPTOR, assoc2Id, 0);
        usuarioRepository.salvar(receptor3);

        System.out.println("[Seeder] 8 usuários inseridos (1 admin, 2 associações, 2 doadores, 3 receptores).");
    }
}
