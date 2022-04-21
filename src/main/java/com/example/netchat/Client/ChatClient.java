package com.example.netchat.Client;

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
        s = new Socket("localhost",8189);
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());

        new Thread(() -> {
            try {
                waitAuth();
                readMsg();
            } finally {
              closeConnection();  
            }
            

        }).start();

    }

    private void closeConnection() {
    }

    private void readMsg() {
        while (true){
            try {
                String buf = in.readUTF();
                if("/end".equals(buf)) {
                    controller.setAuth(false);
                    break;
                }
                controller.addMessage("readMSG: "+buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private void waitAuth() {
        while (true) {
            try {
                String buf = in.readUTF();
                if (buf.startsWith("/authok")) {
                    String[] bufSplitted = buf.split(" ");
                    String nick = bufSplitted[1];
                    controller.addMessage("Auth good with nick = "+nick);
                    controller.setAuth(true);
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void sendMessage(String s) {
        try {
            out.writeUTF(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
