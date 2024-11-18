package com.lowwdel.ParrallelComputing.SocketProdCons.BufferServer;

import com.lowwdel.ParrallelComputing.SocketProdCons.utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Message {
    int id;
    long value;
    String timestamp;
    String tag;
    public Message(int id, long value, String timestamp, String tag) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", value=" + value +
                ", timestamp='" + timestamp + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
