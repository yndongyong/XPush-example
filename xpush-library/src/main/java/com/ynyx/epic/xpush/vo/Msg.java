package com.ynyx.epic.xpush.vo;

import java.io.Serializable;

/**
 * Created by Dong on 2016/4/22.
 */
public class Msg implements Serializable {
    private String id;
    private String to;
    private String from;
    private String type;
    private String body;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
