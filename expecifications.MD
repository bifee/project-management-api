# Project Management API - Especificações

## 📋 Visão Geral

Uma **API REST** que permite que **equipes gerenciem projetos e tarefas**, similar ao **Jira** ou **Trello**.

---

## 👥 Entidades Principais

### 1. **User (Usuário)**

```
Campos:
├── id (Long) - Identificador único
├── email (String) - Email único para login
├── password (String) - Senha criptografada
├── name (String) - Nome do usuário
├── role (Enum) - ADMIN, PROJECT_MANAGER, DEV, VIEWER
├── createdAt (LocalDateTime) - Quando foi criado
└── isActive (Boolean) - Ativo ou inativo
```

**Relacionamentos:**
- Pode ter múltiplos **Projetos** (OneToMany)
- Pode ter múltiplas **Tarefas** (OneToMany)

---

### 2. **Project (Projeto)**

```
Campos:
├── id (Long) - Identificador único
├── name (String) - Nome do projeto
├── description (String) - Descrição
├── status (Enum) - BACKLOG, IN_PROGRESS, COMPLETED, CANCELLED
├── ownerId (Long) - Quem criou (referência User)
├── createdAt (LocalDateTime)
├── updatedAt (LocalDateTime)
└── members (List<User>) - Membros do projeto
```

**Relacionamentos:**
- Pertence a um **User** (OneToMany)
- Tem múltiplas **Tarefas** (OneToMany)
- Tem múltiplos **Members** (ManyToMany)

---

### 3. **Task (Tarefa)**

```
Campos:
├── id (Long) - Identificador único
├── title (String) - Título da tarefa
├── description (String) - Descrição
├── status (Enum) - TO_DO, IN_PROGRESS, REVIEW, DONE
├── priority (Enum) - LOW, MEDIUM, HIGH, CRITICAL
├── assigneeId (Long) - Quem está fazendo (User)
├── projectId (Long) - Em qual projeto está (Project)
├── dueDate (LocalDate) - Data de vencimento
├── createdAt (LocalDateTime)
├── updatedAt (LocalDateTime)
└── comments (List<Comment>) - Comentários na tarefa
```

**Relacionamentos:**
- Pertence a um **Project** (ManyToOne)
- Atribuída a um **User** (ManyToOne)
- Tem múltiplos **Comments** (OneToMany)

---

### 4. **Comment (Comentário)**

```
Campos:
├── id (Long) - Identificador único
├── content (String) - Conteúdo do comentário
├── authorId (Long) - Quem comentou (User)
├── taskId (Long) - Em qual tarefa (Task)
├── createdAt (LocalDateTime)
└── updatedAt (LocalDateTime)
```

**Relacionamentos:**
- Pertence a uma **Task** (ManyToOne)
- Pertence a um **User** (ManyToOne)

---

## 🔐 Sistema de Permissões

```
ADMIN
├── Pode criar/editar/deletar qualquer projeto
├── Pode gerenciar usuários
└── Acesso total

PROJECT_MANAGER
├── Pode criar projetos
├── Pode gerenciar membros do seu projeto
├── Pode ver projetos que participa
└── Pode deletar suas próprias tarefas

DEV
├── Pode ver projetos que participa
├── Pode criar/editar suas próprias tarefas
└── Pode comentar em tarefas

VIEWER
├── Pode apenas visualizar
└── Sem permissão de escrita
```

---

## 📡 Endpoints da API

### **Authentication**
```
POST   /api/auth/register       - Registrar novo usuário
POST   /api/auth/login          - Fazer login (retorna JWT)
POST   /api/auth/refresh        - Renovar token
POST   /api/auth/logout         - Logout
```

### **Usuários**
```
GET    /api/users               - Listar todos (ADMIN)
GET    /api/users/{id}          - Detalhes do usuário
PUT    /api/users/{id}          - Atualizar perfil
DELETE /api/users/{id}          - Deletar (ADMIN)
```

### **Projetos**
```
GET    /api/projects            - Listar meus projetos
POST   /api/projects            - Criar projeto
GET    /api/projects/{id}       - Detalhes do projeto
PUT    /api/projects/{id}       - Atualizar projeto
DELETE /api/projects/{id}       - Deletar projeto
POST   /api/projects/{id}/members/{userId}  - Adicionar membro
DELETE /api/projects/{id}/members/{userId}  - Remover membro
```

### **Tarefas**
```
GET    /api/projects/{projectId}/tasks      - Listar tarefas do projeto
POST   /api/projects/{projectId}/tasks      - Criar tarefa
GET    /api/tasks/{id}                      - Detalhes da tarefa
PUT    /api/tasks/{id}                      - Atualizar tarefa
DELETE /api/tasks/{id}                      - Deletar tarefa
PATCH  /api/tasks/{id}/status               - Mudar status
```

### **Comentários**
```
GET    /api/tasks/{taskId}/comments         - Listar comentários
POST   /api/tasks/{taskId}/comments         - Criar comentário
DELETE /api/comments/{id}                   - Deletar comentário
```

---

## 🏗️ Arquitetura em Camadas

```
┌─────────────────────────────────────────┐
│         REST Controller                 │
│  (HTTP Request/Response + Validação)   │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Service (Lógica)                │
│  (Regras de negócio + Segurança)       │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Repository (JPA)                │
│  (Acesso a dados)                       │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│      Entity (Modelo de Dados)           │
│  (Representação do banco)               │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│      PostgreSQL Database                │
│  (Tabelas, Constraints)                 │
└─────────────────────────────────────────┘
```

---

## 🔄 Fluxo de uma Requisição

**Exemplo: Criar uma Tarefa**

```
1. Cliente envia:
   POST /api/projects/1/tasks
   Authorization: Bearer <JWT_TOKEN>
   {
     "title": "Implementar login",
     "priority": "HIGH"
   }

2. Spring Security valida o JWT
   ├─ Token é válido?  
   ├─ Usuário tem permissão?
   └─ Se não, retorna 401/403

3. Controller recebe a requisição
   ├─ Valida dados com @Valid
   └─ Chama service.createTask()

4. Service executa lógica
   ├─ Verifica se projeto existe
   ├─ Verifica se usuário é membro
   ├─ Verifica se tem permissão
   └─ Cria tarefa no banco

5. Repository salva no banco
   ├─ INSERT INTO tasks...  
   └─ Retorna tarefa criada

6. Controller retorna resposta
   {
     "id": 123,
     "title": "Implementar login",
     "status": "TO_DO",
     ...  
   }
```

---

## 📊 Banco de Dados - Tabelas

```sql
users
├── id (PK)
├── email (UNIQUE)
├── password
├── name
├── role
├── createdAt
└── isActive

projects
├── id (PK)
├── name
├── description
├── status
├── owner_id (FK → users)
├── createdAt
└── updatedAt

project_members (ManyToMany)
├── project_id (FK → projects)
├── user_id (FK → users)
└── PK (project_id, user_id)

tasks
├── id (PK)
├── title
├── description
├── status
├── priority
├── assignee_id (FK → users)
├── project_id (FK → projects)
├── due_date
├── createdAt
└── updatedAt

comments
├── id (PK)
├── content
├── author_id (FK → users)
├── task_id (FK → tasks)
├── createdAt
└── updatedAt
```

---

## 🎯 Tecnologias por Camada

```
Frontend (não é escopo desse projeto)
    ↓ HTTP
─────────────────────────────────────────
Camada Web
├─ Spring Boot (Framework principal)
├─ Spring MVC (Controllers, HTTP)
└─ Spring Security (Autenticação, Autorização)

Camada de Negócio
├─ Services (Lógica)
└─ DTOs (Data Transfer Objects)

Camada de Persistência
├─ Spring Data JPA (ORM)
├─ Hibernate (Implementação JPA)
└─ PostgreSQL (Banco de dados)

Utilities
├─ Nimbus JWT (Tokens)
├─ Validation (Validações)
└─ Lombok (Geração de código)
─────────────────────────────────────────
    ↓ SQL
PostgreSQL Database
```

---

## 🚀 Roadmap de Implementação

**Fase 1: Autenticação**
- User entity
- JWT Token Provider
- Auth Controller

**Fase 2: CRUD de Projetos**
- Project entity
- ProjectRepository
- ProjectService
- ProjectController

**Fase 3: CRUD de Tarefas**
- Task entity
- TaskRepository
- TaskService
- TaskController

**Fase 4: Comentários**
- Comment entity
- CommentRepository
- CommentService
- CommentController

**Fase 5: Testes**
- Testes unitários dos services
- Testes de integração dos controllers

**Fase 6: Melhorias**
- WebSocket para notificações real-time
- Paginação em listagens
- Filtros avançados
- Documentação Swagger/OpenAPI

---

## 📌 Informações do Projeto

- **Linguagem:** Java 17+
- **Framework Principal:** Spring Boot 4.0.0
- **Banco de Dados:** PostgreSQL
- **Autenticação:** JWT (Nimbus JOSE+JWT)
- **ORM:** Spring Data JPA + Hibernate
- **Build:** Maven
- **IDE Recomendada:** IntelliJ IDEA ou Eclipse

---

**Versão:** 1.0.0  
**Última Atualização:** Dezembro 2025