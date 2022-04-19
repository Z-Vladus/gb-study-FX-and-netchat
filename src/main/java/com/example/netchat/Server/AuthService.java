package com.example.netchat.Server;

import java.io.Closeable;

public interface AuthService extends Closeable {
    void start();
    void close();
    String getNickByLoginPass(String login, String pass);

 }
