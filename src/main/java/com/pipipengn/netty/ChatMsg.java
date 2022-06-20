package com.pipipengn.netty;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMsg implements Serializable {

    private static final long serialVersionUID = -7618209296866016496L;

    private String senderId;
    private String receiverId;
    private String msg;
    private String msgId;
}
