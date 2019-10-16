package edu.udacity.java.nano.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

public class JSONUtil {

    public static String geJSONString(Message.TYPE type, String uname, String message, int count ){
        Message msg= new Message(type, uname, message, count);
        try{
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(msg);
        }catch (IOException e ){
            e.printStackTrace();
        }
        return "Not a json string";
    }

    public static Message getMessageFromJSON (String json){
        Message message= null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(json, Message.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }

}
