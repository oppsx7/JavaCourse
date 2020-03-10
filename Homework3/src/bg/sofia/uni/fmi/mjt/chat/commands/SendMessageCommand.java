package bg.sofia.uni.fmi.mjt.chat.commands;

import bg.sofia.uni.fmi.mjt.chat.ChatClient;

public class SendMessageCommand {
    private String message;
    private ChatClient sender;
    private ChatClient receiver;


    public SendMessageCommand(String message, ChatClient sender, ChatClient receiver) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public SendMessageCommand(String message, ChatClient sender) {
        this.message = message;
        this.sender = sender;
    }

    public SendMessageCommand(ChatClient sender, ChatClient receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public SendMessageCommand() {
    }


    public String getMessage() {
        return message;
    }


    public ChatClient getSender() {
        return sender;
    }


    public ChatClient getReceiver() {
        return receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
