package com.pipipengn.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class Users {
    @Id
    private String id;

    private String username;

    private String password;


    @Column(name = "face_image")
    private String faceImage;

    @Column(name = "face_image_big")
    private String faceImageBig;

    private String nickname;

    private String qrcode;

    private String cid;
}