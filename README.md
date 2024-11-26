# **Batalha Naval**

O projeto **Batalha Naval** é um jogo local para dois jogadores, utilizando o protocolo TCP para comunicação. O objetivo é simular a experiência clássica de uma batalha naval, implementando conceitos de redes aprendidos ao longo do semestre.

## **Índice**
- [Sobre](#sobre)
- [Tecnologias](#tecnologias)
- [Pré-Requisitos](#pré-requisitos)
- [Como Jogar](#como-jogar)
- [Limitações](#limitações)

## **Sobre**
Este projeto foi desenvolvido como parte do trabalho final do semestre, explorando a aplicação prática do protocolo TCP. O objetivo era implementar uma versão digital do jogo **Batalha Naval**, promovendo interação entre dois jogadores em uma rede local.

## **Tecnologias**
As principais tecnologias e bibliotecas utilizadas no projeto são:
- **Java**: Linguagem de programação principal.
- **json-20240205.jar**: Biblioteca utilizada para manipulação de arquivos JSON.

## **Pré-Requisitos**
1. Ambas as máquinas devem estar conectadas à **mesma rede local**.
2. A **porta 8080** deve estar aberta e sem bloqueios.
3. É necessário importar a biblioteca **json-20240205.jar** para o correto funcionamento do código.
4. O jogador que se conecta à partida precisa saber o **IP da máquina do adversário**.
5. O envio de mensagens é realizado utilizando coordenadas de linha e coluna no formato **"XY"** (exemplo: `09` para linha 0 e coluna 9).
6. O tabuleiro tem tamanho fixo de **10x10**.
7. A gestão de turnos é manual, feita pelos próprios jogadores.

## **Como Jogar**
1. **Iniciar o jogo**:
   - Ao executar o código, o jogador é perguntado se deseja **iniciar uma partida** ou **conectar-se a uma existente**.
   - Caso inicie a partida, o jogador aguardará a conexão do adversário.
   - Caso se conecte a uma partida, será necessário informar o **IP do jogador que iniciou**.

2. **Configuração inicial**:
   - As posições dos navios são geradas aleatoriamente pelo sistema, garantindo:
     - Agilidade no início do jogo.
     - Evitar sobreposição de navios.
     - Prevenir manipulações no posicionamento.

3. **Jogabilidade**:
   - O controle de turnos é manual:
     - Um jogador informa a posição que deseja atacar (ex.: `23` para linha 2, coluna 3), enquanto o outro aguarda.
     - Após o envio da jogada, as tabelas são atualizadas, indicando **"acerto"** ou **"erro"**.
   - Em seguida, o adversário realiza sua jogada.

4. **Finalização**:
   - O jogo termina quando um jogador acertar todas as posições dos navios do adversário.
   - Após o término, a conexão é encerrada e os resultados finais são exibidos.

## **Limitações**
- **Gestão de turnos**: O código não gerencia os turnos automaticamente; os jogadores devem realizar essa gestão manualmente.
- **Persistência de dados**: O jogo não salva o progresso em caso de interrupção abrupta.
- **Dependência de biblioteca**: É necessário importar manualmente a biblioteca **json-20240205.jar** para manipulação de JSON.
