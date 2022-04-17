package com.example.netchat.Server;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService{
    private class UserID {
        private String login;
        private String pass;
        private String nick;
        public UserID(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }
    private List<UserID> userIDs;
    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }
    @Override
    public void close() {
        System.out.println("Сервис аутентификации остановлен");
    }
    public BaseAuthService() {
        userIDs = new ArrayList<>();
        userIDs.add(new UserID("user1", "pass1", "nick1"));
        userIDs.add(new UserID("user2", "pass2", "nick2"));
        userIDs.add(new UserID("user3", "pass3", "nick3"));
    }
    @Override
    public String getNickByLoginPass(String login, String pass) {
        for (UserID id : userIDs) {
            if (id.login.equals(login) && id.pass.equals(pass)) return id.nick;
        }
        return null;
    }

}
