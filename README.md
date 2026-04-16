# ♻️ EcoCiclo - Backend

Aplicativo de **Coleta Seletiva de Resíduos Sólidos** que conecta doadores de resíduos a cooperativas e pontos de coleta, incentivando a reciclagem e a educação ambiental.

---

## Sobre o Projeto

O EcoCiclo facilita e incentiva a reciclagem, otimizando a logística da coleta seletiva. O sistema conta com um app móvel (Android/iOS) e um painel web administrativo.

## Funcionalidades

- **Cadastro de Usuários** — cidadãos (doadores) e cooperativas (coletores)
- **Pontos de Coleta** — cadastro e localização de pontos para descarte
- **Agendamento de Coleta** — agendamento com data, local e tipo de resíduo
- **Guia de Reciclagem** — orientações visuais de descarte correto
- **Sistema de Recompensas** — pontuação por reciclagem concluída, trocável por recompensas
- **Chat Doador/Coletor** — comunicação direta durante o processo de coleta
- **Perfil do Usuário** — informações pessoais, pontuação e histórico

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Backend / API | Java 17 + Spring Boot |
| Banco de Dados | Cloud Firestore |
| Autenticação | Firebase Authentication |
| Notificações | Firebase Cloud Messaging |
| Chat em Tempo Real | Cloud Firestore Realtime |
| Armazenamento | Firebase Storage |
| Frontend Web | *A definir* |
| Mapeamento | Google Maps Platform |

## Estrutura do Projeto

```
src/main/java/com/ecociclo/
├── EcoCicloApplication.java          # Classe principal
├── config/
│   └── FirebaseConfig.java           # Configuração do Firebase
├── controller/
│   ├── UsuarioController.java
│   ├── PontoColetaController.java
│   ├── AgendamentoController.java
│   └── RecompensaController.java
├── model/
│   ├── Usuario.java
│   ├── PontoColeta.java
│   ├── Agendamento.java
│   └── Recompensa.java
├── service/
│   ├── UsuarioService.java
│   ├── PontoColetaService.java
│   ├── AgendamentoService.java
│   └── RecompensaService.java
└── repository/
    ├── UsuarioRepository.java
    ├── PontoColetaRepository.java
    ├── AgendamentoRepository.java
    └── RecompensaRepository.java
```

## Endpoints da API

Base URL: `http://localhost:8080`

### Usuários (`/api/usuarios`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/usuarios` | Listar todos os usuários |
| `GET` | `/api/usuarios/{id}` | Buscar usuário por ID |
| `POST` | `/api/usuarios` | Criar novo usuário |
| `PUT` | `/api/usuarios/{id}` | Atualizar usuário |
| `DELETE` | `/api/usuarios/{id}` | Deletar usuário |

### Pontos de Coleta (`/api/pontos-coleta`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/pontos-coleta` | Listar todos os pontos |
| `GET` | `/api/pontos-coleta/{id}` | Buscar ponto por ID |
| `POST` | `/api/pontos-coleta` | Criar novo ponto de coleta |
| `PUT` | `/api/pontos-coleta/{id}` | Atualizar ponto |
| `DELETE` | `/api/pontos-coleta/{id}` | Deletar ponto |

### Agendamentos (`/api/agendamentos`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/agendamentos` | Listar todos os agendamentos |
| `GET` | `/api/agendamentos/{id}` | Buscar agendamento por ID |
| `POST` | `/api/agendamentos` | Criar novo agendamento |
| `PUT` | `/api/agendamentos/{id}` | Atualizar agendamento |
| `DELETE` | `/api/agendamentos/{id}` | Deletar agendamento |

### Recompensas (`/api/recompensas`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/recompensas` | Listar todas as recompensas |
| `GET` | `/api/recompensas/{id}` | Buscar recompensa por ID |
| `POST` | `/api/recompensas` | Criar nova recompensa |
| `PUT` | `/api/recompensas/{id}` | Atualizar recompensa |
| `DELETE` | `/api/recompensas/{id}` | Deletar recompensa |

### Exemplos de JSON

**Usuário:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "(71) 99999-0000",
  "tipo": "doador",
  "pontuacao": 0
}
```

**Ponto de Coleta:**
```json
{
  "nome": "Ecoponto Centro",
  "endereco": "Rua Principal, 100",
  "latitude": -12.9714,
  "longitude": -38.5124,
  "tiposResiduos": ["plástico", "vidro", "metal"],
  "horarioFuncionamento": "08:00 - 17:00"
}
```

**Agendamento:**
```json
{
  "usuarioId": "abc123",
  "pontoColetaId": "xyz789",
  "dataHora": "2026-04-15T10:00:00",
  "tipoResiduo": "plástico",
  "status": "pendente"
}
```

**Recompensa:**
```json
{
  "descricao": "Desconto 10% na loja parceira",
  "pontosNecessarios": 100,
  "tipo": "desconto",
  "ativa": true
}
```

## Como Rodar

### Opção 1 — Docker (recomendado)

Essa é a forma mais fácil. Você **não precisa** instalar Java nem Maven na sua máquina — só o Docker.

**Pré-requisitos:**
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e aberto

**Passo a passo:**

1. Clone o repositório:
   ```bash
   git clone https://github.com/DevEduardoSouza/EcoCiclo-Backend.git
   cd EcoCiclo-Backend
   ```

2. Coloque o arquivo `firebase-service-account.json` em `src/main/resources/`
   *(peça esse arquivo para algum membro da equipe — ele não vai para o Git por segurança)*

3. Suba o container:
   ```bash
   docker compose up --build
   ```

4. Pronto! A API estará rodando em `http://localhost:8080`

**Comandos úteis:**
```bash
docker compose up              # rodar (primeira vez pode demorar alguns minutos)
docker compose up -d           # rodar em background
docker compose down            # parar
docker compose up --build      # rebuildar depois de mudar código Java
docker compose logs -f         # acompanhar logs
```

### Opção 2 — Local (Java + Maven)

Se preferir rodar direto na máquina:

1. Instale **Java 17** e **Maven**
2. Coloque o arquivo `firebase-service-account.json` em `src/main/resources/`
3. Execute:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
4. Acesse: `http://localhost:8080/api/usuarios`

## Equipe

| Membro | GitHub |
|---|---|
| Eduardo Souza | [@DevEduardoSouza](https://github.com/DevEduardoSouza) |
| Ricley Neiva | [@RicleyNeiva01](https://github.com/RicleyNeiva01) |
| Luis Filipe | [@LuisFilipe-ifba](https://github.com/LuisFilipe-ifba) |
| Dudas | [@Dudasss](https://github.com/Dudasss) |
| Jasmin | [@Jasmin1209](https://github.com/Jasmin1209) |

**Orientador:** Prof. Ronilson

## Licença

Este projeto é desenvolvido para fins acadêmicos.
