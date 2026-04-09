package com.ecociclo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

// Classe de configuração responsável por inicializar a conexão com o Firebase
// É executada automaticamente pelo Spring ao iniciar a aplicação
@Configuration
public class FirebaseConfig {

    // @PostConstruct garante que este método execute assim que o bean for criado
    // Inicializa o Firebase usando o arquivo de credenciais JSON (chave de serviço)
    @PostConstruct
    public void initFirebase() throws IOException {
        // Verifica se já existe uma instância do Firebase para evitar inicialização duplicada
        if (FirebaseApp.getApps().isEmpty()) {
            // Lê o arquivo de credenciais do Firebase (não vai para o Git por segurança)
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/firebase-service-account.json");

            // Configura as opções do Firebase com as credenciais de autenticação
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    // Cria o bean do Firestore para ser injetado nos repositórios via @Autowired
    // Permite que qualquer classe do projeto acesse o banco de dados Firestore
    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
