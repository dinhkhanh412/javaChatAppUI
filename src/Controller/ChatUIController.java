package Controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.net.Socket;
import java.io.IOException;


public class ChatUIController extends ListView<String>  implements Runnable{
    File file;
    SendThread sender;
    ReceiveThread receiver;
    String currentUser;
    String currentReceiver;
    String message;
    String filePath;
    String currentGr;
    FileChooser fileChooser;
    boolean sendFile = false;
    boolean sendToGr = false;
    public ArrayList<ChatDialog> chatDialogs = new ArrayList<ChatDialog>();
    public ChatDialog currentDialog = null;
    public ChatDialog currentGrDialog;

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

    public void initGroupList(){
        //Depend on what server send back
        if (!groupListView.isEmpty()){
            currentGr = groupListView.get(0);
        }
        groupList.setItems(groupListView);
        scrollPane.setContent(chatBox);
    }

    //TODO: Update online list when a user online
    public void online(String user) {
        listView.add(user);
        ChatDialog newChatDialog = new ChatDialog();
        newChatDialog.setFriendName(user);
        chatDialogs.add(newChatDialog);
        if (listView.isEmpty()) {
            currentReceiver = user;
            currentDialog = newChatDialog;
        }
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
    public void handleMouseClickUser() throws FileNotFoundException {
        sendToGr = false;
        String selectedUser = onlineList.getSelectionModel().getSelectedItem();
        for (ChatDialog chatDialog : chatDialogs) {
            if (chatDialog.getName().equals(selectedUser) && !chatDialog.isGroup()) {
                currentDialog = chatDialog;
                break;
            }
        }
        chatBox.getChildren().removeAll(chatBox.getChildren());
        currentReceiver = selectedUser;
        for (ChatDialog chatDialog : chatDialogs) {
            if (chatDialog.equals(currentDialog)) {
                for (ChatMessage chatMessage :  chatDialog.getChatHistory()) {
                    if (chatMessage.getSender().equals(NameOfUser.getText())) {
                        UISend(chatMessage.getContent());
                    } else {
                        UIrececive(chatMessage.getContent());
                    }
                    System.out.println(chatMessage.getContent());
                }
                return;
            }
        }
    }

    //TODO: Click to chat with other user
    @FXML
    public void handleMouseClickGroup() throws FileNotFoundException {
        sendToGr = true;
        String selectedGr = groupList.getSelectionModel().getSelectedItem();
        currentGr = selectedGr;
        chatBox.getChildren().removeAll(chatBox.getChildren());
        for (ChatDialog chatDialog : chatDialogs) {
            if (chatDialog.getName().equals(currentGr) && chatDialog.isGroup) {
                currentDialog = chatDialog;
                break;
            }
        }
        for (ChatDialog chatDialog : chatDialogs) {
            if (chatDialog.equals(currentDialog)) {
                for (ChatMessage chatMessage :  chatDialog.getChatHistory()) {
                    if (chatMessage.getSender().equals(NameOfUser.getText())) {
                        UISend(chatMessage.getContent());
                    } else {
                        UIrececive(chatMessage.getContent());
                    }
                }
                return;
            }
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

    //TODO: A message to notice that a file was sent
    public void UIFileSend(String fileName) throws FileNotFoundException {
        Image image = new Image(new FileInputStream("/home/khanh/IdeaProjects/UI/src/Controller/file.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);

        Text text = new Text("  " + fileName);
        text.setFill(Color.WHITE);
        TextFlow tempFlow = new TextFlow();


        text.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 16));

        tempFlow.setMaxWidth(300);
        TextFlow flow=new TextFlow(tempFlow);

        tempFlow.getStyleClass().add("tempFlow");
        flow.getStyleClass().add("textFlow");
        tempFlow.getChildren().add(imageView);
        tempFlow.getChildren().add(text);


        HBox root = new HBox();
        root.getChildren().addAll(flow);
        root.getStyleClass().add("hbox");
        chatBox.setAlignment(Pos.TOP_RIGHT);
        root.setAlignment(Pos.CENTER_RIGHT);
        Platform.runLater(() -> chatBox.getChildren().addAll(root));


    }


    //TODO: Send message if message field not empty
    public void UISend(String message) throws FileNotFoundException {
        if (sendFile == true){
            UIFileSend(file.getName());

            sendFile = false;

            return;
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
            HBox hbox= new HBox(12);

            tempFlow.getStyleClass().add("tempFlow");
            flow.getStyleClass().add("textFlow");
            chatBox.setAlignment(Pos.TOP_RIGHT);
            hbox.setAlignment(Pos.CENTER_RIGHT);
            hbox.getChildren().add(flow);
            hbox.getStyleClass().add("hbox");
            Platform.runLater(() -> chatBox.getChildren().addAll(hbox));

            //UIFileSend("test");// test UI

            chatField.layout();
            scrollPane.setVvalue(chatBox.getHeight());

            // receiveMess(message); //Test receive message UI

            //code to send message to server

        }
        else {
            System.out.println("null message");//Use to check emty, delete it
        }
    }
    public void sendMess() throws FileNotFoundException {
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
        UISend(message);
        System.out.println(currentDialog.getName());
        currentDialog.addMess(NameOfUser.getText(), message);
        textField.setText("");//set message field
    }

    //TODO: A message to notice that a file was sent
    public void UIFileReceive(String fileName) throws FileNotFoundException {
        Image image = new Image(new FileInputStream("/home/khanh/IdeaProjects/UI/src/Controller/file.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);

        Text text = new Text("  " + fileName);
        text.setFill(Color.WHITE);
        TextFlow tempFlow = new TextFlow();


        text.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 16));

        tempFlow.setMaxWidth(300);
        TextFlow flow = new TextFlow(tempFlow);

        tempFlow.getStyleClass().add("tempFlowFlipped");
        flow.getStyleClass().add("textFlowFlipped");
        tempFlow.getChildren().add(imageView);
        tempFlow.getChildren().add(text);


        HBox root = new HBox();
        root.getChildren().addAll(flow);
        root.getStyleClass().add("hbox");
        chatBox.setAlignment(Pos.TOP_LEFT);
        root.setAlignment(Pos.CENTER_LEFT);
        Platform.runLater(() -> chatBox.getChildren().addAll(root));

        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                System.out.println("Tile pressed ");
                event.consume();
            }
        });
    }

    //TODO: Receive and message and update chatbox
    public void UIrececive(String mess) throws FileNotFoundException {
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

    public void receiveMess(String mess, String person, boolean isGroup) throws FileNotFoundException {
        for (ChatDialog chatDialog : chatDialogs) {
            if (chatDialog.getName().equals(person)) {
                chatDialog.addMess(person, mess);
                if (chatDialog.equals(currentDialog)) {
                    UIrececive(mess);
                }
            }
        }
    }

    //TODO: Get file name and file path to send
    public void getFile(){
        Stage stage = new Stage();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(stage);
        filePath = file.getPath();
        try {
            UISend("file: " + file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String recv;
        if (sendToGr == true) {
            recv = currentGr;
        } else {
            recv = currentReceiver;
        }
        try {
            sender.loadFile(filePath, file.getName(), recv);
            currentDialog.addMess(NameOfUser.getText(), "file: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendFile = true;
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
        if (groupListView.isEmpty()) {
            currentGr = groupName;
        }
        groupListView.add(groupName);
        groupList.setItems(groupListView);
        ChatDialog newChatDialog = new ChatDialog();
        newChatDialog.setFriendName(groupName);
        newChatDialog.setGroup();
        chatDialogs.add(newChatDialog);
        if (currentDialog == null) {
            currentDialog = chatDialogs.get(0);
        }
    }
}
