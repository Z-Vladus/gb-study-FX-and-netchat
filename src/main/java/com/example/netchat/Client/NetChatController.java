package com.example.netchat.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class NetChatController {

    @FXML
    public Button sendButton;
    @FXML
    private HBox loginBox;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private VBox messageBox;
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField messageField;

    private final ChatClient client;

    public NetChatController() {
        client = new ChatClient(this);
        while (true) {
            try {
                client.openConnection();
                break;
            }
            catch (Exception e) {
                showNotification();
            }
        }
    }

    private void showNotification() {
        final Alert alert = new Alert(Alert.AlertType.ERROR,
                "Could not connect to server",
                new ButtonType("Try again",ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Quit",ButtonBar.ButtonData.CANCEL_CLOSE)
                );
        alert.setTitle("Connection error");
        Optional<ButtonType> buttonType = alert.showAndWait();
        Boolean isExit = buttonType.map(btn -> btn.getButtonData().isCancelButton()).orElse(false);
        if (isExit) {
            System.exit(1);
        }
    }

    ;
/* DEFAULT
    public NetChatController(){
        this.client= new ChatClient(this);
        try {
            client.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
*/
    public void sendButtonClick(ActionEvent actionEvent) {
        //System.out.println("sendbtnclick");
        //if (messageField.getText().length()>1) { // если есть что посылать
        //    messageArea.insertText(0, "[" + LocalDateTime.now() + "] " + messageField.getText() + "\n");
        //    messageField.clear();

        String buf = messageField.getText();
        if (buf.trim().isEmpty()) return;
        client.sendMessage(buf);
        messageField.clear();
        messageField.requestFocus();


    }

    public void authButtonClick(ActionEvent actionEvent) {
        //loginField.setText("user1");
        //passwordField.setText("pass1");
        client.sendMessage("/auth "+loginField.getText() + " "+ passwordField.getText());

    }

    public void addMessage(String s) {
        messageArea.appendText(s+"\n");

    }

    public void setAuth(boolean authorized) {
        loginBox.setVisible(!authorized);
        messageBox.setVisible(authorized);
        sendButton.setVisible(authorized);
        messageField.setVisible(authorized);

    }
}