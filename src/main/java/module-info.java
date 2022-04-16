module com.example.netchat {
    requires javafx.controls;
    requires javafx.fxml;


    //opens com.example.netchat. to javafx.fxml;
    //exports com.example.netchat;
    exports com.example.netchat.Client;
    opens com.example.netchat.Client to javafx.fxml;
}