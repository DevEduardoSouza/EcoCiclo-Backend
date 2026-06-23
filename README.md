# EcoCiclo - Backend

Backend do EcoCiclo, uma API para conectar doadores de residuos, associacoes/cooperativas, coletores, pontos de coleta, agendamentos, recompensas, chat e avaliacoes.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| API | Java 17 + Spring Boot |
| Banco de dados | Cloud Firestore |
| Autenticacao | Firebase Authentication |
| Build | Maven |
| Container | Docker |

## Estrutura do Projeto

O projeto esta organizado por dominio:

```text
src/main/java/com/ecociclo/
+-- agendamento/
|   +-- controller/
|   +-- model/
|   +-- repository/
|   +-- service/
+-- associacao/
|   +-- controller/
|   +-- model/
|   +-- repository/
|   +-- service/
+-- avaliacao/
|   +-- controller/
|   +-- model/
|   +-- repository/
|   +-- service/
+-- chat/
|   +-- Controller/
|   +-- Model/
|   +-- Repository/
|   +-- Service/
+-- config/
+-- mensagem/
|   +-- Controller/
|   +-- Model/
|   +-- Repository/
|   +-- Service/
+-- pontoColeta/
|   +-- controller/
|   +-- model/
|   +-- repository/
|   +-- service/
+-- recompensa/
|   +-- controller/
|   +-- model/
|   +-- repository/
|   +-- service/
+-- resgate/
|   +-- model/
|   +-- repository/
+-- usuario/
    +-- controller/
    +-- model/
    +-- repository/
    +-- service/
```

## Perfis de Usuario

| Perfil | Descricao |
|---|---|
| `ADMIN` | Administrador geral |
| `ASSOCIACAO` | Associacao ou cooperativa parceira |
| `RECEPTOR` | Coletor vinculado a uma associacao |
| `DOADOR` | Usuario que doa residuos |

Para usuarios do tipo `RECEPTOR`, o campo `associacaoId` e obrigatorio.

## Endpoints da API

Base URL: `http://localhost:8080`

### Usuarios (`/api/usuarios`)

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/api/usuarios` | Criar usuario |
| `GET` | `/api/usuarios` | Listar usuarios. Aceita filtros opcionais `tipo` e `associacaoId` |
| `GET` | `/api/usuarios/me` | Buscar usuario vinculado ao token Firebase enviado no header `Authorization` |
| `GET` | `/api/usuarios/{id}` | Buscar usuario por ID |
| `PUT` | `/api/usuarios/{id}` | Atualizar usuario |
| `DELETE` | `/api/usuarios/{id}` | Deletar usuario |

### Pontos de Coleta (`/api/pontos-coleta`)

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/api/pontos-coleta` | Criar ponto de coleta |
| `GET` | `/api/pontos-coleta` | Listar pontos de coleta |
| `GET` | `/api/pontos-coleta/{id}` | Buscar ponto de coleta por ID |
| `PUT` | `/api/pontos-coleta/{id}` | Atualizar ponto de coleta |
| `DELETE` | `/api/pontos-coleta/{id}` | Deletar ponto de coleta |
| `PUT` | `/api/pontos-coleta/{id}/ativar` | Ativar ponto de coleta |
| `PUT` | `/api/pontos-coleta/{id}/desativar` | Desativar ponto de coleta |

### Agendamentos (`/api/agendamentos`)

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/api/agendamentos` | Criar agendamento |
| `GET` | `/api/agendamentos` | Listar agendamentos. Aceita filtros opcionais `doadorId`, `receptorId`, `status`, `dataInicio` e `dataFim` |
| `GET` | `/api/agendamentos/{id}` | Buscar agendamento por ID |
| `PUT` | `/api/agendamentos/{id}/status` | Atualizar status do agendamento |
| `DELETE` | `/api/agendamentos/{id}` | Cancelar agendamento |

Status aceitos: `PENDENTE`, `CONFIRMADO`, `CONCLUIDO`, `CANCELADO`.

### Recompensas (`/api/recompensas`)

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/api/recompensas` | Criar recompensa |
| `GET` | `/api/recompensas` | Listar recompensas. Use `?disponivel=true` para listar apenas disponiveis |
| `GET` | `/api/recompensas/{id}` | Buscar recompensa por ID |
| `PUT` | `/api/recompensas/{id}` | Atualizar recompensa |
| `DELETE` | `/api/recompensas/{id}` | Desativar recompensa com soft delete (`disponivel=false`) |
| `POST` | `/api/recompensas/{id}/resgatar` | Resgatar recompensa, bloquear estoque e criar resgate pendente |
| `POST` | `/api/recompensas/resgates/{resgateId}/confirmar-entrega` | Confirmar entrega, baixar estoque e marcar resgate como `ENTREGUE` |
| `POST` | `/api/recompensas/resgates/{resgateId}/estornar` | Estornar resgate pendente, desbloquear estoque e devolver pontos ao usuario |
| `GET` | `/api/recompensas/usuarios/{id}/resgates` | Historico de resgates do usuario |

Observacao: o resgate usa reserva de estoque. Ao resgatar, o campo `bloqueados` aumenta. O estoque real so diminui quando a entrega e confirmada pelo admin.

### Avaliacoes (`/api/avaliacoes`)

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/api/avaliacoes` | Criar avaliacao |
| `GET` | `/api/avaliacoes` | Listar avaliacoes |
| `GET` | `/api/avaliacoes/{id}` | Buscar avaliacao por ID |
| `PUT` | `/api/avaliacoes/{id}` | Atualizar avaliacao |
| `DELETE` | `/api/avaliacoes/{id}` | Deletar avaliacao |

### Associacoes (`/api/associacoes`)

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/api/associacoes` | Criar associacao |
| `GET` | `/api/associacoes` | Listar associacoes |
| `GET` | `/api/associacoes/{id}` | Buscar associacao por ID |
| `PUT` | `/api/associacoes/{id}` | Atualizar associacao |
| `PATCH` | `/api/associacoes/{id}/status` | Atualizar status da associacao |
| `DELETE` | `/api/associacoes/{id}` | Deletar associacao |

Status aceitos: `ok`, `negado`, `pendente`.

### Chats (`/api/chats`)

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/api/chats` | Criar chat ou retornar chat existente com os mesmos participantes |
| `GET` | `/api/chats/{chatId}` | Buscar chat por ID |
| `GET` | `/api/chats/usuario/{usuarioId}` | Listar chats de um usuario |

### Mensagens (`/api/chats/{chatId}/mensagens`)

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/api/chats/{chatId}/mensagens` | Enviar mensagem |
| `GET` | `/api/chats/{chatId}/mensagens` | Listar mensagens. Aceita `limite` e `cursor` opcionais |
| `DELETE` | `/api/chats/{chatId}/mensagens/{mensagemId}` | Deletar mensagem |
| `PATCH` | `/api/chats/{chatId}/mensagens/{mensagemId}/lida` | Marcar mensagem como lida |
| `PATCH` | `/api/chats/{chatId}/mensagens/lidas?usuarioId={usuarioId}` | Marcar mensagens do chat como lidas para o usuario |

## Exemplos de JSON

### Usuario

```json
{
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "telefone": "(74) 9000-0001",
  "tipo": "DOADOR",
  "associacaoId": null,
  "firebaseUid": null,
  "pontuacao": 120
}
```

### Ponto de Coleta

```json
{
  "nome": "Ecoponto Centro",
  "responsavel": "Ana Souza",
  "telefone": "(74) 99999-0000",
  "email": "ecoponto@email.com",
  "endereco": {
    "logradouro": "Rua Principal, 100",
    "bairro": "Centro",
    "cidade": "Juazeiro",
    "estado": "BA",
    "cep": "48900-000"
  }
}
```

### Agendamento

```json
{
  "doadorId": "usuario-doador-id",
  "pontoColetaId": "ponto-coleta-id",
  "receptorId": "usuario-receptor-id",
  "dataHora": "2026-07-15T10:00:00",
  "doacoes": [
    {
      "id": "doacao-1",
      "nome": "Plastico",
      "quantidade": 3,
      "foto": "https://exemplo.com/foto.jpg"
    }
  ],
  "observacoes": "Separado em sacolas limpas"
}
```

### Recompensa

```json
{
  "nome": "Desconto 10%",
  "descricao": "Desconto na loja parceira",
  "custoPontos": 100,
  "parceiro": "Loja Verde",
  "disponivel": true,
  "estoque": 20,
  "bloqueados": 0,
  "imagemUrl": "https://exemplo.com/recompensa.png"
}
```

### Resgatar Recompensa

```json
{
  "usuarioId": "usuario-doador-id"
}
```

### Avaliacao

```json
{
  "tipo": "coleta",
  "coletor": "usuario-receptor-id",
  "data": "2026-07-15T12:00:00",
  "comentario": "Coleta realizada no horario combinado."
}
```

### Associacao

```json
{
  "nomeAssociacao": "Cooperativa CooperLimpa",
  "status": "pendente"
}
```

### Atualizar Status da Associacao

```json
{
  "status": "ok"
}
```

### Chat

```json
{
  "participantesId": ["usuario-doador-id", "usuario-receptor-id"],
  "agendamentoId": "agendamento-id"
}
```

### Mensagem

```json
{
  "autorId": "usuario-doador-id",
  "texto": "Ola, posso entregar hoje?",
  "tipo": "TEXTO"
}
```

## Como Rodar

### Docker

1. Coloque o arquivo `firebase-service-account.json` em `src/main/resources/`.
2. Execute:

```bash
docker compose up --build
```

A API ficara disponivel em `http://localhost:8080`.

### Local

1. Instale Java 17 e Maven.
2. Coloque o arquivo `firebase-service-account.json` em `src/main/resources/`.
3. Execute:

```bash
mvn spring-boot:run
```

## Seeder de Usuarios de Teste

O `DataSeeder` insere usuarios de teste quando a colecao `usuarios` esta vazia.

Para resetar e recriar os usuarios de teste:

```bash
mvn spring-boot:run -Decociclo.seed.reset=true
```

Ou por variavel de ambiente:

```bash
ECOCICLO_SEED_RESET=true mvn spring-boot:run
```

## Equipe

| Membro | GitHub |
|---|---|
| Eduardo Souza | [@DevEduardoSouza](https://github.com/DevEduardoSouza) |
| Ricley Neiva | [@RicleyNeiva01](https://github.com/RicleyNeiva01) |
| Luis Filipe | [@LuisFilipe-ifba](https://github.com/LuisFilipe-ifba) |
| Dudas | [@Dudasss](https://github.com/Dudasss) |
| Jasmin | [@Jasmin1209](https://github.com/Jasmin1209) |

Orientador: Prof. Ronilson
