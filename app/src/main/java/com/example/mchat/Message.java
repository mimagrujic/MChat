package com.example.mchat;

public class Message {
    private String id;
    private String sender;
    private String receiver;
    private String text;
    private  String iv;
    private long time;

    public Message() {}
    public Message(String id, String sender, String receiver, String text, String iv, long time){
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.iv = iv;
        this.time = time;
    }

    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getText() { return text; }
    public String getIv() { return iv; }
    public long getTime() { return time; }

    public void setText(String text) { this.text = text; }
}