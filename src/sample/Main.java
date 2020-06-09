package sample;

import Controller.ChatClient;
import Controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        ChatClient chatClient;
        String hostname = "localhost";
        int port = 8818;
        System.out.println("connecting");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/Login.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Login");
            LoginController loginController = loader.getController();

            Socket socket = new Socket(hostname, port);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            chatClient = new ChatClient("", inputStream, outputStream, socket);
            Thread chatClientThread = new Thread(chatClient);
            chatClient.setLoginController(loginController);
            chatClientThread.start();

            primaryStage.setScene(new Scene(root, 435, 545));
            primaryStage.show();
            primaryStage.setResizable(false);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    public static void main(String[] args) {
        launch(args);
    }

}
