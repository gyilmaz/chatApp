package edu.udacity.java.nano.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * WebSocket message model
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    public enum TYPE{
        ENTER, SPEAK, LEAVE
    }

    private TYPE type;
    private String username;
    private String msg;
    private int onlineCount;
}
