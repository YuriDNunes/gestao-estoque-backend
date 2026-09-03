# Gestão de Estoque — API

API REST para gestão de estoque, usuários e alocação de produtos. O backend permite cadastrar produtos, controlar entradas e saídas, atribuir itens a usuários, registrar devoluções e consultar o histórico das movimentações.

## Tecnologias

- Java 25
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT (Auth0 Java JWT)
- PostgreSQL
- Spring Mail
- Lombok
- Gradle

## Funcionalidades

- Autenticação stateless com access token e refresh token JWT;
- controle de acesso pelos perfis `Admin`, `Manager` e `User`;
- cadastro, listagem, atualização, bloqueio e exclusão lógica de usuários;
- geração automática de senha e envio das credenciais por e-mail;
- cadastro e manutenção de produtos;
- ajuste de estoque;
- alocação de produtos para usuários;
- devolução total ou parcial de itens alocados;
- histórico de alocações e devoluções.

## Pré-requisitos

- JDK 25;
- PostgreSQL em execução;
- um servidor SMTP para o envio das credenciais de novos usuários.

O Gradle não precisa ser instalado globalmente, pois o projeto inclui o Gradle Wrapper.

## Configuração

1. Crie um banco de dados PostgreSQL.
2. Copie o arquivo de exemplo:

```bash
cp src/main/resources/application-example.yaml src/main/resources/application.yaml
```

3. Complete `src/main/resources/application.yaml` com as propriedades abaixo:

```yaml
cors:
  originPatterns: http://localhost:3000

security:
  jwt:
    token:
      secret-key: substitua-por-uma-chave-secreta-forte
      # Duração do access token em milissegundos (1 hora)
      expire-length: 3600000

spring:
  application:
    name: inventory-management

  datasource:
    url: jdbc:postgresql://localhost:5432/inventory_management
    username: postgres
    password: sua_senha
    driver-class-name: org.postgresql.Driver

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
    show-sql: false

  mail:
    host: smtp.seu-provedor.com
    port: 587
    username: seu_email@exemplo.com
    password: sua_senha_ou_senha_de_aplicativo
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enabled: true
      mail.smtp.starttls.required: true
```

O arquivo `application.yaml` está ignorado pelo Git para evitar o versionamento de credenciais.

> **Banco de dados:** o projeto usa `ddl-auto: validate`, portanto as tabelas precisam existir antes da inicialização. Para preparar rapidamente um ambiente local, é possível usar temporariamente `ddl-auto: update`. Em ambientes compartilhados ou de produção, prefira migrations versionadas.

As roles persistidas no banco devem se chamar exatamente `Admin`, `Manager` e `User`. A aplicação converte esses nomes nas authorities `ROLE_Admin`, `ROLE_Manager` e `ROLE_User`.

## Executando a aplicação

Linux/macOS:

```bash
./gradlew bootRun
```

Windows:

```powershell
.\gradlew.bat bootRun
```

Por padrão, a API fica disponível em `http://localhost:8080`.

Para gerar o artefato executável:

```bash
./gradlew clean build
java -jar build/libs/inventory-management-0.0.1-SNAPSHOT.jar
```

## Autenticação

Faça login usando o e-mail e a senha do usuário:

```http
POST /auth/signin
Content-Type: application/json

{
  "username": "usuario@exemplo.com",
  "password": "sua_senha"
}
```

A resposta contém, entre outros dados, `accessToken` e `refreshToken`. Envie o access token nas rotas protegidas:

```http
Authorization: Bearer SEU_ACCESS_TOKEN
```

## Endpoints

### Autenticação

| Método | Rota | Descrição | Acesso |
| --- | --- | --- | --- |
| `POST` | `/auth/signin` | Autentica um usuário | Público |

### Usuários

| Método | Rota | Descrição | Acesso |
| --- | --- | --- | --- |
| `POST` | `/api/user` | Cria um usuário e envia a senha por e-mail | Admin, Manager |
| `GET` | `/api/user?role={role}` | Lista usuários ativos por perfil | Admin, Manager |
| `PUT` | `/api/user/{id}` | Atualiza um usuário | Admin, Manager |
| `PATCH` | `/api/user/{id}/access` | Libera ou bloqueia o acesso | Admin, Manager |
| `DELETE` | `/api/user/{id}` | Exclui logicamente um usuário | Admin, Manager |

Exemplo de criação:

```json
{
  "name": "Maria Silva",
  "email": "maria@exemplo.com",
  "register": "MAT-001",
  "access": true,
  "role": "User"
}
```

O campo `password` não precisa ser enviado na criação: a API gera uma senha aleatória, armazena o hash BCrypt e envia a senha ao e-mail cadastrado.

Para alterar o acesso:

```json
{
  "access": false
}
```

### Produtos

| Método | Rota | Descrição | Acesso |
| --- | --- | --- | --- |
| `POST` | `/api/product` | Cadastra um produto | Admin, Manager, User |
| `GET` | `/api/product` | Lista os produtos por código | Admin, Manager, User |
| `PUT` | `/api/product/{id}` | Atualiza um produto | Admin, Manager, User |
| `PATCH` | `/api/product/{id}/stock` | Soma ou subtrai uma quantidade do estoque | Admin, Manager, User |
| `DELETE` | `/api/product/{id}` | Exclui um produto | Admin, Manager, User |

Exemplo de produto:

```json
{
  "name": "Notebook",
  "code": "NOTE-001",
  "quantity": 10
}
```

No ajuste de estoque, use um valor positivo para entrada e negativo para saída:

```json
{
  "quantity": -2
}
```

### Alocações

| Método | Rota | Descrição | Acesso |
| --- | --- | --- | --- |
| `POST` | `/api/allocation` | Aloca um produto para um usuário | Autenticado |
| `GET` | `/api/allocation` | Lista as alocações do usuário autenticado | Autenticado |
| `POST` | `/api/allocation/{id}/return` | Registra devolução total ou parcial | Dono da alocação |

Exemplo de alocação:

```json
{
  "targetUserId": 2,
  "productId": 1,
  "quantity": 1
}
```

Na devolução, o corpo utiliza o mesmo DTO, mas somente `quantity` é considerada pela implementação atual:

```json
{
  "quantity": 1
}
```

### Histórico

| Método | Rota | Descrição | Acesso |
| --- | --- | --- | --- |
| `GET` | `/api/history` | Lista as movimentações da mais recente para a mais antiga | Autenticado |
| `POST` | `/api/history/allocate` | Registra uma alocação e baixa o estoque | Autenticado |
| `POST` | `/api/history/return` | Registra uma devolução e repõe o estoque | Autenticado |

```json
{
  "targetUserId": 2,
  "managerId": 1,
  "productId": 1,
  "quantity": 1
}
```

Em `/api/history/allocate`, o gestor é obtido do token e `managerId` não é utilizado. Em `/api/history/return`, o `managerId` enviado no corpo é utilizado.

## Testes

Execute os testes automatizados com:

```bash
./gradlew test
```

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/com/management/inventory/
│   │   ├── auth/                 # Autenticação e perfis
│   │   ├── config/               # Segurança, CORS e beans
│   │   ├── history/              # Histórico de movimentações
│   │   ├── product/              # Produtos e estoque
│   │   ├── productAllocation/    # Alocações e devoluções
│   │   ├── security/             # Filtro e provedor JWT
│   │   ├── shared/               # Tipos e utilitários compartilhados
│   │   └── user/                 # Usuários
│   └── resources/
│       └── application-example.yaml
└── test/                          # Testes automatizados
```

## Observações de segurança

- Não versione o `application.yaml` nem credenciais reais.
- Use uma chave JWT longa, aleatória e exclusiva por ambiente.
- Use senha de aplicativo ou outro mecanismo seguro oferecido pelo provedor SMTP.
- Restrinja `cors.originPatterns` à URL real do frontend em produção.

