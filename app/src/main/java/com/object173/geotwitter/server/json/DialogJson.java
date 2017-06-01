package com.object173.geotwitter.server.json;

import java.util.List;

public class DialogJson {

    private long id;
    private AuthProfile companion;
    private List<MessageJson> messageList;

    public DialogJson() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public AuthProfile getCompanion() {
        return companion;
    }

    public void setCompanion(AuthProfile companion) {
        this.companion = companion;
    }

    public List<MessageJson> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<MessageJson> messageList) {
        this.messageList = messageList;
    }
}
