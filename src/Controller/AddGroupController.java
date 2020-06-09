package Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddGroupController implements Initializable {
    @FXML
    private CheckComboBox<String> addList;

    @FXML
    private JFXTextField groupName;

    @FXML
    private JFXButton button;
    private ChatClient chatClient;
    private SendThread sender;
    private ChatUIController chatUIController;

    //private ObservableList<String> onlineList = FXCollections.observableArrayList("khanh", "dat", "jaanh");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
;
    }

    //TODO: get online list to add to group
    public void getOnlineList(ObservableList<String> strings){
        addList.getItems().addAll(strings);
    }

    //TODO: add group button click event
    public void create(ActionEvent actionEvent) {
        String grName = groupName.getText();
        ObservableList memList = addList.getCheckModel().getCheckedItems();

        if (memList == null) return;

        String memName = "";
        for (Object obj : memList ) {
            memName += obj.toString() + "\n";
        }
        if (memName != "") {
            sender.requestNewGroup(grName, memName);
            chatUIController.newGr(grName);
        }
        return;
    }

    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    public void setSender(SendThread sender) {
        this.sender = sender;
    }
    public void setChatUIController(ChatUIController chatUIController) {
        this.chatUIController = chatUIController;
    }
}
