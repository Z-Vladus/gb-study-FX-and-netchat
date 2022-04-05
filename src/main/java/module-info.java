module com.example.netchat {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.netchat to javafx.fxml;
    exports com.example.netchat;
}