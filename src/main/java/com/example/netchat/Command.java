package com.example.netchat;

public enum Command {
    AUTH("/auth"),
    AUTHOK("/authok"),
    PRIVATE_MESSAGE("/w"),
    END("/end");

    private String command;
    private String[] params = new String[0];

    Command(String command) {
        this.command=command;
    }

    private static boolean isCommand (String message) {
        return message.startsWith("/");
    }

    public String[] getParams (){
        return params;
    }

    private static Command getCommand(String message){
        if (!isCommand(message)) {
            throw new RuntimeException("<"+"> is not a command!");
        }
        int i = message.indexOf(" ");
        //TODO
        return (END); //исправить

    }

}
