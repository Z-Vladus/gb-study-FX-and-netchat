package com.example.netchat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;

    public void run() {
        try (ServerSocket server = new ServerSocket(PORT);
            ) {
            while (true) {
                System.out.println("Server awaits incoming connections...");
                Socket socket = server.accept();
                System.out.println("Client " +socket.getInetAddress()+ " connected");
                new ClientHandler(this, socket,authService);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ChatServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();

            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket,authService);
            }
        } catch (IOException e) {
            System.out.println("Ошибка в работе сервера");
        }
          finally {
            if (authService != null) {
                authService.close();
            }
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void serverMsgToAll(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
    }

    public AuthService getAuthService() {
        return this.authService;
    }
}
