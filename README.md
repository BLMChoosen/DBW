# DBW (Discord But Worse)

A terminal-based multi-client chat application built with Java, demonstrating practical implementations of TCP socket programming, multi-threading, and client-server architecture.

## Overview

DBW is a lightweight chat server and client system that runs entirely in the terminal. The project started as a learning experiment to understand network programming fundamentals and gradually evolved into a feature-complete chat application with room-based conversations, private messaging, and user authentication.

This is an educational project focused on demonstrating core networking concepts without the overhead of frameworks or external dependencies (beyond the Java standard library).

## Why This Project Exists

This project was created to:
- Gain hands-on experience with TCP socket programming in Java
- Explore multi-threaded server architecture and concurrency patterns
- Implement the Command design pattern for extensible feature development
- Build a complete client-server application from scratch
- Learn about user authentication and basic cryptography (SHA-256 hashing)

It serves as both a functional chat system and a reference implementation for others learning similar concepts.

## Tech Stack

- **Java 21** - Core language with modern language features
- **Gradle** - Build automation and dependency management
- **TCP Sockets** - Network communication layer
- **Multi-threading** - Concurrent client handling with thread-safe data structures
- **SHA-256** - Password hashing for user authentication
- **ANSI Escape Codes** - Colorized terminal output

## Features

- ✅ **Multi-threaded TCP Server** - Handles multiple concurrent client connections
- ✅ **User Authentication** - SHA-256 password hashing with automatic registration
- ✅ **Dynamic Chat Rooms** - Create and join rooms on-the-fly
- ✅ **Private Messaging** - Direct messages between users
- ✅ **Command System** - Extensible command pattern implementation: `/join`, `/msg`, `/quit`, `/help`, `/users`, `/rooms`
- ✅ **Persistent User Storage** - Text-based user database
- ✅ **Colorized Terminal UI** - Enhanced readability with ANSI color codes
- ✅ **Graceful Shutdown** - Proper resource cleanup and connection handling

## Architecture

The project follows a clean package structure with separation of concerns:

```
DBW/
├─ chat/
│  ├─ server/           # Server-side components
│  │  ├─ ChatServer     # Main server orchestration
│  │  ├─ ClientHandler  # Individual client thread handler
│  │  └─ User           # User data model
│  ├─ client/           # Client-side components
│  │  ├─ ChatClient     # Main client logic
│  │  └─ MessageListener # Async message receiver thread
│  ├─ commands/         # Command pattern implementations
│  │  ├─ Command        # Base command interface
│  │  ├─ JoinCommand    # Room joining logic
│  │  ├─ MsgCommand     # Private messaging
│  │  └─ QuitCommand    # Graceful disconnection
│  ├─ utils/            # Utility classes
│  │  ├─ ConsoleUtils   # Terminal formatting
│  │  └─ CryptoUtils    # Hashing and security
│  └─ ChatApp           # Application entry point
└─ resources/
   └─ users.txt         # Persistent user storage
```

**Key Design Decisions:**

- **Thread-safe Collections**: Uses `ConcurrentHashMap` and `CopyOnWriteArrayList` to handle concurrent access safely
- **Command Pattern**: Extensible architecture for adding new commands without modifying core server logic
- **Separation of I/O and Business Logic**: Clean boundaries between network operations and application logic

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle (included via wrapper)

### Building the Project

```bash
# Build everything and create standalone JARs
./gradlew buildAll

# Or build server and client separately
./gradlew build serverJar clientJar
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

### Running the Server

```bash
# Start server on default port (25576)
java -jar build/libs/DBW-Server-Standalone-1.0-server.jar server

# Start server on custom port
java -jar build/libs/DBW-Server-Standalone-1.0-server.jar server 9999
```

Or use Gradle:
```bash
./gradlew runServer
```

### Running the Client

```bash
# Connect to localhost:25576
java -jar build/libs/DBW-Client-Standalone-1.0-client.jar client

# Connect to remote server
java -jar build/libs/DBW-Client-Standalone-1.0-client.jar client 192.168.1.100 9999
```

Or use Gradle:
```bash
./gradlew runClient
```

## Example Usage

Once connected, you can interact with the chat system using these commands:

```
> /join general
✓ You joined room 'general'

> Hello everyone!
admin: Hello everyone!

> /msg alice Hey, can we talk privately?
✓ Private message sent to alice

> /users
Online users: admin (general), alice (general), bob (lobby)

> /rooms
Active rooms: general (2), lobby (1)

> /quit
✓ Goodbye! Thanks for using DBW!
```

## Performance / Engineering Highlights

- **Concurrent Client Handling**: Each client runs in a dedicated thread with proper resource management
- **Non-blocking Message Broadcasting**: Efficient room-based message distribution using thread-safe collections
- **Zero External Dependencies**: Built entirely with Java standard library (excluding test frameworks)
- **Graceful Degradation**: Handles client disconnections and network errors without crashing the server
- **Memory Efficient**: Text-based user storage keeps memory footprint minimal

The server has been tested with multiple concurrent clients without performance degradation or race conditions.

## What I Learned

- **Network Programming**: Deep understanding of TCP socket creation, management, and bidirectional communication
- **Concurrency**: Practical experience with multi-threading, synchronization, and thread-safe data structures
- **Design Patterns**: Command pattern implementation for maintainable and extensible code
- **Error Handling**: Robust exception handling for network failures, timeouts, and edge cases
- **Build Systems**: Gradle configuration including custom tasks for creating standalone JARs
- **User Experience**: Even terminal applications benefit from thoughtful UX (colors, clear feedback, intuitive commands)

## Future Improvements

- Implement proper salt-based password hashing (currently uses simple SHA-256)
- Add message encryption for privacy
- Implement persistent chat history
- Add user roles and permissions
- Support for file sharing
- Reconnection logic with session resumption
- Unit and integration test coverage
- Configuration file support for server settings

## Known Limitations

This is a learning project and has intentional limitations:
- Text file-based storage (not suitable for production scale)
- No TLS/SSL encryption for network traffic
- Basic authentication without account recovery
- Limited error recovery for network issues
- No rate limiting or abuse prevention

**This project is not intended for production use.** It's a learning tool and reference implementation.

## Contributing

Contributions are welcome! Feel free to:
1. Open an issue to discuss proposed changes
2. Fork the repository
3. Create a feature branch (`feature/your-feature-name`)
4. Submit a pull request with clear descriptions

## License

Created by Augusto and Davi. Free to use and modify for educational purposes.

---

**Note**: DBW stands for "Discord But Worse" as a lighthearted acknowledgment that this is a minimal implementation compared to production chat systems. The name is a reminder to stay humble while learning.
