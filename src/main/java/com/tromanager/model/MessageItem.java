package com.tromanager.model;

public class MessageItem {
    private String sender;
    private String room;
    private String content;
    private String time;
    private boolean isOwner;
    private boolean isUrgent;

    public MessageItem(String sender, String room, String content, String time, boolean isOwner, boolean isUrgent) {
        this.sender = sender;
        this.room = room;
        this.content = content;
        this.time = time;
        this.isOwner = isOwner;
        this.isUrgent = isUrgent;
    }

    public String getSender() { return sender; }
    public String getRoom() { return room; }
    public String getContent() { return content; }
    public String getTime() { return time; }
    public boolean isOwner() { return isOwner; }
    public boolean isUrgent() { return isUrgent; }
}