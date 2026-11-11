# Documentação

## Requisitos Funcionais

- **RF01 (Acesso ao Dashboard)** - O sistema deve disponibilizar um dashboard administrativo para visualização e acompanhamento das vendas, permitindo o acesso exclusivamente aos usuários com perfis GERENTE.

- **RF02 (Indicadores de Vendas)** - O sistema deve apresentar no dashboard os seguintes indicadores: faturamento total, quantidade de pedidos finalizados, quantidade de pedidos cancelados e ticket médio.

- **RF03 (Análise Gráfica de Vendas)** - O sistema deve apresentar gráficos que permitam visualizar a evolução do faturamento durante o período selecionado e a distribuição dos pedidos por estado.

- **RF04 (Ranking de Produtos)** - O sistema deve apresentar um ranking dos produtos mais vendidos, ordenados de forma decrescente pela quantidade de unidades vendidas, considerando exclusivamente os pedidos finalizados.

- **RF05 (Contabilização de Vendas)** - O sistema deve considerar exclusivamente os pedidos com estado `FINALIZADO` para o cálculo do faturamento total e da quantidade de pedidos concluídos, utilizando o valor final dos pedidos após a aplicação de descontos.

- **RF06 (Filtro por Período)** O sistema deve permitir filtrar os dados do dashboard por períodos predefinidos de 7, 30 ou 90 dias, incluindo o dia atual, além de disponibilizar a seleção de um período personalizado por meio de datas inicial e final.

- **RF07 (Filtro por Produto)** O sistema deve permitir filtrar os dados do dashboard por todos os produtos ou por um produto específico, apresentando os indicadores correspondentes aos critérios selecionados.

- **RF08 (Cálculo do Ticket Médio)** - O sistema deve calcular o ticket médio dos pedidos finalizados no período selecionado, utilizando a seguinte fórmula:

$$
\text{Ticket Médio} = \frac{\text{Faturamento Total}}{\text{Quantidade de Pedidos Finalizados}}
$$

- **RF09 (Tempo Médio de Processamento dos Pedidos)** - O sistema deve apresentar o tempo médio decorrido entre as transições dos estados dos pedidos, utilizando os registros do histórico de alterações e considerando as transições realizadas no período selecionado.

## Requisitos Não Funcionais

- **RNF01 (Segurança de Acesso)** - O sistema deve garantir que apenas usuários autenticados com perfil GERENTE tenham acesso ao dashboard e aos dados apresentados.

- **RNF02  Integridade dos Dados** - O sistema deve garantir que os indicadores, gráficos e rankings sejam calculados com base nos dados persistidos no banco de dados, respeitando as regras de contabilização estabelecidas.

- **RNF03  Precisão dos Cálculos** - O sistema deve garantir a precisão dos cálculos de faturamento, ticket médio e tempo médio de processamento dos pedidos, evitando inconsistências decorrentes de operações aritméticas.

- **RNF04  Desempenho** - O sistema deve realizar as consultas e agregações de dados de maneira eficiente, evitando o processamento desnecessário de registros que não correspondam aos filtros aplicados.

- **RNF05  Usabilidade** - O dashboard deve apresentar os indicadores, gráficos, rankings e filtros de maneira clara, organizada e de fácil compreensão para os usuários gestores.

- **RNF06  Responsividade** - A interface do dashboard deve adaptar-se a diferentes resoluções e tamanhos de tela, preservando a legibilidade e a utilização dos recursos disponíveis.

- **RNF07  Manutenibilidade** - A implementação deve seguir a arquitetura em camadas adotada pelo projeto, mantendo a separação de responsabilidades entre Controller, Service, Repository e componentes de apresentação.

- **RNF08  Confiabilidade** - O sistema deve tratar adequadamente situações de ausência de dados, períodos sem vendas e falhas nas consultas, evitando a apresentação de informações incorretas ou a interrupção inesperada da interface.

- **RNF09  Compatibilidade** - O dashboard deve ser integrado às tecnologias e à estrutura existentes no sistema Pediu Patrão, utilizando Spring Boot, MongoDB, Thymeleaf e Bootstrap.

- **RNF10  Consistência dos Filtros** - O sistema deve aplicar os critérios de período e produto de maneira consistente entre os indicadores, gráficos e rankings, garantindo que os resultados apresentados correspondam aos filtros selecionados.
