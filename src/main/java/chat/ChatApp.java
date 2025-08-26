package chat;

import chat.client.ChatClient;
import chat.server.ChatServer;
import chat.utils.ConsoleUtils;

/**
 * ChatApp - Classe principal do DBW
 * Ponto de entrada pra essa bosta toda
 */
public class ChatApp {
    
    public static void main(String[] args) {
        // Configura shutdown hook pra fechar tudo direitinho
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            ConsoleUtils.printInfo("Encerrando DBW... Obrigado por usar essa bosta!");
        }));
        
        if (args.length == 0) {
            showUsage();
            return;
        }
        
        String mode = args[0].toLowerCase();
        
        switch (mode) {
            case "server":
                runServer(args);
                break;
            case "client":
                runClient(args);
                break;
            default:
                ConsoleUtils.printError("Modo inválido: " + mode);
                showUsage();
        }
    }
    
    /**
     * Executa o servidor
     */
    private static void runServer(String[] args) {
        int port = 25576; // Porta padrão atualizada
        
        // Verifica se foi especificada porta customizada
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
                if (port < 1024 || port > 65535) {
                    ConsoleUtils.printError("Porta deve estar entre 1024 e 65535!");
                    return;
                }
            } catch (NumberFormatException e) {
                ConsoleUtils.printError("Porta inválida: " + args[1]);
                return;
            }
        }
        
        ChatServer server = new ChatServer(port);
        
        try {
            server.start();
        } catch (Exception e) {
            ConsoleUtils.printError("Erro fatal no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Executa o cliente
     */
    private static void runClient(String[] args) {
        String host = "localhost";
        int port = 25576;
        
        // Verifica argumentos opcionais: host e porta
        if (args.length > 1) {
            host = args[1];
        }
        
        if (args.length > 2) {
            try {
                port = Integer.parseInt(args[2]);
                if (port < 1024 || port > 65535) {
                    ConsoleUtils.printError("Porta deve estar entre 1024 e 65535!");
                    return;
                }
            } catch (NumberFormatException e) {
                ConsoleUtils.printError("Porta inválida: " + args[2]);
                return;
            }
        }
        
        ChatClient client = new ChatClient(host, port);
        
        try {
            client.start();
        } catch (Exception e) {
            ConsoleUtils.printError("Erro fatal no cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Mostra como usar o programa
     */
    private static void showUsage() {
        ConsoleUtils.printBanner();
        
        System.out.println("Uso:");
        System.out.println("  java -jar dbw.jar server [porta]");
        System.out.println("  java -jar dbw.jar client [host] [porta]");
        System.out.println();
        System.out.println("Exemplos:");
        System.out.println("  java -jar dbw.jar server          # Servidor na porta 25576");
        System.out.println("  java -jar dbw.jar server 9999     # Servidor na porta 9999");
        System.out.println("  java -jar dbw.jar client          # Cliente conecta em localhost:25576");
        System.out.println("  java -jar dbw.jar client 192.168.1.100 9999  # Conecta em IP específico");
        System.out.println();
        System.out.println("Com Gradle:");
        System.out.println("  ./gradlew runServer               # Inicia servidor");
        System.out.println("  ./gradlew runClient               # Inicia cliente");
        System.out.println();
        System.out.println("Alternativa no Windows:");
        System.out.println("  .\\gradlew.bat runServer");
        System.out.println("  .\\gradlew.bat runClient");
        System.out.println();
        
        ConsoleUtils.printWarning("LEMBRE-SE: Isso é uma bosta e assumimos sem vergonha!");
        ConsoleUtils.printInfo("Divirta-se quebrando tudo! :)");
    }
}
