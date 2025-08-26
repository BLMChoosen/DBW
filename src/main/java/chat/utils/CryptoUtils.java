package chat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * CryptoUtils - Utilitários de "segurança" 
 * Spoiler: é mais furado que queijo suíço
 */
public class CryptoUtils {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Gera hash SHA-256 de uma string
     * Básico como água, mas funciona
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(password.getBytes());
            
            // Converte pra hex porque somos retro
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Se não tem SHA-256, o sistema tá mais fudido que nós
            throw new RuntimeException("SHA-256 não disponível, seu sistema é uma bosta!", e);
        }
    }
    
    /**
     * Verifica se a senha bate com o hash
     */
    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
    
    /**
     * Gera salt aleatório (não usado ainda, mas fica aí pro futuro)
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * "Criptografia" ultra-secreta para mensagens
     * É uma ROT13 glorificada, mas finge que é seguro
     */
    public static String encryptMessage(String message) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                encrypted.append((char) ((c - base + 13) % 26 + base));
            } else {
                encrypted.append(c);
            }
        }
        return encrypted.toString();
    }
    
    /**
     * "Descriptografia" da mensagem (é a mesma função, ROT13 é simétrica)
     */
    public static String decryptMessage(String encryptedMessage) {
        return encryptMessage(encryptedMessage); // ROT13 é simétrica, genius!
    }
    
    /**
     * Gera token de sessão aleatório
     */
    public static String generateSessionToken() {
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    /**
     * Valida se o token tem formato válido (básico demais)
     */
    public static boolean isValidToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            Base64.getUrlDecoder().decode(token);
            return token.length() >= 20; // Token muito pequeno é suspeito
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
