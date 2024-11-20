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

        Tabuleiro tabuleiro = new Tabuleiro(true);
        Tabuleiro tabuleiroAdversario = new Tabuleiro(false);
        String[] meusNavios = new String[10]; // Array para armazenar posições dos navios
        String[] adversarioNavio = new String[10]; // Array para o navio do adversário
        String[] envioAtaque = new String[100]; // Array para armazenar ataques enviados
        String[] envioAdversario = new String[100]; // Array para armazenar ataques recebidos

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


            // Carregar e exibir os tabuleiros após o envio dos arquivos
            tabuleiro.carregarTabuleiroDeJSON(arquivo);
            tabuleiroAdversario.carregarTabuleiroDeJSON(arquivoAdversario);
            tabuleiro.exibirTabuleiro("Tabuleiro do Jogador");
            tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");

            // Thread para receber ataques
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
                            envioAdversario[linha * 10 + coluna] = message; // Armazena a posição atacada
                            tabuleiroAdversario.processarAtaque(linha, coluna);
                            tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");
                            comunicacao.sendMessage(linha + " " + coluna + " " + resultado); // Envia resultado
                        }
                    } catch (Exception e) {
                        System.out.println("Conexão encerrada.");
                        break;
                    }
                }
            }).start();

            // Loop para envio de ataques
            while (running) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    running = false;
                    break;
                }

                String[] pos = message.split(" ");
                int linha = Integer.parseInt(pos[0]);
                int coluna = Integer.parseInt(pos[1]);
                char resultado = tabuleiro.processarAtaque(linha, coluna);


                envioAtaque[linha * 10 + coluna] = message; // Armazena o ataque enviado
                comunicacao.sendMessage(linha + " " + coluna + " " + resultado);
                tabuleiro.exibirTabuleiro("Tabuleiro do Jogador");
                tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");

                // Verifica se o jogo acabou
                if (tabuleiroAdversario.todosNaviosAfundados()) {
                    System.out.println("Você venceu!");
                    running = false;
                }
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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            comunicacao.closeConnection();
        }
    }
}