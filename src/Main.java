import java.util.Scanner;

import org.json.JSONArray;
public class Main {
    private static final int PORT = 50000;
    private static boolean running = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Comunicacao comunicacao = new Comunicacao();
        String ipMaquina = "192.168.0.211";
        String arquivo = "navios_" + ipMaquina + "json";
        try {
            System.out.println("Digite 'server' para iniciar como servidor ou 'client' para conectar:");
            String mode = scanner.nextLine();

            if (mode.equalsIgnoreCase("server")) {
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
                System.out.println("Arquivos trocados! Iniciando troca de mensagens...");

            } else {
                System.out.println("Modo inválido! Use 'server' ou 'client'.");
                return;
            }
            // Thread para receber mensagens
            new Thread(() -> {
                while (running) {
                    try {
                        String message = comunicacao.receiveMessage();
                        if (message != null) {
                            System.out.println("Recebido: " + message);
                        }
                    } catch (Exception e) {
                        System.out.println("Conexão encerrada.");
                        break;
                    }
                }
            }).start();

            // Enviar mensagens
            while (running) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    running = false;
                    break;
                }
                comunicacao.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            comunicacao.closeConnection();
        }
    }
}