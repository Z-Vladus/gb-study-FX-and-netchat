package com.example.netchat.Client;

import com.example.netchat.Command;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;

    private final NetChatController controller;

     public ChatClient(NetChatController controller) {
        this.controller = controller;
    }

    public void openConnection () throws IOException {
        System.out.println("Opening connection...");
        s = new Socket("localhost",8189);

        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());

        Thread readThread = new Thread (() -> {
            System.out.println("readThread started...");
            try {
                waitAuth();
                readMsg();
            } finally {
              closeConnection();  
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

    private void closeConnection() {
        try {
            s.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readMsg() {
        System.out.println("readMsg started...");
         while (true){
            try {
                System.out.println("Reading input stream...");
                String buf = in.readUTF();
                System.out.println("Done. Result: "+buf);

                if (Command.isCommand(buf)) {
                    Command cmd = Command.getCommand(buf);
                    String[] params = cmd.parse(buf);
                    System.out.println("Command detected: "+cmd);
                    System.out.println("params detected: "+params);
                    if(cmd == Command.END) {
                        System.out.println("Command.END: ");
                        controller.setAuth(false);
                        break;
                    }
                    if (cmd == Command.ERROR) {
                        System.out.println("Command.ERROR: ");
                        //controller.setAuth(false); // надо ли?
                        // OLD
                        // controller.showError(params);
                        Platform.runLater(() -> controller.showError(params));
                        //break;
                        continue;
                    }
                    if (cmd==Command.CLIENTS) {
                        controller.updClientList(params);
                        //continue или всё ж break?
                        continue;
                    }
                }


                controller.addMessage("readMSG: "+buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private void waitAuth() {
        System.out.println("waitAuth() method started");
         while (true) {
            try {
                System.out.println("waiting for server auth reply...");
                String buf = in.readUTF();
                System.out.println("Got reply from server. buf="+buf);
                if (Command.isCommand(buf)) {
                    Command cmd = Command.getCommand(buf);
                    String[] params = cmd.parse(buf);
                    if (cmd == Command.AUTHOK) {
                        String nick = params[0];
                        controller.addMessage("Auth good with nick = "+nick);
                        controller.setAuth(true);
                        break;
                    }
                    if (cmd==Command.ERROR) {
                        // так - неверно, будет ошибка
                        //controller.showError(params);
                        // а вот с таким магическим заклинанием - норм!
                        Platform.runLater(() -> controller.showError(params));
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void sendMessage(String s) {
        try {
            System.out.println("sending message:"+s);
            out.writeUTF(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessage(Command cmd, String... params) {
         sendMessage(cmd.collectMessage(params));
    }
}
