package com.example.netchat.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;

public class NetChatController {
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField messageField;


    public void sendButtonClick(ActionEvent actionEvent) {
        //System.out.println("sendbtnclick");
        if (messageField.getText().length()>1) { // если есть что посылать
            messageArea.insertText(0, "[" + LocalDateTime.now() + "] " + messageField.getText() + "\n");
            messageField.clear();

        }

    }


}