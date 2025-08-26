package chat.commands;

import chat.server.ClientHandler;
import chat.utils.ConsoleUtils;

/**
 * MsgCommand - Comando pra mandar mensagem
 * O coração deste sistema de merda
 */
public class MsgCommand implements Command {
    
    @Override
    public boolean execute(ClientHandler clientHandler, String[] args) {
        if (args.length < 3) {
            clientHandler.sendMessage("ERRO: Uso correto: /msg <usuário|sala> <mensagem>");
            return false;
        }
        
        String target = args[1].toLowerCase().trim();
        
        // Reconstrói a mensagem do resto dos argumentos
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            messageBuilder.append(args[i]);
            if (i < args.length - 1) {
                messageBuilder.append(" ");
            }
        }
        String message = messageBuilder.toString().trim();
        
        if (message.isEmpty()) {
            clientHandler.sendMessage("ERRO: Mensagem não pode ser vazia, seu mudo!");
            return false;
        }
        
        // Limita tamanho da mensagem porque spam é chato
        if (message.length() > 500) {
            clientHandler.sendMessage("ERRO: Mensagem muito longa! Máximo 500 caracteres, escreve um livro não!");
            return false;
        }
        
        String senderName = clientHandler.getUser().getUsername();
        
        // Verifica se é mensagem privada (pra usuário específico)
        if (clientHandler.getServer().isUserOnline(target)) {
            // Mensagem privada
            String privateMessage = "[PRIVADA] " + senderName + ": " + message;
            
            if (clientHandler.getServer().sendPrivateMessage(target, privateMessage)) {
                clientHandler.sendMessage("✓ Mensagem privada enviada para " + target);
                ConsoleUtils.printInfo("Mensagem privada: " + senderName + " -> " + target);
            } else {
                clientHandler.sendMessage("ERRO: Não foi possível enviar mensagem para " + target);
            }
            
        } else {
            // Assume que é pra uma sala
            String roomMessage = senderName + ": " + message;
            
            // Se não especificou sala, usa a sala atual
            if (!clientHandler.getServer().roomExists(target)) {
                target = clientHandler.getUser().getCurrentRoom();
                if (target == null || target.isEmpty()) {
                    clientHandler.sendMessage("ERRO: Você não está em nenhuma sala! Use /join <sala> primeiro");
                    return false;
                }
            }
            
            // Envia mensagem pra sala
            int recipients = clientHandler.getServer().broadcastToRoom(target, roomMessage, senderName);
            
            if (recipients > 0) {
                ConsoleUtils.printChatMessage(senderName, target, message);
            } else {
                clientHandler.sendMessage("AVISO: Ninguém mais na sala '" + target + "' pra receber sua mensagem :(");
            }
        }
        
        return true;
    }
    
    @Override
    public String getCommandName() {
        return "msg";
    }
    
    @Override
    public String getDescription() {
        return "Envia mensagem para usuário ou sala";
    }
    
    @Override
    public String getUsage() {
        return "/msg <usuário|sala> <mensagem>";
    }
}
