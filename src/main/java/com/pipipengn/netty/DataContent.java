package com.pipipengn.netty;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataContent implements Serializable {
    private static final long serialVersionUID = 5419945296519647291L;
    private Integer action;
    private ChatMsg chatMsg;
    private String extand;
}
