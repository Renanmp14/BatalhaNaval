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

    /*
    public void sendFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado: " + filePath);
            return;
        }

        // Envia o nome do arquivo
       // writer.println(file.getName());

        // Envia o conteúdo do arquivo
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = fileReader.readLine()) != null) {
            writer.println(line);
        }
        //writer.println("EOF");
        fileReader.close();
        System.out.println("Arquivo enviado: " + file.getName());
    }
    */


    public void sendFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado: " + filePath);
            return;
        }

        // Envia o nome do arquivo (opcional)
        // writer.println(file.getName());

        // Envia o conteúdo do arquivo como uma única string
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        StringBuilder fileContent = new StringBuilder();
        String line;

        // Lê todo o conteúdo do arquivo e acumula na StringBuilder
        while ((line = fileReader.readLine()) != null) {
            fileContent.append(line); // Acumula as linhas
        }
        fileReader.close();

        // Envia o conteúdo inteiro do arquivo em uma única string
        writer.println(fileContent.toString());  // Envia a string completa

        // Exibe uma mensagem de sucesso
        System.out.println("Arquivo enviado: " + file.getName());
    }

    // Recebe um arquivo e o salva no caminho especificado
    /*
    public void receiveFile(String saveDirectory) throws IOException {

        String fileName = "ArquivoAdversario.json";


        File file = new File(saveDirectory, fileName);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));

        // Lê o conteúdo do arquivo
        String line;
        while ((line = reader.readLine()) != null) { //(line = reader.readLine()) != null) //!(line = reader.readLine()).equals("EOF")
            fileWriter.write(line);
            fileWriter.newLine();
        }
        fileWriter.close();
        System.out.println("Arquivo recebido e salvo como: " + file.getPath());
    }
    */
    public void receiveFile(String saveDirectory) throws IOException {
        String fileName = "ArquivoAdversario.json";
        File file = new File(saveDirectory, fileName);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));

        // StringBuilder para armazenar o conteúdo do arquivo recebido
        StringBuilder fileContent = new StringBuilder();

        // Variável para armazenar as partes do arquivo
        String line;

        // Lê o conteúdo do arquivo até que ele tenha a estrutura completa do JSON
        boolean isJsonComplete = false; // Flag para verificar se o JSON completo foi recebido

        while ((line = reader.readLine()) != null) {
            // Acumula a linha no conteúdo do arquivo
            fileContent.append(line);

            // Verifica se o JSON completo foi recebido
            // A estrutura básica do JSON é que começa com [{ e termina com }]
            String content = fileContent.toString().trim();  // Remover espaços extras no começo e final

            if (content.startsWith("[{") && content.endsWith("}]")) {
                isJsonComplete = true;
                break;  // Se o JSON está completo, sai do laço
            }
        }

        if (isJsonComplete) {
            // Escreve o conteúdo completo no arquivo
            fileWriter.write(fileContent.toString());
            fileWriter.close();
            System.out.println("Arquivo recebido e salvo como: " + file.getPath());
        } else {
            System.out.println("Erro: JSON incompleto ou mal formatado.");
        }
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

    public boolean vefificadorMensagem (String numero){
        return numero.matches("\\d{2}");
    }
}
