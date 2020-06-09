package Controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;


public class ChatUIController extends ListView<String>  implements Runnable{
    SendThread sender;
    ReceiveThread receiver;
    String currentUser;
    String currentReceiver;
    String message;
    String filePath;
    String currentGr;
    FileChooser fileChooser;
    boolean sendToGr = false;

    @FXML
    public Label NameOfUser;

    @FXML
    private JFXTextField textField;

    @FXML
    private ImageView sendBut;

    @FXML
    private ImageView fileBut;

    @FXML
    private Button addGroupBut;

    @FXML
    private JFXListView<String> onlineList;

    public ObservableList<String> listView = FXCollections.observableArrayList();//contant name on online user

    @FXML
    private Pane chatField;

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private JFXListView<String> groupList;
    public ObservableList<String> groupListView = FXCollections.observableArrayList();//contant group

    private List<Label> messagesList = new ArrayList<>();
    private int index = 0;

//    @Override
//    public void (URL url, ResourceBundle resourceBundle) {
//
//        onlineList.setItems(listView);
//    }

    //TODO: Init online list for user base on online user that user send back
    public void initOnlineList(){
        //Depend on what server send back
        if (!listView.isEmpty()){
            currentUser = listView.get(0);
        }
        onlineList.setItems(listView);
        scrollPane.setContent(chatBox);
    }

    //TODO: Update online list when a user online
    public void online(String user) {
        if (listView.isEmpty()) {
            currentReceiver = user;
        }

        listView.add(user);
        onlineList.setItems(listView);
    }

    public void setSender(SendThread sender){
        this.sender = sender;
        this.sender.setChatUIController(this);
    }

    public void setReceiver(ReceiveThread receiver){
        this.receiver = receiver;
        this.receiver.setChatUIController(this);
    }


    //TODO: Click to chat with other user
    @FXML
    public void handleMouseClickUser(){
        String selectedUser = onlineList.getSelectionModel().getSelectedItem();

        sendToGr = false;
        if (selectedUser == currentReceiver){
            System.out.println("Notthing change");
        }
        else {
            currentReceiver = selectedUser;
            chatBox.getChildren().removeAll(chatBox.getChildren());
        }
    }

    //TODO: Click to chat with other user
    @FXML
    public void handleMouseClickGroup(){
        sendToGr = true;
        String selectedGr = groupList.getSelectionModel().getSelectedItem();
        if (selectedGr == currentGr){
            System.out.println("Notthing change");
        }
        else {
            currentGr = selectedGr;
            chatBox.getChildren().removeAll(chatBox.getChildren());
        }
    }

    //TODO: Update online list when a user offline
    public void offline(String user){
        listView.remove(user);
        onlineList.setItems(listView);
    }

    //TODO: Set name of current User
    public void setNameOfUser(String nameOfUser){
        NameOfUser.setText(nameOfUser);
    }

    //TODO: Send message if message field not empty
    public void sendMess(){
        message = textField.getText();
        String recv;
        String msg = "";
        if (sendToGr == false) {
            msg = "SEND MSG\n";
            recv = currentReceiver;
        } else {
            msg = "SEND GROUP\n";
            recv = currentGr;
        }
        msg += this.sender.getClientName() + " " +recv+ "\n";
        msg += "\n";
        msg += message + "\n";
        try {
            sender.send(msg);
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!"".equals(message)){
            Text text = new Text(message);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 16));
            text.getStyleClass().add("message" + "\n");
            TextFlow tempFlow = new TextFlow();

            tempFlow.getChildren().add(text);
            tempFlow.setMaxWidth(300);


            TextFlow flow=new TextFlow(tempFlow);
            HBox hbox=new HBox(12);

            tempFlow.getStyleClass().add("tempFlow");
            flow.getStyleClass().add("textFlow");
            chatBox.setAlignment(Pos.TOP_RIGHT);
            hbox.setAlignment(Pos.CENTER_RIGHT);
            hbox.getChildren().add(flow);
            hbox.getStyleClass().add("hbox");
            Platform.runLater(() -> chatBox.getChildren().addAll(hbox));

            chatField.layout();
            scrollPane.setVvalue(chatBox.getHeight());

            // receiveMess(message); //Test receive message UI

            //code to send message to server

        }
        else {
            System.out.println("null message");//Use to check emty, delete it
        }
        textField.setText("");//set message field
    }

    //TODO: Receive and message and update chatbox
    public void receiveMess(String mess){
        Text text = new Text(mess);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 16));
        text.getStyleClass().add("message");
        TextFlow tempFlow = new TextFlow();

        tempFlow.getChildren().add(text);
        tempFlow.setMaxWidth(300);


        TextFlow flow=new TextFlow(tempFlow);
        HBox hbox=new HBox(12);

        tempFlow.getStyleClass().add("tempFlowFlipped");
        flow.getStyleClass().add("textFlowFlipped");
        chatBox.setAlignment(Pos.TOP_LEFT);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(flow);
        hbox.getStyleClass().add("hbox");
        Platform.runLater(() -> chatBox.getChildren().addAll(hbox));

        chatField.layout();
        scrollPane.setVvalue(chatBox.getHeight());
    }

    //TODO: Get file name and file path to send
    public void getFile(){
        Stage stage = new Stage();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
        filePath = file.getPath();
        textField.setText(file.getName());
    }


    @FXML //TODO: Send message via Enter Press
    public void onEnter(javafx.event.ActionEvent actionEvent) throws IOException{
        sendMess();
    }

    @Override
    public void run() {

    }

    public void addGroup(ActionEvent actionEvent) throws IOException {
        Stage addGroup = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/addGroup.fxml"));
        Parent root = loader.load();
        AddGroupController addGroupController = loader.getController();
        addGroupController.setSender(sender);
        addGroupController.setChatUIController(this);
        addGroupController.getOnlineList(listView);
        Scene scene = new Scene(root);
        addGroup.setScene(scene);
        addGroup.show();
        addGroup.setResizable(false);
    }

    public void newGr(String groupName){
        if (!groupListView.isEmpty()){
            currentGr = groupName;
        }
        groupListView.add(groupName);
        groupList.setItems(groupListView);

    }
}
