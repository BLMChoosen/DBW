package chat.commands;

import chat.server.ClientHandler;

/**
 * Interface Command - Porque todo comando precisa ser executado
 * Design pattern básico pra organizar essa bagunça
 */
public interface Command {
    
    /**
     * Executa o comando
     * @param clientHandler - quem mandou essa merda
     * @param args - argumentos do comando (pode vir qualquer bosta)
     * @return true se executou com sucesso, false se deu merda
     */
    boolean execute(ClientHandler clientHandler, String[] args);
    
    /**
     * Retorna o nome do comando (sem a barra)
     */
    String getCommandName();
    
    /**
     * Retorna a descrição do comando pra help
     */
    String getDescription();
    
    /**
     * Retorna como usar o comando
     */
    String getUsage();
}
