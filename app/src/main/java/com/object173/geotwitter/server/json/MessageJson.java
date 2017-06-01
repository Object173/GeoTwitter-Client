package com.object173.geotwitter.server.json;

import com.object173.geotwitter.database.entities.Message;

import java.util.Date;

public class MessageJson {

    private long id;
    private long senderId;
    private long dialogId;
    private String text;
    private Date date;

    private String imageUrl;
    private MarkerJson marker;

    public MessageJson() {
    }

    public static MessageJson newInstance(final Message message) {
        if(message == null) {
            return null;
        }
        final MessageJson messageJson = new MessageJson();
        messageJson.senderId = message.getSenderId();
        messageJson.dialogId = message.getDialogId();
        messageJson.text = message.getText();
        messageJson.date = message.getDate();

        if(message.getMarker() != null) {
            messageJson.marker = MarkerJson.newInstance(message.getMarker());
        }
        return messageJson;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getDialogId() {
        return dialogId;
    }

    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MarkerJson getMarker() {
        return marker;
    }

    public void setMarker(MarkerJson marker) {
        this.marker = marker;
    }
}
