package chat.server;

import chat.commands.*;
import chat.utils.ConsoleUtils;
import chat.utils.CryptoUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * ClientHandler - Manipula cada cliente conectado
 * Thread individual pra cada desgraçado que se conecta
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ChatServer server;
    private User user;
    private boolean isConnected;
    private String sessionToken;
    
    // Mapa de comandos disponíveis
    private Map<String, Command> commands;
    
    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.isConnected = false;
        this.sessionToken = CryptoUtils.generateSessionToken();
        
        initializeCommands();
        
        try {
            // Timeout de 30 segundos pra não ficar esperando eternamente
            clientSocket.setSoTimeout(30000);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            ConsoleUtils.printError("Erro ao configurar cliente: " + e.getMessage());
        }
    }
    
    private void initializeCommands() {
        commands = new HashMap<>();
        commands.put("join", new JoinCommand());
        commands.put("msg", new MsgCommand());
        commands.put("quit", new QuitCommand());
    }
    
    @Override
    public void run() {
        try {
            ConsoleUtils.printInfo("Nova conexão de: " + clientSocket.getRemoteSocketAddress());
            
            // Processo de autenticação/login
            if (!authenticate()) {
                ConsoleUtils.printWarning("Falha na autenticação para: " + clientSocket.getRemoteSocketAddress());
                return;
            }
            
            isConnected = true;
            sendMessage("✓ Bem-vindo ao DBW, " + user.getUsername() + "!");
            sendMessage("✓ Digite /help para ver os comandos disponíveis.");
            sendMessage("✓ Digite /join <sala> para entrar numa sala.");
            
            // Loop principal de mensagens
            String inputLine;
            while (isConnected && (inputLine = in.readLine()) != null) {
                inputLine = inputLine.trim();
                
                if (inputLine.isEmpty()) {
                    continue;
                }
                
                // Processa comando
                if (inputLine.startsWith("/")) {
                    processCommand(inputLine);
                } else {
                    // Mensagem normal - envia pra sala atual
                    sendToCurrentRoom(inputLine);
                }
            }
            
        } catch (SocketTimeoutException e) {
            ConsoleUtils.printWarning("Cliente " + (user != null ? user.getUsername() : "desconhecido") + " timeout");
        } catch (IOException e) {
            if (isConnected) {
                ConsoleUtils.printError("Erro na comunicação com cliente: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }
    
    private boolean authenticate() {
        try {
            sendMessage("=== DBW - Discord But Worse ===");
            sendMessage("Digite seu username:");
            
            String username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                sendMessage("ERRO: Username não pode ser vazio!");
                return false;
            }
            username = username.trim().toLowerCase();
            
            sendMessage("Digite sua senha:");
            String password = in.readLine();
            if (password == null || password.trim().isEmpty()) {
                sendMessage("ERRO: Senha não pode ser vazia!");
                return false;
            }
            
            // Verifica se usuário existe e senha confere
            User foundUser = server.authenticateUser(username, password);
            if (foundUser != null) {
                // Usuário existente
                if (server.isUserOnline(username)) {
                    sendMessage("ERRO: Usuário já está online! Só pode uma sessão por vez, seu safado!");
                    return false;
                }
                
                this.user = foundUser;
                user.setOnline(true);
                user.setIpAddress(clientSocket.getRemoteSocketAddress().toString());
                
            } else {
                // Novo usuário - registro rápido
                sendMessage("Usuário não encontrado. Criando novo usuário...");
                String hashedPassword = CryptoUtils.hashPassword(password);
                this.user = new User(username, hashedPassword);
                user.setOnline(true);
                user.setIpAddress(clientSocket.getRemoteSocketAddress().toString());
                
                server.registerUser(user);
                sendMessage("✓ Usuário criado com sucesso!");
            }
            
            server.addOnlineUser(this);
            ConsoleUtils.printSuccess("Usuário " + username + " autenticado com sucesso");
            return true;
            
        } catch (IOException e) {
            ConsoleUtils.printError("Erro durante autenticação: " + e.getMessage());
            return false;
        }
    }
    
    private void processCommand(String commandLine) {
        String[] args = commandLine.split("\\s+");
        String commandName = args[0].substring(1).toLowerCase(); // Remove a barra
        
        // Comandos built-in
        switch (commandName) {
            case "help":
                showHelp();
                return;
            case "users":
                showOnlineUsers();
                return;
            case "rooms":
                showRooms();
                return;
        }
        
        // Comandos customizados
        Command command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(this, args);
            } catch (Exception e) {
                sendMessage("ERRO: Falha ao executar comando - " + e.getMessage());
                ConsoleUtils.printError("Erro no comando " + commandName + ": " + e.getMessage());
            }
        } else {
            sendMessage("ERRO: Comando '" + commandName + "' não reconhecido. Digite /help para ver comandos disponíveis.");
        }
    }
    
    private void sendToCurrentRoom(String message) {
        if (user.getCurrentRoom() == null || user.getCurrentRoom().isEmpty()) {
            sendMessage("ERRO: Você não está em nenhuma sala! Use /join <sala> primeiro.");
            return;
        }
        
        String formattedMessage = user.getUsername() + ": " + message;
        int recipients = server.broadcastToRoom(user.getCurrentRoom(), formattedMessage, user.getUsername());
        
        if (recipients == 0) {
            sendMessage("AVISO: Você está sozinho na sala. Que tristeza!");
        }
        
        ConsoleUtils.printChatMessage(user.getUsername(), user.getCurrentRoom(), message);
    }
    
    private void showHelp() {
        sendMessage("=== Comandos DBW ===");
        sendMessage("/help           - Mostra esta ajuda");
        sendMessage("/join <sala>    - Entra numa sala");
        sendMessage("/msg <dest> <msg> - Envia mensagem");
        sendMessage("/users          - Lista usuários online");
        sendMessage("/rooms          - Lista salas ativas");
        sendMessage("/quit           - Sai do chat");
        sendMessage("==================");
        sendMessage("Dica: Digite mensagens normais para falar na sala atual!");
    }
    
    private void showOnlineUsers() {
        String userList = server.getOnlineUsersList();
        if (userList.isEmpty()) {
            sendMessage("Nenhum usuário online (além de você, obviamente).");
        } else {
            sendMessage("Usuários online: " + userList);
        }
    }
    
    private void showRooms() {
        String roomList = server.getActiveRoomsList();
        if (roomList.isEmpty()) {
            sendMessage("Nenhuma sala ativa no momento.");
        } else {
            sendMessage("Salas ativas: " + roomList);
        }
    }
    
    public void sendMessage(String message) {
        if (out != null && !clientSocket.isClosed()) {
            out.println(message);
        }
    }
    
    public void disconnect() {
        isConnected = false;
        
        if (user != null) {
            user.setOnline(false);
            server.removeOnlineUser(this);
        }
        
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            ConsoleUtils.printError("Erro ao fechar conexão: " + e.getMessage());
        }
        
        ConsoleUtils.printInfo("Cliente desconectado: " + 
                              (user != null ? user.getUsername() : "desconhecido"));
    }
    
    // Getters
    public User getUser() {
        return user;
    }
    
    public ChatServer getServer() {
        return server;
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
}
