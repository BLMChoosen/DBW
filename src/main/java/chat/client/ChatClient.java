package chat.client;

import chat.utils.ConsoleUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 * ChatClient - Cliente do DBW
 * Conecta no servidor e tenta não morrer no processo
 */
public class ChatClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 25576;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener messageListener;
    private boolean isConnected;
    private String serverHost;
    private int serverPort;
    
    public ChatClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
        this.isConnected = false;
    }
    
    public ChatClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    /**
     * Conecta no servidor
     */
    public boolean connect() {
        try {
            ConsoleUtils.printInfo("Conectando em " + serverHost + ":" + serverPort + "...");
            
            socket = new Socket(serverHost, serverPort);
            socket.setSoTimeout(30000); // 30 segundos timeout
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            isConnected = true;
            
            // Inicia thread pra receber mensagens
            messageListener = new MessageListener(in, this);
            messageListener.start();
            
            ConsoleUtils.printSuccess("Conectado ao servidor DBW!");
            return true;
            
        } catch (IOException e) {
            ConsoleUtils.printError("Erro ao conectar: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inicia o loop principal do cliente
     */
    public void start() {
        if (!connect()) {
            ConsoleUtils.printError("Não foi possível conectar ao servidor!");
            return;
        }
        
        ConsoleUtils.printBanner();
        ConsoleUtils.printInfo("Digite suas mensagens ou comandos (digite /help para ajuda)");
        ConsoleUtils.printInfo("Para sair, digite /quit ou Ctrl+C");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        
        try {
            while (isConnected && scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                
                if (!input.isEmpty()) {
                    sendMessage(input);
                    
                    // Se digitou /quit, para o cliente
                    if (input.equalsIgnoreCase("/quit")) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            ConsoleUtils.printError("Erro na entrada do usuário: " + e.getMessage());
        } finally {
            disconnect();
            scanner.close();
        }
    }
    
    /**
     * Envia mensagem para o servidor
     */
    public void sendMessage(String message) {
        if (isConnected && out != null) {
            out.println(message);
        }
    }
    
    /**
     * Desconecta do servidor
     */
    public void disconnect() {
        isConnected = false;
        
        try {
            if (messageListener != null) {
                messageListener.stopListening();
            }
            
            if (out != null) {
                out.close();
            }
            
            if (in != null) {
                in.close();
            }
            
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            
            ConsoleUtils.printInfo("Desconectado do servidor.");
            
        } catch (IOException e) {
            ConsoleUtils.printError("Erro ao desconectar: " + e.getMessage());
        }
    }
    
    /**
     * Callback chamado quando conexão é perdida
     */
    public void onConnectionLost() {
        if (isConnected) {
            ConsoleUtils.printError("Conexão com servidor perdida!");
            isConnected = false;
        }
    }
    
    /**
     * Callback chamado quando recebe mensagem do servidor
     */
    public void onMessageReceived(String message) {
        // Mensagens do servidor são printadas diretamente
        // com timestamp local
        ConsoleUtils.printWithTimestamp(message);
    }
    
    // Getters
    public boolean isConnected() {
        return isConnected;
    }
    
    public String getServerHost() {
        return serverHost;
    }
    
    public int getServerPort() {
        return serverPort;
    }
}
