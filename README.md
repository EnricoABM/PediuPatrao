<div align="center">

# PediuPatrao

![home screen](https://github.com/EnricoABM/PediuPatrao/blob/main/docs/home-screenshot.png)

### Sistema para gerenciamento de pedidos, usuários e produtos de uma pizzaria. Envolve o controle de acesso, política de segurança e auditoria de ações de seus usuários. 

</div>

---

## Tecnologias
* Java 21
* Spring Framework
* Spring Security
* MongoDB
* Thymeleaf
* Bootstrap

## Como executar

### 1. Dependências

* Java 21
* MongoDB
* Maven

### 2. Clone o repositório.
```git
git clone https://github.com/EnricoABM/PediuPatrao.git

cd PediuPatrao
```

### 3. Copie o arquivo `.env.example` e preencha às variáveis de ambiente.
```text
cp .env.example .env
```

### 4. Inicie o container do mongo e mongo-express (Opcional).

> [!NOTE]
> Caso o MongoDB não esteja instalado na máquina, execute um container docker para criação do banco de dados.


Criando rede interna para conexão dos containeres
```bash
docker network create mongo-net
```

Criando container do MongoDB.
```bash
docker run --name mongo -di -p 27017:27017 \
--net mongo-net \
-e MONGO_INITDB_ROOT_USERNAME=CHANGE \
-e MONGO_INITDB_ROOT_PASSWORD=CHANGE \
mongo:latest
```

Criando container do Mongo-Express para acessar o banco de dados (Opcional).
```bash
docker run --name mongo-express -di -p 8081:8081 \
--net mongo-net \
-e ME_CONFIG_BASICAUTH_USERNAME=CHANGE \
-e ME_CONFIG_BASICAUTH_PASSWORD=CHANGE \
-e ME_CONFIG_MONGODB_PORT=27017 \
-e ME_CONFIG_MONGODB_ADMINUSERNAME=CHANGE \
-e ME_CONFIG_MONGODB_ADMINPASSWORD=CHANGE \
-e ME_CONFIG_MONGODB_SERVER=mongo \
mongo-express
```

### 5. Suba o projeto Spring

Execute o comando.
```bash
./mvnw spring-boot:run
```

Acesse as URLs:
* Web: [http://localhost:8080](http://localhost:8080)
* Mongo-Express: [http://localhost:8081](http://localhost:8081)

## Funcionalidades

### Gestão de Perfis
* A política de sessão do projeto foi mantida com o uso de sessões mantidas pelo servidor.
* Aplicado controle de acesso baseado em rotas (RBAC) e cada operação possui uma permissão associada (PoLP), sendo necessário que o usuário a possua para executar ações no sistema.
* Criado perfis para usuários do sitema, `ADMIN`, `GERENTE`, `ATENDENTE` com atribuição de permissões de forma granular.
* Alterado a atribuição geral de perfis aos usuários, conforme cadastro no banco de dados.
* Criado controle de rotas utilizando anotações de nível de método, `@hasRole`, `@hasAnyRole`, `@hasAuthority` e `@hasAnyAuthority`, nas classes de serviço do backend e, junto a extensão do thymeleaf com o spring security, as tags `sec:authorize` para bloquear o acesso as funcionalidades no frontend.

### Cadastro de Usuários
* Usuários podem ser cadastrados apenas por administradores do sistema.
* Foi removido endpoints de `HomeController`, sendo transferidos para `UsuarioController`.
* Criado bancada de testes unitários para classe `UsuarioService`.

### Cadastro de Produtos
* Produtos podem ser cadastrados apenas por gerentes, permitindo que atendentes apenas os consultem.
* Foi removido endpoints de `HomeController`, sendo transferidos para `ProdutoController`.
* Criado bancada de testes unitários para classe `ProdutoService`.

### Cadastro de Clientes
* Clientes podem ser cadastrados apenas por gerentes e atendentes, porém sua exclusão é permitida apenas para gerentes.
* Foi removido endpoints de `HomeController`, sendo transferidos para `ClienteController`.
* Criado bancada de testes unitários para classe `ClienteService`.

### Gestão de Pedidos
* A entidade foi alterada para monitoramento do pedido, incrementado data e hora de criação, usuário responsável, histórico de alterações de status e justificativa de cancelamento.
* Criado aplicação de desconto aos pedidos com regra de aplicação limitada a 20%.
* Criado controle sequencial para o avanço do estado do pedido, além de bloqueios para o cancelamento e aplicação de desconto em pedidos retirados, finalizados ou cancelados.
* Criado mecanismo para cancelamento de pedidos, salve justificativa cadastrada, aos gerentes do sistema.

> [!WARNING]
> A atualização do valor exibido ao funcionário durante o cadastro do pedido é meramente visual e utiliza funções javascript. O cálculo final é realizado pelo backend, conforme o preço de cada produto e sua quantidade. 
