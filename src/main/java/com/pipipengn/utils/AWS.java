package com.pipipengn.utils;

import software.amazon.awssdk.core.sync.RequestBody;

import java.io.File;
import java.util.Random;
import java.nio.file.Path;


public class AWS {
    public static String AccessKey = "";
    public static String AccessSecret = "";
    public static String S3BucketName = "";

    public static String S3Image0 = "https://weixin-header.s3.us-west-2.amazonaws.com/12f79c1e-9830-40d4-b5ef-48279343d8c3.jpg";
    public static String S3Image1 = "https://weixin-header.s3.us-west-2.amazonaws.com/2c362881-0af2-467e-8bbb-22ea84d92ea8.jpg";
    public static String S3Image2 = "https://weixin-header.s3.us-west-2.amazonaws.com/f1f378db-d42e-441c-9055-8308efe776e9.jpg";
    public static String S3Image3 = "https://weixin-header.s3.us-west-2.amazonaws.com/edd4e3c5-9f71-4b4d-892d-10d64fa94034.jpg";
    public static String S3Image4 = "https://weixin-header.s3.us-west-2.amazonaws.com/66f1ccf1-d060-435b-849c-1429e38ad54b.jpg";
    public static String S3Image5 = "https://weixin-header.s3.us-west-2.amazonaws.com/a22012d6-3f87-44d7-b89d-420bacd673c7.jpg";
    public static String S3QRCode = "https://weixin-header.s3.us-west-2.amazonaws.com/7161654075011_.pic.jpg";


    public static RequestBody fromBytes(byte[] bytes) {
        return RequestBody.fromBytes(bytes);
    }

    public static RequestBody fromFile(File file) {
        return RequestBody.fromFile(file);
    }

    public static RequestBody fromPath(Path file) {
        return RequestBody.fromFile(file);
    }

    public static String randomImg() {
        Random rand = new Random();
        int temp = rand.nextInt(6);
        String img;
        if (temp == 0) {
            img = AWS.S3Image0;
        } else if (temp == 1) {
            img = AWS.S3Image1;
        } else if (temp == 2) {
            img = AWS.S3Image2;
        } else if (temp == 3) {
            img = AWS.S3Image3;
        } else if (temp == 4) {
            img = AWS.S3Image4;
        } else {
            img = AWS.S3Image5;
        }

        return img;
    }
}
