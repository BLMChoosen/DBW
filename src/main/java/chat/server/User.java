package chat.server;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe User - Representa um usuário nessa bosta de sistema
 * Mais simples que miojo de madrugada
 */
public class User implements Serializable {
    private String username;
    private String passwordHash;
    private String currentRoom;
    private LocalDateTime lastSeen;
    private boolean isOnline;
    private String ipAddress;
    
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.currentRoom = "lobby"; // Todo mundo começa no lobby, fodasse
        this.lastSeen = LocalDateTime.now();
        this.isOnline = false;
        this.ipAddress = "unknown";
    }
    
    public User(String username, String passwordHash, String currentRoom) {
        this(username, passwordHash);
        this.currentRoom = currentRoom;
    }
    
    // Getters e setters básicos porque Java é chato pra caralho
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }
    
    public LocalDateTime getLastSeen() {
        return lastSeen;
    }
    
    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
    
    public void setOnline(boolean online) {
        isOnline = online;
        if (online) {
            lastSeen = LocalDateTime.now();
        }
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    @Override
    public String toString() {
        return String.format("User{username='%s', room='%s', online=%s, lastSeen=%s}", 
                           username, currentRoom, isOnline, lastSeen);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }
    
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
