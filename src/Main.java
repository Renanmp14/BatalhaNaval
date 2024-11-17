import java.util.Scanner;
public class Main {
    private static final int PORT = 50000;
    private static boolean running = true;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Comunicacao comunicacao = new Comunicacao();

        try {
            System.out.println("Digite 'server' para iniciar como servidor ou 'client' para conectar:");
            String mode = scanner.nextLine();

            if (mode.equalsIgnoreCase("server")) {
                comunicacao.startServer(PORT);
            } else if (mode.equalsIgnoreCase("client")) {
                System.out.print("Digite o IP do servidor: ");
                String ip = scanner.nextLine();
                comunicacao.startClient(ip, PORT);
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