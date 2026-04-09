package com.ecociclo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuração que registra o filtro de autenticação Firebase
// Define quais rotas exigem token de autenticação
@Configuration
public class FilterConfig {

    // TODO: Descomentar o @Bean quando o frontend estiver pronto para ativar a autenticação Firebase
    // @Bean
    public FilterRegistrationBean<FirebaseAuthFilter> firebaseAuthFilter() {
        FilterRegistrationBean<FirebaseAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FirebaseAuthFilter());
        // Protege todas as rotas da API
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
