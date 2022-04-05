package com.example.netchat;

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

    private int textAreaIndex=0;

    public void sendButtonClick(ActionEvent actionEvent) {
        System.out.println("sendbtnclick");
        messageArea.insertText(0, "["+ LocalDateTime.now() +"] " + messageField.getText() + "\n");

        //textAreaIndex++;
    }
   /* @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");


    */



}