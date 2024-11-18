import java.io.*;
import java.net.*;
public class Comunicacao {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ServerSocket serverSocket;
    //Inicia Server
    public void startServer(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado. Aguardando conexões...");
        socket = serverSocket.accept();
        System.out.println("Cliente Conectado!!");
        setupStreams();
    }
    //Inicia cliente
    public void startClient(String ip,int port) throws IOException{
        socket = new Socket(ip,port);
        System.out.println("Conectado ao Servidor");
        setupStreams();
    }
    //configuração streams de entrada e saída
    private void setupStreams() throws IOException{
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(),true);
    }

    public void sendFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado: " + filePath);
            return;
        }

        // Envia o nome do arquivo
        writer.println(file.getName());

        // Envia o conteúdo do arquivo
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = fileReader.readLine()) != null) {
            writer.println(line);
        }
        writer.println("EOF");
        fileReader.close();
        System.out.println("Arquivo enviado: " + file.getName());
    }

    // Recebe um arquivo e o salva no caminho especificado
    public void receiveFile(String saveDirectory) throws IOException {
        // Lê o nome do arquivo enviado
        String fileName = reader.readLine();

        // Cria o arquivo no diretório de destino
        File file = new File(saveDirectory, fileName);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));

        // Lê o conteúdo do arquivo
        String line;
        while (!(line = reader.readLine()).equals("EOF")) {
            fileWriter.write(line);
            fileWriter.newLine();
        }
        fileWriter.close();
        System.out.println("Arquivo recebido e salvo como: " + file.getPath());
    }

    public void sendMessage(String message){
        if (writer != null){
            writer.println(message);
        }
    }

    public String receiveMessage() throws IOException{
        if (reader != null){
            return reader.readLine();
        }
        return null;
    }

    public void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
