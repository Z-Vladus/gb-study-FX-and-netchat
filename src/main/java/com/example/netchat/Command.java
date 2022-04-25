package com.example.netchat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Command {
    AUTH("/auth"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(COMMAND_DELIMITER);
            return new String[]{split[0],split[1]};
        }
    },
    AUTHOK("/authok") {
        @Override
        public String[] parse(String commandText) {
            return new String[]{commandText.split(COMMAND_DELIMITER)[1]};
        }
    },
    PRIVATE_MESSAGE("/w"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(COMMAND_DELIMITER ,3);
            String nick = split[1];
            String mateMsg = split[2];
            return new String[]{nick,mateMsg};
        }
    },
    END("/end") {
        @Override
        public String[] parse(String commandText) {
            return new String[0];
        }
    },
    ERROR("/error"){ // /error сообщение об ошибке
        @Override
        public String[] parse(String commandText) {
            String errorMsg = commandText.split(COMMAND_DELIMITER, 2)[1];
            return new String[]{errorMsg};

        }
    };


    /*
    // вариант Мап 1
    private static final Map<String,Command> cmdMap = new HashMap<>(){{
        put("/auth",Command.AUTH);
        put("/authok",Command.AUTHOK);
        put("/w",Command.PRIVATE_MESSAGE);
        put("/end",Command.END);
    }};

    // вариант Мап 2 (immutable? ) - неизменяемый, быстрый
    private static final Map<String,Command> cmdMap = Map.of(
        "/auth",Command.AUTH,
        "/authok",Command.AUTHOK,
        "/w",Command.PRIVATE_MESSAGE,
        "/end",Command.END
    );
*/
    // вариант Мап 3 (immutable? ) - с динамическим добавлением из списка - такое часто используют
    private static final Map<String,Command> cmdMap =
            Stream.of(Command.values()).collect(Collectors.toMap(Command::getCommand, Function.identity()));

    private String command;
    private String[] params = new String[0];
    //разделитель = один или больше пробельных символов = \s
    private static String COMMAND_DELIMITER ="\\s+";


    Command(String command) {
        this.command=command;
    }

    public static boolean isCommand (String message) {
        return message.startsWith("/");
    }

    public String[] getParams (){
        return params;
    }

    public String getCommand() {
        return command;
    }

    public static Command getCommand(String message){
        message=message.trim();
        if (!isCommand(message)) {
            throw new RuntimeException("<"+"> is not a command!");
        }
        int i = message.indexOf(" ");
        String cmd;
        if (i>0) {
            cmd = message.substring(0,i);}
        else {
            cmd = message;}

        for (Command value : Command.values()) {
             if (value.getCommand().equals(cmd)) {
                return value;
             }
        }

        return null;

    }

    public abstract String[] parse(String commandText);

}
