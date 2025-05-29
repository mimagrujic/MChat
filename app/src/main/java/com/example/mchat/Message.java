package com.example.mchat;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private String id;
    private String sender;
    private String receiver;
    private String text;
    private  String iv;
    private long time;
    private Map<String, Boolean> visibleTo = new HashMap<>();

    public Message() {}
    public Message(String id, String sender, String receiver, String text, String iv, long time){
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.iv = iv;
        this.time = time;
        visibleTo.put(sender, true);
        visibleTo.put(receiver, true);
    }

    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getText() { return text; }
    public String getIv() { return iv; }
    public long getTime() { return time; }
    public Map<String, Boolean> getVisibleTo() { return visibleTo; }

    public void setText(String text) { this.text = text; }
    public void setVisibleTo(Map<String, Boolean> visibleTo) { this.visibleTo = visibleTo; }
}