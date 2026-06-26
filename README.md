# OOP Clicker

> Um jogo incremental (*Clicker Game*) desenvolvido em **Java** utilizando **JavaFX**, criado como projeto final da disciplina de **Programação Orientada a Objetos**.

<p align="center">

*Java • JavaFX • Maven • FXML • Scene Builder*

</p>

---

# Sobre o projeto

**OOP Clicker** é um jogo incremental inspirado em títulos como *Cookie Clicker*, porém ambientado no universo da Programação Orientada a Objetos.

O objetivo do jogador é produzir o maior número possível de **Instâncias**, inicialmente através de cliques e, posteriormente, utilizando construções automáticas e upgrades que representam conceitos da programação, como Estagiários, Classes, Herança, Threads, Design Patterns e Clusters Computacionais.

Ao atingir o limite do sistema, o jogo encerra com um evento especial simulando um **OutOfMemoryError** da JVM.

Além do aspecto lúdico, o projeto foi desenvolvido para demonstrar diversos conceitos de orientação a objetos e engenharia de software.

---

# Funcionalidades

* Clique manual para gerar instâncias
* Sistema de clique crítico
* Produção automática por segundo
* Loja de construções
* Sistema de upgrades
* Upgrades de produção
* Upgrades de poder de clique
* Painel Debug
* Sistema de salvamento em três slots
* Sistema de carregamento de partidas
* Interface gráfica em JavaFX
* Animações visuais
* Representação gráfica das construções
* Evento final personalizado de vitória

---

# Tecnologias utilizadas

* Java
* JavaFX
* FXML
* Scene Builder
* Maven

---

# Estrutura do projeto

```text
src
└── main
    ├── java
    │   ├── controller
    │   ├── exception
    │   ├── model
    │   ├── persistence
    │   ├── service
    │   ├── util
    │   └── view
    │
    └── resources
        ├── fxml
        └── images
```

Cada pacote possui uma responsabilidade específica, seguindo uma arquitetura em camadas.

---

# Conceitos de Programação Orientada a Objetos

O projeto demonstra a utilização de diversos conceitos da disciplina, incluindo:

* ✔ Encapsulamento
* ✔ Abstração
* ✔ Herança
* ✔ Polimorfismo
* ✔ Classes abstratas
* ✔ Interfaces
* ✔ Coleções polimórficas
* ✔ Tratamento de exceções
* ✔ Exceções próprias
* ✔ Persistência em arquivos
* ✔ Arquitetura em camadas

---

# Interface

A aplicação possui três áreas principais:

| Área     | Descrição                                          |
| -------- | -------------------------------------------------- |
| Esquerda | Informações da partida e botão principal de clique |
| Centro   | Representação visual das construções adquiridas    |
| Direita  | Loja de construções e upgrades                     |

Além disso, o projeto possui um **Painel Debug** utilizado para testes e desenvolvimento.

---

# Persistência

O progresso do jogador pode ser salvo e carregado utilizando arquivos `.properties`.

Atualmente existem três slots independentes de salvamento:

```text
save1.properties
save2.properties
save3.properties
```

São armazenadas informações como:

* quantidade de instâncias;
* construções adquiridas;
* upgrades comprados;
* estado geral da partida.

---

# Como executar

## Clonar o repositório

```bash
git clone https://github.com/SEU-USUARIO/OOP_Clicker.git
```

## Abrir o projeto

Abra o projeto em uma IDE compatível com Maven, como:

* IntelliJ IDEA
* Eclipse
* NetBeans

## Executar

Execute a classe:

```text
view.Launcher
```

---

# Capturas de tela

## Tela principal

<img width="1576" height="892" alt="TelaPrincipal" src="https://github.com/user-attachments/assets/1de64226-dc36-42aa-b768-a427c37f3b9d" />

---

## Painel Debug

<img width="725" height="780" alt="TelaDebugg" src="https://github.com/user-attachments/assets/181f2b4b-482c-496d-81a6-065c84cde1c6" />

---

## Evento de vitória

<img width="1592" height="937" alt="TelaVitoria" src="https://github.com/user-attachments/assets/ef5d04c9-fff7-445d-8796-1d99ef4bea95" />

---

# Organização do projeto

```text
OOP_Clicker
│
├── saves
├── src
├── pom.xml
├── README.md
└── .gitignore
```

---

# Status

Projeto desenvolvido para fins acadêmicos como trabalho final da disciplina de **Programação Orientada a Objetos**.
