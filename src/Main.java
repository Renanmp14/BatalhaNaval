import java.util.*;

import org.json.JSONArray;

public class Main {
    private static final int PORT = 8080;
    private static boolean running = true;
    private static boolean isMyTurn;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Comunicacao comunicacao = new Comunicacao();
        Batalha batalha = new Batalha();
        String ipMaquina = "192.168.0.211";
        String arquivo = "navios_" + ipMaquina + ".json";
        String arquivoAdversario = "ArquivoAdversario.json";

        Tabuleiro tabuleiro = new Tabuleiro(true);
        Tabuleiro tabuleiroAdversario = new Tabuleiro(false);

        List<String> tiroAdversario = new ArrayList<>();
        List<String> tiroAtaque = new ArrayList<>();


        try {
            System.out.println("Digite 'server' iniciar o Jogo ou 'client' para conectar em um jogo iniciado:");
            String mode = scanner.nextLine();

            boolean isServer = mode.equalsIgnoreCase("server");

            if (isServer) {
                comunicacao.startServer(PORT);

                isMyTurn = true;
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

            //Criar uma explicação sobre o jogo


            // Carregar e exibir os tabuleiros após o envio dos arquivos
            tabuleiro.carregarTabuleiroDeJSON(arquivo);
            tabuleiroAdversario.carregarTabuleiroDeJSON(arquivoAdversario);
            tabuleiro.exibirTabuleiro("Tabuleiro do Jogador");
            tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");
            List<String> minhasPosições = batalha.extrairPosicoes(arquivo);
            List<String> adversarioPosições = batalha.extrairPosicoes(arquivoAdversario);


            new Thread(() -> {
                while (running) {
                    try {
                        if (!isMyTurn) {
                            String message = comunicacao.receiveMessage();
                            if (message != null) {
                                if (message.equals("END_TURN")) {
                                    isMyTurn = true;
                                    System.out.println("Sua vez de jogar!");
                                    continue;
                                }

                                System.out.println("Recebido: " + message);
                                boolean verificador = comunicacao.vefificadorMensagem(message);

                                if (verificador) {
                                    tiroAdversario.add(message);
                                    boolean status1 = batalha.tiroComparaPosicao(minhasPosições, tiroAdversario);

                                    if (status1) {
                                        System.out.println("Todas as posições atingidas. Você perdeu!");
                                        running = false;
                                        break;
                                    }

                                    String info = batalha.respostaTiro(minhasPosições, message);
                                    tabuleiro.atualizacaoStausTabela(message, info);
                                    tabuleiro.exibirTabuleiro("Tabuleiro do Jogador");
                                } else {
                                    System.out.println("Mensagem inválida recebida!");
                                }
                            }
                        } else {
                            Thread.sleep(100); // Evitar consumo excessivo de CPU
                        }
                    } catch (Exception e) {
                        System.out.println("Erro na recepção: " + e.getMessage());
                        running = false;
                    }
                }
            }).start();

            while (running) {
                if (isMyTurn) {
                    System.out.println("Envie a posição de tiro: ");
                    String message = scanner.nextLine();
                    if (message.equalsIgnoreCase("exit")) {
                        running = false;
                        break;
                    }

                    boolean verificador = comunicacao.vefificadorMensagem(message);
                    if (verificador) {
                        comunicacao.sendMessage(message);
                        tiroAtaque.add(message);

                        boolean status2 = batalha.tiroComparaPosicao(adversarioPosições, tiroAtaque);
                        if (status2) {
                            System.out.println("Você ganhou! Todas as posições inimigas foram atingidas.");
                            running = false;
                            break;
                        }

                        String info = batalha.respostaTiro(adversarioPosições, message);
                        tabuleiroAdversario.atualizacaoStausTabela(message, info);
                        tabuleiro.exibirTabuleiro("Tabuleiro do Jogador");
                        tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");

                        // Turno encerrado
                        isMyTurn = false;
                        comunicacao.sendMessage("END_TURN");
                    } else {
                        System.out.println("Mensagem inválida. Tente novamente.");
                    }
                } else {
                    System.out.println("Aguarde sua vez...");
                    Thread.sleep(100);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Conexão encerrada.");
            comunicacao.closeConnection();
        }
    }
} //enviocco