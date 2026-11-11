# Endpoints

## Dashboard

| Método | URL          | Request                                                                               | Response                                                                   |
| ------ | ------------ | ------------------------------------------------------------------------------------- | -------------------------------------------------------------------------- |
| GET    | `/dashboard` | Query opcional: `periodo` (7, 30 ou 90), `inicio` e `fim` (`yyyy-MM-dd`), `produtoId` | `200 OK` - renderiza `layout` com o fragmento `dashboard/index :: content` |

**Exemplo de requisição:**

```http
GET /dashboard?periodo=30&produtoId=produto-1
```

## Pedidos

| Método | URL                      | Request                                       | Response                                              |
| ------ | ------------------------ | --------------------------------------------- | ----------------------------------------------------- |
| GET    | `/pedidos`               | Sem parâmetros                                | `200 OK` - lista de pedidos                           |
| GET    | `/pedidos/novo`          | Sem parâmetros                                | `200 OK` - formulário de cadastro                     |
| POST   | `/pedidos`               | Formulário: `clienteId`, `usuarioId`, `itens` | `302 Found` - redireciona para `/pedidos`             |
| GET    | `/pedidos/exibir/{id}`   | Path: `id`                                    | `200 OK` - detalhes do pedido                         |
| POST   | `/pedidos/{id}/avancar`  | Path: `id`                                    | `302 Found` - redireciona para `/pedidos/exibir/{id}` |
| POST   | `/pedidos/{id}/cancelar` | Path: `id`; formulário: `justificativa`       | `302 Found` - redireciona para os detalhes            |
| POST   | `/pedidos/{id}/desconto` | Path: `id`; formulário: `desconto` (inteiro)  | `302 Found` - redireciona para os detalhes            |

## Produtos

| Método | URL                      | Request                                            | Response                                                                          |
| ------ | ------------------------ | -------------------------------------------------- | --------------------------------------------------------------------------------- |
| GET    | `/produtos`              | Sem parâmetros                                     | `200 OK` - lista de produtos                                                      |
| GET    | `/produtos/novo`         | Sem parâmetros                                     | `200 OK` - formulário de cadastro                                                 |
| POST   | `/produtos`              | Formulário: `id`, `nome`, `tipo`, `preco`, `ativo` | `302 Found` - redireciona para `/produtos`                                        |
| GET    | `/produtos/editar/{id}`  | Path: `id`                                         | `200 OK` - formulário preenchido; se não encontrado, redireciona para `/produtos` |
| POST   | `/produtos/deletar/{id}` | Path: `id`                                         | `302 Found` - redireciona para `/produtos`                                        |

## Clientes

| Método | URL                      | Request                                  | Response                                                                          |
| ------ | ------------------------ | ---------------------------------------- | --------------------------------------------------------------------------------- |
| GET    | `/clientes`              | Sem parâmetros                           | `200 OK` - lista de clientes                                                      |
| GET    | `/clientes/novo`         | Sem parâmetros                           | `200 OK` - formulário de cadastro                                                 |
| POST   | `/clientes/salvar`       | Formulário: dados do cliente             | `302 Found` - redireciona para `/clientes`                                        |
| GET    | `/clientes/editar/{id}`  | Path: `id`                               | `200 OK` - formulário preenchido; se não encontrado, redireciona para `/clientes` |
| POST   | `/clientes/editar/{id}`  | Path: `id`; formulário: dados do cliente | `302 Found` - redireciona para `/clientes`                                        |
| POST   | `/clientes/deletar/{id}` | Path: `id`                               | `302 Found` - redireciona para `/clientes`                                        |

## Usuários

| Método | URL                      | Request                                              | Response                                                                          |
| ------ | ------------------------ | ---------------------------------------------------- | --------------------------------------------------------------------------------- |
| GET    | `/usuarios`              | Sem parâmetros                                       | `200 OK` - lista de usuários                                                      |
| GET    | `/usuarios/novo`         | Sem parâmetros                                       | `200 OK` - formulário de cadastro                                                 |
| POST   | `/usuarios/salvar`       | Formulário: `username`, `password`, `role`           | `302 Found` - redireciona para `/usuarios`                                        |
| GET    | `/usuarios/editar/{id}`  | Path: `id`                                           | `200 OK` - formulário preenchido; se não encontrado, redireciona para `/usuarios` |
| POST   | `/usuarios/editar/{id}`  | Path: `id`; formulário: campos da entidade `Usuario` | `302 Found` - redireciona para `/usuarios`                                        |
| POST   | `/usuarios/deletar/{id}` | Path: `id`                                           | `302 Found` - redireciona para `/usuarios`                                        |

## Auditoria

| Método | URL          | Request        | Response                                   |
| ------ | ------------ | -------------- | ------------------------------------------ |
| GET    | `/auditoria` | Sem parâmetros | `200 OK` - lista de registros de auditoria |

## Autenticação

| Método | URL       | Request                            | Response                                                                   |
| ------ | --------- | ---------------------------------- | -------------------------------------------------------------------------- |
| GET    | `/login`  | Sem parâmetros                     | `200 OK` - página de login                                                 |
| GET    | `/`       | Sem parâmetros                     | `200 OK` - página inicial                                                  |
| POST   | `/login`  | Formulário: `username`, `password` | Autenticação via Spring Security; em caso de sucesso, redireciona para `/` |
| POST   | `/logout` | Sessão autenticada                 | `302 Found` - redireciona para `/login?logout`                             |
