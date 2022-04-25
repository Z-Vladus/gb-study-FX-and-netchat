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

            new Thread(() -> {
                try {
                    authentication();
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


    public void auth () {
        while (true) {

            try {
                final String buf = in.readUTF();
                if (buf.startsWith("/auth")) {
                    final String[] buf2 = buf.split(" "); // buf2[0]="/auth"
                    final String login=buf2[1];
                    final String pass=buf2[2];

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    //пока оставим метод из (товтология!) методички
    public void authentication() throws IOException {
        while (true) {
            String buf = in.readUTF();
            if (Command.isCommand(buf)) {
                Command command = Command.getCommand(buf);
                String[] params = command.parse(buf);
                // формат команды аутентификации: /auth <login> <password>
                if (command==Command.AUTH) {
                    //разделяем на слова
                    String login = params[0];
                    String password = params[1];
                    System.out.println("Buf parsing: Login = "+login+" password="+password);
                    //String nick = chatServer.getAuthService().getNickByLoginPass(login, password);
                    String nick = authService.getNickByLoginPass(login, password);

                if (nick != null) {
                    System.out.println("Got nick: "+nick);
                    if (!chatServer.isNickBusy(nick)) {
                        System.out.println("Nick="+nick+" is not busy");
                        //TODO 1:07

                        sendMsg("/authok " + nick);
                        name = nick;
                        chatServer.serverMsgToAll("Сервер: "+name+" зашёл в чат");
                        chatServer.subscribe(this);
                        //return;
                        break;
                    } else {
                        sendMsg("Учетная запись "+login+" уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }
    public void readMessages() throws IOException {
        while (true) {
            String buf = in.readUTF();
            System.out.println("received from "+name + ": " + buf);
            if (buf.equals("/end")) {
                System.out.println("received /end command. Exiting");
                // default code
                // return;

                // my code 22/04/22
                closeConnection();
                break;
                // end my code 22/04/22
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
                chatServer.serverMsgToAll(name + ": " + buf);
            }


        }
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
        sendMsg("/end");
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
