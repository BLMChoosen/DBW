package chat.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ConsoleUtils - Utilitários pra fazer o console menos feio
 * Spoiler: ainda vai continuar uma porcaria
*/
public class ConsoleUtils {
    
    // Cores ANSI porque vida sem cor é depressão pura
    // https://www.w3schools.blog/ansi-colors-java 
    // Se quiser mudar
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // Estilo bold pra dar uma de importante
    public static final String BOLD = "\u001B[1m";
    
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    

     // Printa uma mensagem com as o horário, pq ninguém lembra que horas é

    public static void printWithTimestamp(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        System.out.printf("[%s] %s%n", timestamp, message);
    }
    
    
    // Printa mensagem de erro em vermelho pra assustar
     
    public static void printError(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        System.out.printf("%s[%s] ERRO: %s%s%n", RED, timestamp, message, RESET);
    }
    
    
     //Printa mensagem de sucesso em verde pra dar esperança
    
    public static void printSuccess(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        System.out.printf("%s[%s] ✓ %s%s%n", GREEN, timestamp, message, RESET);
    }
    
     //Printa warning em amarelo pra dar ansiedade

    public static void printWarning(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        System.out.printf("%s[%s] ⚠ %s%s%n", YELLOW, timestamp, message, RESET);
    }
    

    //Printa info em azul pra fingir que é profissional
     
    public static void printInfo(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        System.out.printf("%s[%s] ℹ %s%s%n", BLUE, timestamp, message, RESET);
    }
    
    
    //Printa mensagem de chat com formatação especial
     
    public static void printChatMessage(String username, String room, String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        System.out.printf("%s[%s] %s[%s]%s %s%s%s: %s%n", 
                         CYAN, timestamp, BOLD, room, RESET, 
                         GREEN, username, RESET, message);
    }
    

     //Printa mensagem do sistema em roxo pra parecer bonito

    public static void printSystemMessage(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        System.out.printf("%s[%s] SISTEMA: %s%s%n", PURPLE, timestamp, message, RESET);
    }
    
   
    //Limpa a tela (ou tenta, pelo menos)

    public static void clearScreen() {
        try {
            // No Windows, usa cls
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // No Linux/Mac, usa clear
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Se não conseguir limpar, azar, printa umas linhas vazias
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    
     //Printa o banner do DBW porque somos narcisistas

    public static void printBanner() {
        System.out.println(BOLD + CYAN);
        System.out.println("==========================================");
        System.out.println("                  DBW                   ");
        System.out.println("           Discord But Worse           ");
        System.out.println("                                        ");
        System.out.println("  Uma bosta e assumimos sem vergonha   ");
        System.out.println("==========================================");
        System.out.println(RESET);
    }
    
    
    //Printa help dos comandos

    public static void printHelp() {
        System.out.println(YELLOW + "Comandos disponíveis:" + RESET);
        System.out.println("  /join <sala>           - Entra numa sala");
        System.out.println("  /msg <user/sala> <msg> - Manda mensagem");
        System.out.println("  /quit                  - Sai dessa merda");
        System.out.println("  /help                  - Mostra essa ajuda");
        System.out.println("  /users                 - Lista usuários online");
        System.out.println("  /rooms                 - Lista salas");
    }
}
