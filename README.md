# DBW

Discord But Worse — Sim, somos uma bosta e assumimos sem vergonha

Bem-vindo ao **DBW**: o sonho molhado de qualquer programador sem grana e com zero senso de responsabilidade. Aqui fazemos um Discord fake, mais tosco que sopa de pedra e com menos funcionalidades do que você jamais imaginaria.

Se você veio procurar estabilidade, UX decente ou suporte humano, **volta logo pro Google**. Este repositório é para masoquistas que curtem quebrar a cara e rir da própria incompetência. Se você ficou, parabéns, seu sadomasoquista — prepare-se pra xingar cada linha de código enquanto contribui.

## Visão (ou confissão de incompetência)

`DBW` é um protótipo de chat no terminal em Java. Não é seguro, não escala, e provavelmente vai travar ou explodir o seu PC enquanto você toma café. É honesto sobre ser **uma merda completa**.

Use para:

* Aprender sockets, threads e como NÃO escrever código às 3 da manhã.
* Se divertir com bugs grotescos e travamentos épicos.
* Entender por que confiar em código de madrugada é igual apostar na Mega-Sena.

Principais intenções:

* Código barato, mal-ajambrado e didático (aka gambiarra com charme).
* Pacotes organizados só pra fingir profissionalismo: `chat.server`, `chat.client`, `chat.commands`, `chat.utils`.
* Persistência mínima em `src/main/resources/users.txt` (porque banco de dados é luxo que não temos).

## Status

**🎉 CARALHO, ESSA MERDA FUNCIONA MESMO! 🎉** 

Implementação COMPLETA e TESTADA:

- ✅ **Servidor TCP multi-threaded** - Aceita múltiplos clientes simultaneamente
- ✅ **Sistema de autenticação SHA-256** - Registra usuário automaticamente se não existir
- ✅ **Salas de chat dinâmicas** - Cria salas na hora, sem configuração
- ✅ **Mensagens privadas** - `/msg username mensagem`
- ✅ **Comandos funcionais**: `/join`, `/msg`, `/quit`, `/help`, `/users`, `/rooms`
- ✅ **Persistência automática** - Salva usuários em `users.txt` automaticamente
- ✅ **Interface colorida** - Terminal com cores ANSI (pra não ficar tão deprimente)
- ✅ **Logs detalhados** - Acompanha conexões, mensagens e erros
- ✅ **Thread-safe** - Usa ConcurrentHashMap e CopyOnWriteArrayList
- ✅ **Graceful shutdown** - Para tudo direitinho com Ctrl+C

**🔥 TESTADO E APROVADO:** Múltiplos clientes conectados, chat em tempo real, mensagens privadas, persistência de usuários. É uma bosta, mas É UMA BOSTA QUE FUNCIONA!

**⚠️ Bugs conhecidos:** Ainda é uma bosta, mas pelo menos é uma bosta funcional!

## Estrutura do projeto (árvore de desastre)

```
DBW/
│
├─ src/
│   ├─ main/
│   │   ├─ java/
│   │   │   ├─ chat/
│   │   │   │   ├─ server/
│   │   │   │   │   ├─ ChatServer.java
│   │   │   │   │   ├─ ClientHandler.java
│   │   │   │   │   └─ User.java
│   │   │   │   ├─ client/
│   │   │   │   │   ├─ ChatClient.java
│   │   │   │   │   └─ MessageListener.java
│   │   │   │   ├─ utils/
│   │   │   │   │   ├─ ConsoleUtils.java
│   │   │   │   │   └─ CryptoUtils.java
│   │   │   │   └─ commands/
│   │   │   │       ├─ Command.java
│   │   │   │       ├─ JoinCommand.java
│   │   │   │       ├─ MsgCommand.java
│   │   │   │       └─ QuitCommand.java
│   │   │   └─ ChatApp.java
│   │   └─ resources/
│   │       └─ users.txt
│   └─ test/
│
├─ build.gradle  (ou `pom.xml`)
└─ README.md
```

## Contrato mínimo (ou seja, não prometemos nada)

* Entrada: conexões TCP de clientes e linhas de texto digitadas desesperadamente.
* Saída: mensagens entre clientes, com perdas, bugs e crashes inesperados.
* Persistência: `src/main/resources/users.txt` — mais inútil que guarda-chuva em furacão.

## Como compilar e executar (ou se fuder tentando)

**ATENÇÃO: O código agora existe e pode realmente funcionar (ou não)!**

### Compilação

Com Gradle (pra quem gosta de tortura planejada):

```powershell
# Compila tudo e gera JARs do servidor E cliente
gradle buildAll

# Ou comandos separados:
gradle build          # Compila o código
gradle serverJar       # Gera JAR do servidor
gradle clientJar       # Gera JAR do cliente

# Ou usa o script automático (Windows)
.\build-all.bat
```

### Execução

**Servidor** (o coração dessa bosta):

```powershell
# Método ULTRA-FÁCIL (recomendado)
.\start-server.bat

# Método JAR direto (porta 25576 padrão)
java -jar build\libs\DBW-Server-Standalone-1.0-server.jar server

# Servidor em porta específica
java -jar build\libs\DBW-Server-Standalone-1.0-server.jar server 9999
```

**Cliente** (pra você entrar nessa merda):

```powershell
# Método ULTRA-FÁCIL (recomendado)
.\start-client.bat

# Método JAR específico do cliente (conecta em localhost:25576)
java -jar build\libs\DBW-Client-Standalone-1.0-client.jar client

# Cliente conectando em servidor remoto
java -jar build\libs\DBW-Client-Standalone-1.0-client.jar client 192.168.1.100 25576

# Método JAR universal (também funciona)
java -jar build\libs\DBW-Server-Standalone-1.0-server.jar client
```

### Execução esperada (na prática):

1. **Inicie o servidor primeiro**, senão você é burro.
2. **Abra quantos clientes quiser** até o servidor chorar, explodir ou seu PC morrer.
3. **Faça login** (ou registre-se na hora - é tudo automático, que bosta conveniente!).
4. **Use /join sala** pra entrar numa sala.
5. **Digite mensagens** normais ou use /msg pra mensagem privada.
6. **Use /quit** quando cansar dessa merda.

## Comandos do cliente (funcionais e prontos pra quebrar)

* `/join <sala>` — entra numa sala (cria se não existir) ou muda de sala.
* `/msg <usuario|sala> <mensagem>` — manda mensagem privada ou pra sala específica.
* `/quit` — fecha o cliente e volta pra vida real.
* `/help` — mostra ajuda (básica, mas funciona).
* `/users` — lista quem tá online e em que sala.
* `/rooms` — lista salas ativas com número de usuários.

**Exemplos que funcionam de verdade:**

```
> /join geral
✓ Você entrou na sala 'geral'

> Oi pessoal, essa bosta funciona!
admin: Oi pessoal, essa bosta funciona!

> /msg fulano Mensagem secreta
✓ Mensagem privada enviada para fulano

> /users
Usuários online: admin (geral), fulano (lobby)

> /quit
✓ Tchau! Obrigado por usar essa bosta do DBW!
```

## Edge cases garantidos (ou seja, prepare-se para surtar)

* Concorrência: threads brigam entre si por atenção.
* Conexões instáveis: desconecte-se e chore.
* Usuários com mesmo nome: conflito e drama garantidos.
* Mensagens longas: truncamento, erro ou crash com estilo.

## Próximos passos sugeridos (se você for corajoso)

1. ✅ ~~Implementar `ChatServer`, `ClientHandler`, `User`.~~ **FEITO!**
2. ✅ ~~Implementar `ChatClient` e `MessageListener`.~~ **FEITO!**
3. ✅ ~~Criar comandos reais em `chat.commands`.~~ **FEITO!**
4. ✅ ~~Adicionar logs decentes~~ **FEITO!** (porque `System.out.println` agora tem estilo!)
5. 🔄 **Em andamento:** TLS, autenticação melhor e pagar alguém pra auditar essa bosta.
6. 🆕 **Novo:** Adicionar emojis nas mensagens porque vida sem emoji é tristeza.
7. 🆕 **Novo:** Sistema de ban/kick porque sempre tem um chato.
8. 🆕 **Novo:** Histórico de mensagens porque memória humana é uma bosta.
9. 🆕 **Novo:** Interface gráfica (Swing ou JavaFX) pra quem tem preguiça de terminal.

## Como contribuir (para masoquistas de plantão)

1. Abra uma issue explicando qual parte dessa zona você quer arrumar.
2. Crie um branch com nome claro (`feature/alguma-bosta` ou `fix/mais-bosta`).
3. Faça PR com descrição, testes e paciência infinita.

## Aviso legal (de boa fé, mas duvido que importe)

Este projeto é uma **piada de mau gosto** e de aprendizado. Não use em produção. Se usar, algo provavelmente vai explodir e a culpa é **inteiramente sua**. Nós apenas fornecemos o manual da merda.

## Licença

Nenhum direito reservado

---