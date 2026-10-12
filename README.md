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
* AdminLTE

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
