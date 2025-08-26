package chat.commands;

import chat.server.ClientHandler;
import chat.utils.ConsoleUtils;

/**
 * JoinCommand - Comando pra entrar numa sala
 * Simples como pão com manteiga, mas pode dar merda igual
 */
public class JoinCommand implements Command {
    
    @Override
    public boolean execute(ClientHandler clientHandler, String[] args) {
        if (args.length < 2) {
            clientHandler.sendMessage("ERRO: Uso correto: /join <nome_da_sala>");
            return false;
        }
        
        String roomName = args[1].toLowerCase().trim();
        
        // Valida nome da sala (básico demais)
        if (roomName.isEmpty() || roomName.length() > 20) {
            clientHandler.sendMessage("ERRO: Nome da sala deve ter entre 1 e 20 caracteres, seu animal!");
            return false;
        }
        
        // Não pode ter caracteres especiais porque somos chatos
        if (!roomName.matches("^[a-zA-Z0-9_-]+$")) {
            clientHandler.sendMessage("ERRO: Nome da sala só pode ter letras, números, _ e -");
            return false;
        }
        
        String oldRoom = clientHandler.getUser().getCurrentRoom();
        
        // Se já tá na sala, pra que sair e entrar de novo?
        if (oldRoom.equals(roomName)) {
            clientHandler.sendMessage("AVISO: Você já está na sala '" + roomName + "', seu esquecido!");
            return true;
        }
        
        // Notifica a sala antiga que o cara vazou
        if (oldRoom != null && !oldRoom.isEmpty()) {
            clientHandler.getServer().broadcastToRoom(
                oldRoom, 
                "SISTEMA: " + clientHandler.getUser().getUsername() + " saiu da sala",
                clientHandler.getUser().getUsername()
            );
        }
        
        // Muda pra nova sala
        clientHandler.getUser().setCurrentRoom(roomName);
        
        // Notifica o usuário
        clientHandler.sendMessage("✓ Você entrou na sala '" + roomName + "'");
        
        // Notifica a nova sala que chegou gente nova
        clientHandler.getServer().broadcastToRoom(
            roomName, 
            "SISTEMA: " + clientHandler.getUser().getUsername() + " entrou na sala",
            clientHandler.getUser().getUsername()
        );
        
        // Lista quem tá na sala pra não ficar perdido
        String usersInRoom = clientHandler.getServer().getUsersInRoom(roomName);
        if (!usersInRoom.isEmpty()) {
            clientHandler.sendMessage("Usuários na sala: " + usersInRoom);
        } else {
            clientHandler.sendMessage("Você está sozinho na sala. Que tristeza!");
        }
        
        ConsoleUtils.printInfo("Usuário " + clientHandler.getUser().getUsername() + 
                              " entrou na sala " + roomName);
        
        return true;
    }
    
    @Override
    public String getCommandName() {
        return "join";
    }
    
    @Override
    public String getDescription() {
        return "Entra numa sala de chat (ou cria se não existir)";
    }
    
    @Override
    public String getUsage() {
        return "/join <nome_da_sala>";
    }
}
