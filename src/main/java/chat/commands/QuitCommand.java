package chat.commands;

import chat.server.ClientHandler;
import chat.utils.ConsoleUtils;

/**
 * QuitCommand - Comando pra sair dessa merda
 * Talvez o comando mais útil de todos
 */
public class QuitCommand implements Command {
    
    @Override
    public boolean execute(ClientHandler clientHandler, String[] args) {
        String username = clientHandler.getUser().getUsername();
        String currentRoom = clientHandler.getUser().getCurrentRoom();
        
        // Mensagem de despedida personalizada
        String[] farewellMessages = {
            " vazou dessa bosta!",
            " foi pra um lugar melhor (qualquer lugar é melhor).",
            " não aguentou mais essa merda.",
            " decidiu ter uma vida social de verdade.",
            " foi fazer algo mais produtivo.",
            " saiu correndo gritando."
        };
        
        int randomIndex = (int) (Math.random() * farewellMessages.length);
        String farewellMessage = "SISTEMA: " + username + farewellMessages[randomIndex];
        
        // Notifica a sala atual se tiver em alguma
        if (currentRoom != null && !currentRoom.isEmpty()) {
            clientHandler.getServer().broadcastToRoom(
                currentRoom, 
                farewellMessage,
                username
            );
        }
        
        // Mensagem pro próprio usuário
        clientHandler.sendMessage("✓ Tchau! Obrigado por usar essa bosta do DBW!");
        clientHandler.sendMessage("✓ Volte sempre... ou não, tanto faz.");
        
        ConsoleUtils.printInfo("Usuário " + username + " desconectou");
        
        // Força desconexão
        clientHandler.disconnect();
        
        return true;
    }
    
    @Override
    public String getCommandName() {
        return "quit";
    }
    
    @Override
    public String getDescription() {
        return "Sai do chat (finalmente!)";
    }
    
    @Override
    public String getUsage() {
        return "/quit";
    }
}
