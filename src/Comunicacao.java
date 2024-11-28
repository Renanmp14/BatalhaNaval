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

        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        StringBuilder fileContent = new StringBuilder();
        String line;

        while ((line = fileReader.readLine()) != null) {
            fileContent.append(line);
        }
        fileReader.close();

        // Envia o conteúdo inteiro do arquivo em uma única string
        writer.println(fileContent.toString());  // Envia a string completa

        System.out.println("Arquivo enviado: " + file.getName());
    }


    public void receiveFile(String saveDirectory) throws IOException {
        String fileName = "ArquivoAdversario.json";
        File file = new File(saveDirectory, fileName);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));

        StringBuilder fileContent = new StringBuilder();

        String line;

        // Lê o conteúdo do arquivo até que ele tenha a estrutura completa do JSON
        boolean isJsonComplete = false;

        while ((line = reader.readLine()) != null) {

            fileContent.append(line);

            String content = fileContent.toString().trim();

            if (content.startsWith("[{") && content.endsWith("}]")) {
                isJsonComplete = true;
                break;  // Se o JSON está completo, sai do laço
            }
        }

        if (isJsonComplete) {
            fileWriter.write(fileContent.toString());
            fileWriter.close();
            System.out.println("Arquivo recebido e salvo como: " + file.getPath());
        } else {
            System.out.println("Erro: JSON incompleto ou mal formatado.");
        }
    }


    public void sendMessage(String message){
        if (writer != null && !socket.isClosed() && socket.isConnected()){
            writer.println(message);
        }
        else{
            System.out.println("Erro: Conexão Perdida. Mensagem não enviada");
        }
    }

    public String receiveMessage() throws IOException{
        if (reader != null && !socket.isClosed() && socket.isConnected() && reader != null){
            return reader.readLine();
        }
        System.out.println("Erro: Conexão Perdida.");
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

    public boolean vefificadorMensagem (String numero){
        return numero.matches("\\d{2}");
    }

    public void deletarArquivo (String arquivo){
        File file = new File(arquivo);
        if (file.exists()) {
            file.delete();
        }
        else{
            System.out.println("Arquivo não encontrada");
        }
    }

}
