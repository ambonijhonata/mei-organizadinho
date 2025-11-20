# MEI Organizadinho

## Visão Geral
Sistema de gestão para MEI (Microempreendedor Individual) desenvolvido com Spring Boot, seguindo uma arquitetura em camadas (Layered Architecture) com separação clara de responsabilidades.

## Stack Tecnológica
- **Framework**: Spring Boot 3.5.7
- **Linguagem**: Java 25
- **Banco de Dados**: PostgreSQL
- **Segurança**: Spring Security + JWT (Auth0)
- **ORM**: Spring Data JPA
- **Migrations**: Flyway
- **Build**: Maven

## Estrutura de Camadas

### 1. **Controller Layer** (`controller/`)
Camada de apresentação responsável por:
- Expor endpoints REST API
- Validar requisições HTTP
- Mapear requisições para serviços
- Retornar respostas adequadas

**Controllers disponíveis:**
- `UserController` - Gerenciamento de usuários
- `ClientController` - Gerenciamento de clientes
- `AppointmentController` - Gerenciamento de agendamentos
- `ServicesController` - Gerenciamento de serviços oferecidos
- `ReportController` - Geração de relatórios

### 2. **Service Layer** (`service/`)
Camada de negócio que contém:
- Regras de negócio
- Lógica de processamento
- Orquestração de operações

**Serviços principais:**
- `UserService` - Lógica de usuários
- `ClientService` - Lógica de clientes
- `AppointmentService` - Lógica de agendamentos
- `ServicesService` - Lógica de serviços
- `ReportService` - Geração de relatórios financeiros
- `JwtTokenService` - Gerenciamento de tokens JWT
- `UserDetailsServiceImpl` - Implementação de autenticação

### 3. **Repository Layer** (`repository/`)
Camada de persistência:
- Acesso ao banco de dados
- Operações CRUD
- Queries customizadas via Spring Data JPA

**Repositórios:**
- `UserRepository`
- `ClientRepository`
- `AppointmentRepository`
- `ServiceRepository`

### 4. **Entity Layer** (`entity/`)
Modelos de domínio mapeados para o banco:
- `User` - Usuários do sistema
- `Client` - Clientes do MEI
- `Appointment` - Agendamentos de serviços
- `Services` - Serviços oferecidos
- `CashFlowStatement` - Demonstrativo de fluxo de caixa
- `Role` - Papéis de acesso
- `UserDetailsImpl` - Detalhes de autenticação

### 5. **DTO Layer** (`dto/`)
Objetos de transferência de dados organizados por domínio:
- `appointmentdto/` - DTOs de agendamentos
- `clientdto/` - DTOs de clientes
- `servicedto/` - DTOs de serviços
- `userdto/` - DTOs de usuários
- `reportdto/` - DTOs de relatórios

### 6. **Configuration Layer** (`config/`)
Configurações do sistema:
- `SecurityConfiguration` - Configuração de segurança
- `UserAuthenticationFilter` - Filtro de autenticação JWT

### 7. **Exception Layer** (`exception/`)
Tratamento centralizado de exceções:
- `GlobalExceptionHandler` - Handler global
- `BusinessException` - Exceções de negócio
- `NotFoundException` - Recurso não encontrado
- `JwtTokenException` - Exceções de autenticação
- `ErrorMessage` - Estrutura de mensagens de erro

### 8. **Enums** (`enums/`)
Enumerações do sistema:
- `RoleName` - Papéis de usuário (ex: ADMIN, USER)

## Fluxo de Dados
```
Cliente → Controller → Service → Repository → Database
         ↓           ↓          ↓
        DTO       Validação  Entity
```

## Segurança
- Autenticação baseada em JWT (JSON Web Tokens)
- Filtro customizado para validação de tokens
- Spring Security para controle de acesso
- Papéis e permissões via entidade Role

## Banco de Dados
- **SGBD**: PostgreSQL
- **Versionamento**: Flyway Migrations
- **Migrations**: Localizadas em `resources/db/migration/`

## Funcionalidades Principais
1. **Gestão de Usuários** - Cadastro, autenticação e autorização
2. **Gestão de Clientes** - CRUD de clientes do MEI
3. **Gestão de Serviços** - Catálogo de serviços oferecidos
4. **Agendamentos** - Controle de compromissos e atendimentos
5. **Relatórios Financeiros** - Fluxo de caixa e receitas

## Padrões Utilizados
- **Repository Pattern** - Abstração de acesso a dados
- **DTO Pattern** - Transferência de dados entre camadas
- **Dependency Injection** - Inversão de controle via Spring
- **RESTful API** - Endpoints seguindo padrões REST
- **Builder Pattern** - Via Lombok para construção de objetos

## Como Rodar a Aplicação

### Pré-requisitos
- **Java JDK 25** instalado
- **Maven** instalado (ou utilizar o Maven Wrapper incluído)
- **PostgreSQL** instalado e em execução
- Banco de dados PostgreSQL criado

### Configuração do Banco de Dados
1. Configure as credenciais do banco no arquivo `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/meiorganizadinho
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

2. As tabelas serão criadas automaticamente pelo Flyway na primeira execução

### Execução

#### Opção 1: Usando Maven Wrapper (recomendado)
**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

#### Opção 2: Usando Maven instalado
```bash
mvn spring-boot:run
```

#### Opção 3: Gerando e executando o JAR
```bash
# Gerar o JAR
mvn clean package

# Executar o JAR
java -jar target/meiorganizadinho-0.0.1-SNAPSHOT.jar
```

### Acesso à Aplicação
Após iniciar, a aplicação estará disponível em:
```
http://localhost:8080
```

### Endpoints Principais
- `/api/users` - Gerenciamento de usuários
- `/api/clients` - Gerenciamento de clientes
- `/api/appointments` - Gerenciamento de agendamentos
- `/api/services` - Gerenciamento de serviços
- `/api/reports` - Geração de relatórios

> **Nota**: A maioria dos endpoints requer autenticação JWT. Primeiro, faça login para obter um token.
