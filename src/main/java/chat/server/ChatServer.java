package chat.server;

import chat.utils.ConsoleUtils;
import chat.utils.CryptoUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ChatServer - O servidor principal dessa bosta
 * Coordena todo o caos e tenta não explodir
 */
public class ChatServer {
    private static final int DEFAULT_PORT = 25576;
    private static final String USERS_FILE = "src/main/resources/users.txt";
    
    private ServerSocket serverSocket;
    private boolean isRunning;
    private int port;
    
    // Estruturas de dados thread-safe porque concorrência é foda
    private List<ClientHandler> onlineClients;
    private Map<String, User> registeredUsers;
    private Map<String, List<ClientHandler>> roomClients;
    
    public ChatServer(int port) {
        this.port = port;
        this.isRunning = false;
        this.onlineClients = new CopyOnWriteArrayList<>();
        this.registeredUsers = new ConcurrentHashMap<>();
        this.roomClients = new ConcurrentHashMap<>();
        
        loadUsersFromFile();
    }
    
    public ChatServer() {
        this(DEFAULT_PORT);
    }
    
    /**
     * Inicia o servidor e fica escutando conexões
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            
            ConsoleUtils.printBanner();
            ConsoleUtils.printSuccess("Servidor DBW iniciado na porta " + port);
            ConsoleUtils.printInfo("Pressione Ctrl+C para parar o servidor");
            ConsoleUtils.printInfo("Aguardando conexões...");
            
            // Loop principal do servidor
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    
                    // Cria thread pra cada cliente
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    clientHandler.start();
                    
                } catch (IOException e) {
                    if (isRunning) {
                        ConsoleUtils.printError("Erro ao aceitar conexão: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            ConsoleUtils.printError("Erro ao iniciar servidor: " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    /**
     * Para o servidor e fecha tudo
     */
    public void stop() {
        isRunning = false;
        
        ConsoleUtils.printInfo("Parando servidor...");
        
        // Desconecta todos os clientes
        for (ClientHandler client : onlineClients) {
            client.sendMessage("SERVIDOR: Servidor está sendo desligado. Tchau!");
            client.disconnect();
        }
        
        // Salva usuários no arquivo
        saveUsersToFile();
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            ConsoleUtils.printError("Erro ao fechar servidor: " + e.getMessage());
        }
        
        ConsoleUtils.printSuccess("Servidor parado. Obrigado por usar essa bosta!");
    }
    
    /**
     * Carrega usuários do arquivo texto
     */
    private void loadUsersFromFile() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            ConsoleUtils.printWarning("Arquivo de usuários não encontrado. Será criado quando necessário.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignora comentários e linhas vazias
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                    String username = parts[0];
                    String passwordHash = parts[1];
                    String currentRoom = parts[2];
                    
                    User user = new User(username, passwordHash, currentRoom);
                    registeredUsers.put(username, user);
                    count++;
                }
            }
            
            ConsoleUtils.printSuccess("Carregados " + count + " usuários do arquivo");
            
        } catch (IOException e) {
            ConsoleUtils.printError("Erro ao carregar usuários: " + e.getMessage());
        }
    }
    
    /**
     * Salva usuários no arquivo texto
     */
    private void saveUsersToFile() {
        File file = new File(USERS_FILE);
        
        // Cria diretório se não existir
        file.getParentFile().mkdirs();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# Arquivo de usuários do DBW - Discord But Worse");
            writer.println("# Formato: username:senha_hash:sala_atual");
            writer.println("# Não delete essa porra, senão tudo explode");
            writer.println();
            
            for (User user : registeredUsers.values()) {
                writer.printf("%s:%s:%s%n", 
                            user.getUsername(), 
                            user.getPasswordHash(), 
                            user.getCurrentRoom());
            }
            
            ConsoleUtils.printInfo("Usuários salvos no arquivo");
            
        } catch (IOException e) {
            ConsoleUtils.printError("Erro ao salvar usuários: " + e.getMessage());
        }
    }
    
    /**
     * Autentica usuário com username e senha
     */
    public User authenticateUser(String username, String password) {
        User user = registeredUsers.get(username.toLowerCase());
        if (user != null && CryptoUtils.verifyPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }
    
    /**
     * Registra novo usuário
     */
    public void registerUser(User user) {
        registeredUsers.put(user.getUsername().toLowerCase(), user);
        saveUsersToFile(); // Salva imediatamente
        ConsoleUtils.printInfo("Novo usuário registrado: " + user.getUsername());
    }
    
    /**
     * Adiciona cliente online
     */
    public void addOnlineUser(ClientHandler client) {
        onlineClients.add(client);
        
        String room = client.getUser().getCurrentRoom();
        if (room != null && !room.isEmpty()) {
            roomClients.computeIfAbsent(room, k -> new CopyOnWriteArrayList<>()).add(client);
        }
        
        ConsoleUtils.printInfo("Usuário online: " + client.getUser().getUsername() + 
                              " (Total: " + onlineClients.size() + ")");
    }
    
    /**
     * Remove cliente online
     */
    public void removeOnlineUser(ClientHandler client) {
        onlineClients.remove(client);
        
        // Remove de todas as salas
        for (List<ClientHandler> roomUsers : roomClients.values()) {
            roomUsers.remove(client);
        }
        
        if (client.getUser() != null) {
            ConsoleUtils.printInfo("Usuário offline: " + client.getUser().getUsername() + 
                                  " (Total: " + onlineClients.size() + ")");
        }
    }
    
    /**
     * Verifica se usuário está online
     */
    public boolean isUserOnline(String username) {
        return onlineClients.stream()
                           .anyMatch(client -> client.getUser().getUsername().equalsIgnoreCase(username));
    }
    
    /**
     * Envia mensagem para todos os usuários de uma sala
     */
    public int broadcastToRoom(String roomName, String message, String excludeUser) {
        List<ClientHandler> roomUsers = roomClients.computeIfAbsent(roomName, k -> new CopyOnWriteArrayList<>());
        
        // Remove usuários desconectados
        roomUsers.removeIf(client -> !client.isConnected());
        
        // Adiciona usuários que entraram na sala mas não estão na lista
        for (ClientHandler client : onlineClients) {
            if (client.getUser().getCurrentRoom().equals(roomName) && !roomUsers.contains(client)) {
                roomUsers.add(client);
            }
        }
        
        int count = 0;
        for (ClientHandler client : roomUsers) {
            if (excludeUser == null || !client.getUser().getUsername().equals(excludeUser)) {
                client.sendMessage(message);
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Envia mensagem privada para usuário específico
     */
    public boolean sendPrivateMessage(String targetUsername, String message) {
        for (ClientHandler client : onlineClients) {
            if (client.getUser().getUsername().equalsIgnoreCase(targetUsername)) {
                client.sendMessage(message);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verifica se sala existe (tem pelo menos um usuário)
     */
    public boolean roomExists(String roomName) {
        List<ClientHandler> roomUsers = roomClients.get(roomName);
        return roomUsers != null && !roomUsers.isEmpty();
    }
    
    /**
     * Retorna lista de usuários online
     */
    public String getOnlineUsersList() {
        if (onlineClients.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < onlineClients.size(); i++) {
            if (i > 0) sb.append(", ");
            ClientHandler client = onlineClients.get(i);
            sb.append(client.getUser().getUsername())
              .append(" (")
              .append(client.getUser().getCurrentRoom())
              .append(")");
        }
        return sb.toString();
    }
    
    /**
     * Retorna lista de usuários em uma sala específica
     */
    public String getUsersInRoom(String roomName) {
        List<ClientHandler> roomUsers = roomClients.get(roomName);
        if (roomUsers == null || roomUsers.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < roomUsers.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(roomUsers.get(i).getUser().getUsername());
        }
        return sb.toString();
    }
    
    /**
     * Retorna lista de salas ativas
     */
    public String getActiveRoomsList() {
        if (roomClients.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, List<ClientHandler>> entry : roomClients.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                if (!first) sb.append(", ");
                sb.append(entry.getKey()).append(" (").append(entry.getValue().size()).append(")");
                first = false;
            }
        }
        return sb.toString();
    }
    
    // Getters
    public boolean isRunning() {
        return isRunning;
    }
    
    public int getPort() {
        return port;
    }
    
    public int getOnlineUsersCount() {
        return onlineClients.size();
    }
}
