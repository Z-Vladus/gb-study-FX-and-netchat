package com.example.netchat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatServer {
    private final int PORT = 8189;
    private Map<String, ClientHandler> clients;
    private AuthService authService;
   public void run() {
        try (ServerSocket server = new ServerSocket(PORT);
             AuthService authService = new BaseAuthService()
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
        this.clients = new HashMap<>();

        /* old code from Lesson7
        try (ServerSocket server = new ServerSocket(PORT)) {
            //authService = new BaseAuthService();
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
        } */
    }

    public synchronized boolean isNickBusy(String nick) {
        // new
        return clients.containsKey(nick);

        /* OLD
        for (ClientHandler client : clients) {
            if (client.getName().equals(nick)) {
                return true;
            }
        }
        return false; */
    }

    public synchronized void serverMsgToAll(String msg) {
        List<String> allUsersOnline = clients.values().stream().
                map(ClientHandler::getName).collect(Collectors.toList());
        //broadcast(ClientListMessage.of(allUsersOnline));
        /* OLD
        for (ClientHandler client : clients) {
            client.sendMsg(msg);
        }

        */
    }

    public synchronized void subscribe(ClientHandler o) {
        //clients.add(o);
        clients.put(o.getName(),o);
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o.getName());
    }
    public synchronized void broadcast(String msg) {
        clients.values().forEach(client -> client.sendMsg(msg));
    }

    /*
    public AuthService getAuthService() {
        return this.authService;
    }
*/
    public synchronized void serverMsgToNick(String senderNick, String mateNick, String mateMsg) {
        System.out.println("проверяем занят ли ник которому прислали приватное сообщение");
        if (isNickBusy(mateNick)) {
            //найдём и отошлём сообщение адресату
            // ToDo new code...


            //my oldcode
            /*for (ClientHandler client : clients) {
                if (client.getName().equals(mateNick)) {
                    client.sendMsg("Вам сообщение от " + senderNick + ":" + mateMsg);
                }
            }*/
        } else {
            System.out.println("");
            //если такого ника нет на связи, отсылаем сообщение отправителю
            // ToDo new code...


            //my oldcode
            /*for (ClientHandler client : clients) {
                if (client.getName().equals(senderNick)) {
                    client.sendMsg("Сервер: Ошибка! нет пользователя с ником " + mateMsg + "!");
                }
            }*/
        }
    }
}
