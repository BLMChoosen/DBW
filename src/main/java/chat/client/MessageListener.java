package chat.client;

import chat.utils.ConsoleUtils;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * MessageListener - Thread que fica escutando mensagens do servidor
 * Porque ninguém gosta de perder conversa
 */
public class MessageListener extends Thread {
    private BufferedReader in;
    private ChatClient client;
    private boolean isListening;
    
    public MessageListener(BufferedReader in, ChatClient client) {
        this.in = in;
        this.client = client;
        this.isListening = false;
        this.setDaemon(true); // Thread daemon pra não travar o programa
    }
    
    @Override
    public void run() {
        isListening = true;
        
        try {
            String message;
            while (isListening && (message = in.readLine()) != null) {
                // Processa mensagem recebida
                processMessage(message);
            }
            
        } catch (IOException e) {
            if (isListening) {
                // Só mostra erro se ainda tava escutando
                ConsoleUtils.printError("Erro ao receber mensagem: " + e.getMessage());
                client.onConnectionLost();
            }
        } finally {
            stopListening();
        }
    }
    
    /**
     * Processa mensagem recebida do servidor
     */
    private void processMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        
        // Formatação especial pra diferentes tipos de mensagem
        if (message.startsWith("ERRO:")) {
            ConsoleUtils.printError(message.substring(5).trim());
        } else if (message.startsWith("AVISO:")) {
            ConsoleUtils.printWarning(message.substring(6).trim());
        } else if (message.startsWith("SISTEMA:")) {
            ConsoleUtils.printSystemMessage(message.substring(8).trim());
        } else if (message.startsWith("✓")) {
            ConsoleUtils.printSuccess(message.substring(1).trim());
        } else if (message.contains("[PRIVADA]")) {
            // Mensagem privada em cor especial
            System.out.printf("%s[PRIVADA] %s%s%n", 
                             ConsoleUtils.PURPLE, 
                             message.replace("[PRIVADA]", "").trim(), 
                             ConsoleUtils.RESET);
        } else if (message.contains(":") && !message.startsWith("===")) {
            // Mensagem de chat normal - tenta extrair usuário e mensagem
            int colonIndex = message.indexOf(":");
            if (colonIndex > 0 && colonIndex < message.length() - 1) {
                String username = message.substring(0, colonIndex).trim();
                String chatMessage = message.substring(colonIndex + 1).trim();
                
                // Se tem formato de sala [sala]
                if (username.contains("[") && username.contains("]")) {
                    // Extrai sala e usuário
                    int startBracket = username.indexOf("[");
                    int endBracket = username.indexOf("]");
                    if (startBracket >= 0 && endBracket > startBracket) {
                        String room = username.substring(startBracket + 1, endBracket);
                        String user = username.substring(endBracket + 1).trim();
                        ConsoleUtils.printChatMessage(user, room, chatMessage);
                    } else {
                        // Formato simples usuário: mensagem
                        ConsoleUtils.printChatMessage(username, "geral", chatMessage);
                    }
                } else {
                    // Formato simples usuário: mensagem
                    ConsoleUtils.printChatMessage(username, "geral", chatMessage);
                }
            } else {
                // Mensagem sem formato especial
                ConsoleUtils.printWithTimestamp(message);
            }
        } else {
            // Mensagem genérica
            client.onMessageReceived(message);
        }
    }
    
    /**
     * Para de escutar mensagens
     */
    public void stopListening() {
        isListening = false;
    }
    
    /**
     * Verifica se ainda tá escutando
     */
    public boolean isListening() {
        return isListening;
    }
}
