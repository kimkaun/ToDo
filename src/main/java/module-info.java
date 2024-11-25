module com.example.todo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    exports com.example.todo;
    opens com.example.todo to javafx.fxml;

}
