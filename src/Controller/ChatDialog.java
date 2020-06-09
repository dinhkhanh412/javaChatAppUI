package Controller;

import java.util.ArrayList;

public class ChatDialog {
    String friendName = "";
    boolean isGroup = false;
    private ArrayList<ChatMessage> chatHistory = new ArrayList<>();

    public void addMess(String sender, String content) {
        ChatMessage chatMessage = new ChatMessage(sender, content);
        chatHistory.add(chatMessage);
    }

    public ArrayList<ChatMessage> getChatHistory() {
        return chatHistory;
    }

    public String getName() {
        return friendName;
    }
    public void setFriendName(String name) {
        this.friendName = name;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup() {
        isGroup = true;
    }
}
