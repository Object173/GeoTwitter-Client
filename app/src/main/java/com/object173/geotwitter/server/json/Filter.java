package com.object173.geotwitter.server.json;

import java.io.Serializable;

public class Filter implements Serializable{

    private String request;
    private int size;
    private int offset;

    public Filter() {
    }

    public Filter(String request, int size, int offset) {
        this.request = request;
        this.size = size;
        this.offset = offset;
    }

    public Filter(int size, int offset) {
        this.size = size;
        this.offset = offset;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
