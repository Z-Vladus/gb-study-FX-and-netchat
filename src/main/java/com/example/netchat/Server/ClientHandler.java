package com.example.netchat.Server;

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
    private String nick;



    public String getName() {
        return name;
    }

    public ClientHandler(ChatServer chatServer, Socket socket) {
        try {
            this.chatServer = chatServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";


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
                    // TODO
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    //пока оставим метод из (товтология!) методички
    public void authentication() throws IOException {
        while (true) {

            String str = in.readUTF();

            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                String nick =
                        chatServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                if (nick != null) {
                    if (!chatServer.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        chatServer.broadcastMsg(name + " зашел в чат");
                        chatServer.subscribe(this);
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }
    public void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            System.out.println("от " + name + ": " + strFromClient);
            if (strFromClient.equals("/end")) {
                return;
            }
            chatServer.broadcastMsg(name + ": " + strFromClient);
        }
    }
    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeConnection() {
        chatServer.unsubscribe(this);
        chatServer.broadcastMsg(name + " вышел из чата");

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
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("socket close problem");
        }
    }
}
