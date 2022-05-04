package com.example.netchat.Server;

import com.example.netchat.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final ChatServer chatServer;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String name;
    private AuthService authService;

    public boolean authTimeOutFlag;
    public boolean userAuthenticated;

    public String getName() {
        return name;
    }

    public ClientHandler(ChatServer chatServer, Socket socket, AuthService authService) {
        try {
            this.chatServer = chatServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            this.authService=authService;
            this.authTimeOutFlag =false;
            this.userAuthenticated=false;


            new Thread(() -> {
                try {
                    authentication();
                    if (authTimeOutFlag) closeConnection();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("ClientHandler problem");
        }
    }

    public void authentication() throws IOException {
        // homework lesson 8!

        Thread loginTimeOutThread = new Thread(() -> {
            for (int i = 0; i < 15; i++) {
                System.out.println("login timeout for this client at socket"+this.socket+" is "+(15-i));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (this.userAuthenticated) break;
            }

            if (!this.userAuthenticated) {
                System.out.println("login timed out for "+name+"!");
                this.authTimeOutFlag =true;
            }

        });
        loginTimeOutThread.start();
        // homework lesson 8 END

        while (true) {
            String buf = in.readUTF();
            if (authTimeOutFlag) { sendMsg(Command.ERROR, "Таймаут ввода пароля!"); break; };
            if (Command.isCommand(buf)) {
                Command command = Command.getCommand(buf);
                String[] params = command.parse(buf);
                // формат команды аутентификации: /auth <login> <password>
                if (command == Command.AUTH) {
                    //разделяем на слова
                    String login = params[0];
                    String password = params[1];
                    System.out.println("Buf parsing: Login = " + login + " password=" + password);
                    //String nick = chatServer.getAuthService().getNickByLoginPass(login, password);
                    String nick = authService.getNickByLoginPass(login, password);

                    if (nick != null) {
                        System.out.println("Got nick: " + nick);
                        if (!chatServer.isNickBusy(nick)) {
                            System.out.println("Nick=" + nick + " is not busy");
                            this.userAuthenticated=true;
                            sendMsg(Command.AUTHOK,nick);
                            name = nick;
                            chatServer.serverMsgToAll("Сервер: " + name + " зашёл в чат");
                            chatServer.subscribe(this);
                            //return;
                            break;
                        } else {
                            sendMsg(Command.ERROR, "Учетная запись " + login + " уже используется");
                        }
                    } else {
                        sendMsg(Command.ERROR, "Неверные логин/пароль");
                    }
                }
            }
        }
    }

    public void readMessages() throws IOException {
        while (true) {
            String buf = in.readUTF();
            System.out.println("received from "+name + ": " + buf);

            if (Command.isCommand(buf) ){

                Command cmd = Command.getCommand(buf);
                String[] params = cmd.parse(buf);
                System.out.println("Looks like it is a command:"+cmd.getCommand());

                if (cmd == Command.END ) {
                    System.out.println("Executing END command");
                    //todo оставим ли тут closeConnection(); ?
                    //closeConnection();
                    break;
                }
                if (cmd == Command.PRIVATE_MESSAGE) {
                    System.out.println("Executing PRIVATE_MESSAGE command");
                    // ЛС - от кого, кому и само сообщение.
                    chatServer.serverMsgToNickNew(this, params[0],params[1]);
                    continue;
                }
            }

            // если приватное сообщение...
            else if (buf.startsWith("/w ")) {
                System.out.println("получена команда приватного сообщения:");
                //распарсим буфер
                String[] buf2 = buf.split("\\s");
                String mateNick = buf2[1];
                String mateMsg = buf2[2];
                System.out.println("кому сообщение:"+mateNick);
                System.out.println("сообщение:"+mateMsg);
                chatServer.serverMsgToNick(name,mateNick,mateMsg);
            }
            //в остальных случаях
            else {
                //chatServer.serverMsgToAll(name + ": " + buf);
                //chatServer.broadcast(name + ": " + buf);
                // Todo что тут писать?
            }


        }
    }

    public void sendMsg(Command command, String... params) {
        sendMsg(command.collectMessage(params));

    }
    public void sendMsg(String msg) {

        try {
            out.writeUTF(msg);
            System.out.println("Sending: "+msg);
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("sending message problem");
        }
    }

    public void closeConnection() {
        //sendMsg("/end");
        sendMsg(Command.END);
        chatServer.unsubscribe(this);
        chatServer.serverMsgToAll("Server: "+name + " вышел из чата");

        try {
            if (in != null ) in.close();
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("Input stream close problem");
        }

        try {
            out.close();
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("Output stream close problem");
        }

        try {
            socket.close();
            chatServer.unsubscribe(this);
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("socket close problem");
        }
    }



}
