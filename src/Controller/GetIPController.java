package Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GetIPController {
    String hostname;
    ChatClient chatClient;

    @FXML
    JFXTextField ipAdd;

    public void enter() throws IOException {
        Stage stage = new Stage();


        hostname = ipAdd.getText();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/Login.fxml"));

        Parent root = loader.load();
        stage.setTitle("Login");
        LoginController loginController = loader.getController();

        Socket socket = new Socket(hostname, 8818);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        chatClient = new ChatClient("", inputStream, outputStream, socket);
        Thread chatClientThread = new Thread(chatClient);
            chatClient.setLoginController(loginController);
        chatClientThread.start();

            stage.setScene(new Scene(root, 435, 545));
        Stage thisStage = (Stage) ipAdd.getScene().getWindow();
        thisStage.hide();
        stage.show();
        stage.setResizable(false);
    }

    public String getIp(){
        return hostname;

    }
}
