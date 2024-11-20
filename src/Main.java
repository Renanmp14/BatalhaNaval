import java.util.Scanner;

import org.json.JSONArray;

public class Main {
    private static final int PORT = 8080;
    private static boolean running = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Comunicacao comunicacao = new Comunicacao();
        String ipMaquina = "192.168.0.211";
        String arquivo = "navios_" + ipMaquina + ".json";
        String arquivoAdversario = "ArquivoAdversario.json";
        try {
            System.out.println("Digite 'server' para iniciar como servidor ou 'client' para conectar:");
            String mode = scanner.nextLine();

            boolean isServer = mode.equalsIgnoreCase("server");

            if (isServer) {
                comunicacao.startServer(PORT);

                JSONArray naviosServer = PosicaoNavio.gerarPosicoesNavios();
                PosicaoNavio.salvarNaviosEmJSON(naviosServer, arquivo);

                comunicacao.sendFile(arquivo);
                comunicacao.receiveFile(".");
                System.out.println("Arquivos trocados! Iniciando troca de mensagens...");

            } else if (mode.equalsIgnoreCase("client")) {
                System.out.print("Digite o IP do servidor: ");
                String ip = scanner.nextLine();
                comunicacao.startClient(ip, PORT);

                comunicacao.receiveFile(".");

                JSONArray naviosCliente = PosicaoNavio.gerarPosicoesNavios();
                PosicaoNavio.salvarNaviosEmJSON(naviosCliente,arquivo);
                comunicacao.sendFile(arquivo);
                System.out.println("Arquivos trocados! Iniciando troca de mensagens...");

            } else {
                System.out.println("Modo inválido! Use 'server' ou 'client'.");
                return;
            }




//            new Thread(() -> {
//                while (running) {
//                    try {
//                        String message = comunicacao.receiveMessage();
//                        if (message != null) {
//                            System.out.println("Recebido: " + message);
//                        }
//                    } catch (Exception e) {
//                        System.out.println("Conexão encerrada.");
//                        break;
//                    }
//                }
//            }).start();
//            while (running) {
//                String message = scanner.nextLine();
//                if (message.equalsIgnoreCase("exit")) {
//                    running = false;
//                    break;
//                }
//                comunicacao.sendMessage(message);
//            }

            // Inicializa tabuleiros
            Tabuleiro meuTabuleiro = new Tabuleiro(true);
            meuTabuleiro.carregarTabuleiroDeJSON(arquivo);

            Tabuleiro tabuleiroAdversario = new Tabuleiro(false);

            // Exibe tabuleiros iniciais
            meuTabuleiro.exibirTabuleiro("Seu Tabuleiro");
            tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");

            boolean minhaVez = isServer; // Servidor começa atacando

            // Lógica principal do jogo
            while (running) {
                if (minhaVez) {
                    // Turno do jogador local
                    System.out.println("\nSua vez! Informe o ataque (exemplo: 09):");
                    String ataque = scanner.nextLine();

                    // Envia ataque ao adversário
                    comunicacao.sendMessage(ataque);

                    // Atualiza o tabuleiro do adversário localmente
                    int linha = Character.getNumericValue(ataque.charAt(0));
                    int coluna = Character.getNumericValue(ataque.charAt(1));
                    char resultado = tabuleiroAdversario.atualizarComBaseNoArquivo(linha, coluna, arquivoAdversario);

                    // Exibe tabuleiros atualizados
                    meuTabuleiro.exibirTabuleiro("Seu Tabuleiro");
                    tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");

                    // Verifica se venceu
                    if (tabuleiroAdversario.todosNaviosAfundados()) {
                        System.out.println("Parabéns! Você venceu!");
                        break;
                    }
                } else {
                    // Turno do adversário
                    System.out.println("\nAguardando ataque do adversário...");
                    String ataque = comunicacao.receiveMessage();

                    // Processa o ataque recebido
                    int linha = Character.getNumericValue(ataque.charAt(0));
                    int coluna = Character.getNumericValue(ataque.charAt(1));
                    char resultado = meuTabuleiro.processarAtaque(linha, coluna);

                    // Atualiza o tabuleiro localmente
                    System.out.println("Adversário atacou (" + linha + ", " + coluna + "): " +
                            (resultado == 'X' ? "Acertou um navio!" : "Acertou água!"));

                    // Envia resposta do ataque
                    comunicacao.sendMessage(String.valueOf(resultado));

                    // Exibe tabuleiros atualizados
                    meuTabuleiro.exibirTabuleiro("Seu Tabuleiro");
                    tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");

                    // Verifica se perdeu
                    if (meuTabuleiro.todosNaviosAfundados()) {
                        System.out.println("Que pena, você perdeu!");
                        break;
                    }
                }

                // Alterna a vez
                minhaVez = !minhaVez;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            comunicacao.closeConnection();
        }
    }
}