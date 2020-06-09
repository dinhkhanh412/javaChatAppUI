package Controller;

import com.jfoenix.controls.*;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;



public class LoginController implements Initializable{
    SendThread sender;
    ReceiveThread receiver;
    public ChatClient client;


    @FXML
        private JFXButton signupBut;

    @FXML
        private JFXButton loginBut;

    @FXML
        private JFXTextField username;

    @FXML
        private JFXPasswordField password;

    @FXML
        private Label invalidLable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        invalidLable.setVisible(false);
    }

    @FXML
    public void login(javafx.event.ActionEvent actionEvent) throws IOException{

        Stage chatUI = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/chatUI.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        String nameOfUser = this.client.getName(); // Server send back
        ChatUIController chatUIController = loader.getController();
        // me trying to set sender and receiver for this shit but it did not work
        chatUIController.setReceiver(receiver);
        chatUIController.setSender(sender);

        boolean checkUser = false;
        String userName = username.getText();
        String passWord = password.getText();

        sender.loginRequest(userName, passWord);

        while(!client.isLogin()){
        }
        checkUser = true;
        if (checkUser) {
            Stage st = (Stage) loginBut.getScene().getWindow();
            st.hide();
            chatUIController.setNameOfUser(client.getName());
            Scene scene = new Scene(root);
            chatUI.setScene(scene);
            chatUI.show();
            chatUI.setResizable(false);

        } else {
            invalidLable.setVisible(true);
        }
    }

    public void setSender(SendThread sender){
        this.sender = sender;
    }

    public void setReceiver(ReceiveThread receiver){
        this.receiver = receiver;
    }

    public void setChatClient(ChatClient chatClient){
        this.client = chatClient;
        setReceiver(client.getRecv());
        setSender(client.getSend());
    }

    //TODO: Switch to signup form
    @FXML
    public void signUp(javafx.event.ActionEvent actionEvent) throws IOException {
        Stage st = (Stage) signupBut.getScene().getWindow();
        st.hide();
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/SignUP.fxml"));
        Scene scene = new Scene(root);

        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }



    //TODO: Login via Enter
    @FXML
    public void onEnter(javafx.event.ActionEvent actionEvent) throws IOException {
        login(actionEvent);
    }
}
