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
        Thread chatClient;
        String hostname = "192.168.20.135";
        int port = 8818;
        System.out.println("connecting");

        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("connecting");
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            chatClient = new Thread(new ChatClient("", inputStream, outputStream, socket));
            chatClient.start();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/Login.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Login");
            LoginController loginController = loader.getController();
            loginController.chatClient = chatClient;
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
