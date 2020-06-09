package Controller;

public class ChatMessage {
    private String sender;
    private String content;

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }
    public String getContent() {
        return content;
    }
    public String getSender() {
        return sender;
    }
}
