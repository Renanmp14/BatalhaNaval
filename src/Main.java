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
                        //if (!isMyTurn) {
                        String message = comunicacao.receiveMessage();
                        if (message != null) {
                            System.out.println("Recebido: " + message);
                            boolean verificador = comunicacao.vefificadorMensagem(message);
                            if (verificador == true) {
                                tiroAdversario.add(message);
                                boolean status1 = batalha.tiroComparaPosicao(minhasPosições, tiroAdversario);

                                if (status1 == true) {
                                    System.out.println("Todas as posições atingidas. Você perdeu!");
                                    //System.out.println("Precione escreva 'exit' para sair!!");
                                    running = false;
                                    break;
                                }
                                if (status1 == false) {
                                    //System.out.println("Jogo continua");
                                    //colocar as atualizações de tabela
                                    String info = batalha.respostaTiro(minhasPosições, message);
                                    tabuleiro.atualizacaoStausTabela(message, info);
                                    tabuleiro.exibirTabuleiro("Tabuleiro do Jogador");
                                    tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");

                                }
                            } else {
                                System.out.println("Mensagem Ignorada pelas regras do Jogo!!");
                            }
                        }
                        //}
                        else{
                            System.out.println("Aguarde sua Vez");
                        }
                    } catch (Exception e) {
                        System.out.println("Conexão encerrada.");
                        break;
                    }
                }
            }).start();
            while (running) {
                if (scanner.hasNextLine()) {
                    System.out.println("Envie a posição de tiro: ");
                    String message = scanner.nextLine();
                    if (message.equalsIgnoreCase("exit")) {
                        running = false;
                        break;
                    }
                    boolean verificador = comunicacao.vefificadorMensagem(message);
                    if (verificador == true) {
                        comunicacao.sendMessage(message);
                        tiroAtaque.add(message);
                        boolean status2 = batalha.tiroComparaPosicao(adversarioPosições, tiroAtaque);

                        if (status2 == true) {
                            System.out.println("Todas as posições atingidas. Você Ganhou, Vamos Caralho!");
                            //System.out.println("Precione escreva 'exit' para sair!!");
                            running = false;
                            break;
                        }
                        if (status2 == false) {
                            //colocar as atualizações de tabela
                            String info = batalha.respostaTiro(adversarioPosições, message);
                            tabuleiroAdversario.atualizacaoStausTabela(message, info);
                            tabuleiro.exibirTabuleiro("Tabuleiro do Jogador");
                            tabuleiroAdversario.exibirTabuleiro("Tabuleiro do Adversário");
                        }
                    } else {
                        System.out.println("Mensagem fora do padrão, não será enviada");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Conexão encerrada.");
            comunicacao.closeConnection();
        }
    }
}