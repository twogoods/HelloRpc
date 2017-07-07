package com.tg.rpc.core.Serializer;

import java.util.Map;

/**
 * Created by twogoods on 17/2/15.
 */
public class User {
    private int id;
    private Map<String,Object> item;

    public User() {
    }

    public User(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, Object> getItem() {
        return item;
    }

    public void setItem(Map<String, Object> item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", item=" + item +
                '}';
    }
}
