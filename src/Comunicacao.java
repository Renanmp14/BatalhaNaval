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
