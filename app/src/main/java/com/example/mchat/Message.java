package com.example.mchat;

public class Message {
    private String sender;
    private String text;
    private long time;

    public Message() {}
    public Message(String sender, String text, long time){
        this.sender = sender;
        this.text = text;
        this.time = time;
    }

    public String getSender() { return sender; }
    public String getText() { return text; }
    public long getTime() { return time; }

}