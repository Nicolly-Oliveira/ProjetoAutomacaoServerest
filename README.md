# 🧪 Projeto de Testes Automatizados - Serverest API

Este projeto foi desenvolvido com o objetivo de automatizar testes de API REST utilizando Java, JUnit 5, Maven e gerar relatórios com Allure. A aplicação testada é a [Serverest API](https://serverest.dev/), focada em simular um ambiente de e-commerce para testes.

## 🚀 Tecnologias Utilizadas

- Java 
- Maven
- JUnit 5
- Rest Assured
- Allure Reports
- GitHub Actions

## ✅ Objetivos dos Testes

- Verificar se as funcionalidades da API estão retornando os dados esperados.
- Validar fluxos completos (jornadas de usuário).
- Manter a qualidade do software por meio de testes automatizados e CI.

## 🧠 Organização e Estrutura Colaborativa

Este projeto foi desenvolvido em colaboração, como entrega final de uma disciplina de pós-graduação. A estrutura foi pensada para facilitar a independência dos testes, possibilitando que diferentes níveis de maturidade nas tecnologias pudessem colaborar de forma ativa e evoluir seu conhecimento. Veja como ela está organizada:

### 📁 `testsAlternativos`
Contém testes isolados por funcionalidade. Cada classe foca em um recurso específico da API, como usuários ou produtos. Ideal para validar comportamentos em cenários mais simples e independentes.

### 📁 `testsJornadas`
Abriga testes que simulam **jornadas completas de usuário**, como um fluxo de compra ou administração de produtos. Esses testes tendem a cobrir múltiplas funcionalidades de ponta a ponta, ajudando a garantir a integridade do sistema como um todo.

### 📄 `SuiteRunner.java`
Classe responsável por orquestrar a execução em conjunto dos testes das duas pastas acima. Usamos as anotações `@Suite` e `@SelectClasses` do JUnit 5 para permitir que o Maven execute uma suíte completa com um único comando.

### 📁 `.github/workflows`
Contém o workflow de CI configurado com GitHub Actions. Esse pipeline:

- Executa os testes a cada push ou pull request na branch `main`.
- Gera o relatório Allure com os resultados da execução.
- Publica o relatório automaticamente via [GitHub Pages](https://nicolly-oliveira.github.io/ProjetoAutomacaoServerest/).

> Durante o desenvolvimento, cada colaborador ficou responsável por uma parte da estrutura de testes e discutimos em grupo como manter a padronização de nomeação, organização e foco de cada classe. Essa colaboração foi essencial para que todos pudessem colaborar conforme seu conhecimento. Então ficou dividido da seguinte forma: 
>- [Nicolly Oliveira](https://github.com/Nicolly-Oliveira): responsável por montar a estrutura do projeto, a estratégia de testes, o workflow no GitHub Actions e a publicação do relatório no GitHub Pages.
>- [Isabelle Ramalho](https://github.com/isabellemr): responsável pela criação da jornada do usuário comprador.
>- [David Celani](https://github.com/davidwcelani): responsável pela jornada de administração de produtos.
>- [Ernani Nogueira](https://github.com/Ernanenn): responsável pelos testes alternativos, focando em cenários de borda e comportamentos inesperados.

## 🧪 Como Executar os Testes

```bash
mvn clean test -Dtest=suite.SuiteRunner -Dallure.results.directory=target/allure-results
