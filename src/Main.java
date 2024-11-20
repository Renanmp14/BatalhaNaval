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


            // Criar tabuleiros para servidor e cliente
            Tabuleiro tabuleiro = new Tabuleiro(false);  // false pois o adversário não deve ver os navios
            tabuleiro.carregarTabuleiroDeJSON(arquivo);  // Carrega a tabela de navios

            Tabuleiro tabuleiroAdversario = new Tabuleiro(true);  // true pois o servidor/cliente verá os navios do adversário
            tabuleiroAdversario.carregarTabuleiroDeJSON(arquivoAdversario);

            // A thread para receber ataques
            new Thread(() -> {
                while (running) {
                    try {
                        String message = comunicacao.receiveMessage();
                        if (message != null) {
                            System.out.println("Ataque recebido na posição: " + message);
                            String[] pos = message.split(" ");
                            int linha = Integer.parseInt(pos[0]);
                            int coluna = Integer.parseInt(pos[1]);
                            char resultado = pos[2].charAt(0);

                            // Atualiza o tabuleiro do adversário após receber o ataque
                            tabuleiroAdversario.processarAtaque(linha, coluna);

                            // Exibe os tabuleiros após o ataque
                            tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");

                            // Envia a confirmação do ataque de volta
                            comunicacao.sendMessage(linha + " " + coluna + " " + resultado);
                        }
                    } catch (Exception e) {
                        System.out.println("Conexão encerrada.");
                        break;
                    }
                }
            }).start();

            // Loop principal para o envio de ataques
            while (running) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    running = false;
                    break;
                }

                String[] pos = message.split(" ");
                int linha = Integer.parseInt(pos[0]);
                int coluna = Integer.parseInt(pos[1]);

                // Processa o ataque e envia
                char resultado = tabuleiro.processarAtaque(linha, coluna);
                comunicacao.sendMessage(linha + " " + coluna + " " + resultado);

                // Atualiza o tabuleiro do jogador após o ataque
                tabuleiro.exibirTabuleiro("Tabuleiro do Jogador");

                // Atualiza o tabuleiro do adversário na tela
                tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");

                // Verifica se o jogo acabou
                if (tabuleiroAdversario.todosNaviosAfundados()) {
                    System.out.println("Você venceu!");
                    running = false;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            comunicacao.closeConnection();
        }
    }
}