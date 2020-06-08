package Controller;

import com.jfoenix.controls.*;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOError;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;

public class SignUpController implements Initializable{
    @FXML
    private JFXButton signup;

    @FXML
    private JFXButton loginBut;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML
    public void login(javafx.event.ActionEvent actionEvent) throws IOException {
        Stage st1 = (Stage) loginBut.getScene().getWindow();
        st1.hide();
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/Login.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }
}
