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
        System.out.println("Opening connection...");
        s = new Socket("localhost",8189);

        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());

        Thread readThread = new Thread (() -> {
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
        while (true){
            try {
                System.out.println("Reading input stream...");
                String buf = in.readUTF();
                System.out.println("Done. Result: "+buf);
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
                System.out.println("waiting for server auth reply...");
                String buf = in.readUTF();
                System.out.println("Got reply from server. buf="+buf);
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
            System.out.println("sending message:"+s);
            out.writeUTF(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
