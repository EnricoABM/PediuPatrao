# Checklist de Desenvolvimento por Funcionalidade

## RF01 - Controle de Usuários e Permissões (2,0 pts)
* **Gerenciamento de Perfis e Autenticação:**
  - [x] Atualizar o modelo/enum de perfis para contemplar: `ADMIN`, `GERENTE` e `ATENDENTE`.
  - [x] Ajustar o `UserDetailsService` / fluxo de autenticação para carregar e atribuir corretamente as *authorities* de acordo com o perfil cadastrado no MongoDB.
* **Proteção Backend com Spring Security (`SecurityConfig`):**
  - [x] Configurar controle de acesso em nível de rota (ex: `.hasRole()` / `.hasAnyRole()`) para proteger URLs de ADMIN, GERENTE e ATENDENTE.
  - [ ] Aplicar anotações `@PreAuthorize` na camada de serviço/controlador em métodos restritos.
  - [ ] Garantir que ações exclusivas do `GERENTE` (descontos e cancelamentos) fiquem bloqueadas no backend para `ADMIN` e `ATENDENTE`.
  - [ ] Proteger rotas de criação/alteração de usuários para evitar atribuição indevida de privilégios e autoelevação de perfil.
  - [ ] Revisar e validar a proteção CSRF nos formulários e endpoints de operações sensíveis.
* **Interface Thymeleaf:**
  - [x] Ajustar a exibição condicional de botões/links na interface com base no perfil autenticado (sem depender exclusivamente dessa camada para segurança).

---

## RF02 - Evolução do Cadastro de Pedidos (1,0 pt)
* **Modelagem e Persistência de Dados:**
  - [ ] Atualizar a entidade `Pedido` no MongoDB para incluir os novos campos: itens, quantidades, preços unitários, descontos aplicados, valor total, usuário responsável pelo registro e `data/hora de entrada`.
* **Regras de Negócio e Cálculo no Servidor:**
  - [ ] Implementar o cálculo do valor total no backend em `PedidoService`, somando o valor dos itens e aplicando os descontos autorizados.
  - [ ] Impedir que a requisição HTTP defina ou sobrescreva arbitrariamente o valor total calculado.
  - [ ] Registrar automaticamente o usuário autenticado atual como o responsável pelo cadastro do pedido.
  - [ ] Registrar a data/hora exata do cadastro no servidor.

---

## RF03 - Controle de Entrada, Saída e Transições dos Pedidos (1,5 pt)
* **Fluxo Obrigatório de Status:**
  - [ ] Modelar a máquina de estados / Enum de status: `RECEBIDO` $\rightarrow$ `EM PREPARAÇÃO` $\rightarrow$ `PRONTO` $\rightarrow$ `SAIU PARA ENTREGA/RETIRADO` $\rightarrow$ `FINALIZADO` (além do estado excepcional `CANCELADO`).
  - [ ] Implementar validação em `PedidoService` para bloquear transições inválidas (ex: passar direto de `EM PREPARAÇÃO` para `FINALIZADO`).
* **Registros de Entrada, Saída e Responsáveis:**
  - [ ] Capturar e gravar a data/hora e o usuário responsável na criação (`RECEBIDO`).
  - [ ] Capturar obrigatoriamente a data/hora e o responsável pelo encerramento na transição para `FINALIZADO` ou `SAIU PARA ENTREGA/RETIRADO`.
* **Bloqueios Pós-Cancelamento:**
  - [ ] Garantir o bloqueio total de qualquer transição ou alteração de status em pedidos que estejam como `CANCELADO`.

---

## RF04 - Cancelamentos e Descontos Exclusivos do Gerente (1,5 pt)
* **Aplicações de Desconto:**
  - [ ] Criar operação restrita ao perfil `GERENTE` no backend para aplicar desconto.
  - [ ] Adicionar validação de regra de negócio: desconto percentual limitado a no máximo 20%.
  - [ ] Validar no backend para permitir desconto apenas **antes** de o pedido transitar para `SAIU PARA ENTREGA/RETIRADO`.
* **Cancelamento de Pedidos:**
  - [ ] Criar operação exclusiva do `GERENTE` para cancelamento de pedidos.
  - [ ] Exigir o preenchimento obrigatório do campo de justificativa para realizar o cancelamento.
  - [ ] Validar para impedir o cancelamento caso o pedido já tenha saído para entrega ou sido retirado.
  - [ ] Preservar os dados históricos do pedido sem excluí-lo do banco de dados.

---

## RF05 - Auditoria das Alterações de Pedidos e Clientes (3,0 pts)
* **Modelagem e Infraestrutura de Auditoria:**
  - [ ] Criar a entidade de auditoria MongoDB, a interface de repositório (`AuditoriaRepository`) e a camada de serviço (`AuditoriaService`).
  - [ ] Mapear na entidade os campos obrigatórios: `usuário`, `perfil`, `data/hora`, `tipo de operação`, `entidade afetada (e ID)`, `campos modificados (valores anteriores e novos)` e `justificativa` (quando houver).
* **Interceptação e Disparo de Eventos:**
  - [ ] Integrar o disparo de auditoria em inclusões e alterações de pedidos, mudanças de status, aplicação de descontos e cancelamentos.
  - [ ] Integrar o disparo de auditoria nas alterações de cadastros de clientes (foco em logradouro/endereço e telefone).
  - [ ] Garantir o registro detalhado comparativo (de/para) de campos modificados.
* **Segurança e Restrições de Leitura/Escrita:**
  - [ ] Criar endpoint e interface para consulta de logs de auditoria restritos exclusivamente para os perfis `ADMIN` e `GERENTE`.
  - [ ] Bloquear completamente qualquer endpoint ou rota de edição/exclusão de registros de auditoria.
  - [ ] Garantir que senhas, tokens ou dados sensíveis/credenciais **nunca** sejam gravados nos registros de auditoria.
  - [ ] Tratar/mascarar dados pessoais expostos na consulta de auditoria, se aplicável, respeitando a finalidade da regra.

---

## Qualidade, Testes e Organização do Código (1,0 pt)
* [ ] Garantir arquitetura em camadas clara e coerente (`controller`, `service`, `repository`, `model/entity`).
* [ ] Implementar tratamento centralizado de exceções (ex: `@ControllerAdvice` ou `@ExceptionHandler`) para acessos negados, regras de negócio violadas e transições inválidas.
* [ ] Garantir respostas HTTP com status codes apropriados (403 Forbidden, 400 Bad Request, etc.) e mensagens claras de erro.
* [ ] Escrever testes de unidade/integração para validação das principais regras e bloqueios de segurança.

---

# Entregáveis da Atividade

- [ ] **Repositório GitHub Individual:** Código-fonte completo com histórico de commits que evidencie o desenvolvimento individual.
- [ ] **Arquivo `README.md`:**
  - [ ] Instruções passo a passo para execução do projeto e do MongoDB.
  - [ ] Resumo explicativo com a lista dos requisitos funcionais implementados.
  - [ ] Descrição técnica das principais alterações e soluções de arquitetura/segurança adotadas.
- [ ] **Vídeo de Demonstração Prática:**
  - [ ] Gravação direta da tela demonstrando a execução real das funcionalidades.
  - [ ] Demonstração da autenticação com cada um dos três perfis (`ADMIN`, `GERENTE`, `ATENDENTE`).
  - [ ] Comprovação visual de bloqueio backend ao tentar realizar ações não autorizadas (ex: tentar cancelar/descontar via API como ADMIN/ATENDENTE).
  - [ ] Demonstração da criação, transições do fluxo de status e encerramento de pedido.
  - [ ] Aplicação de desconto e cancelamento justificável executados pelo `GERENTE`.
  - [ ] Alteração de dados cadastrais de cliente (endereço/telefone).
  - [ ] Exibição dos registros na tela de auditoria resultantes de todas as operações realizadas durante a gravação.

---
