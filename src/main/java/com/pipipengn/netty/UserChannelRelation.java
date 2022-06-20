package com.pipipengn.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class UserChannelRelation {
    private static Map<String, Channel> map = new HashMap<>();

    public static void put(String senderId, Channel channel) {
        map.put(senderId, channel);
    }

    public static Channel get(String senderId) {
        return map.get(senderId);
    }

    public static void output() {
        for (HashMap.Entry<String, Channel> entry : map.entrySet()) {
            System.out.println("UserId: " + entry.getKey() + ", ChannelId: " + entry.getValue().id().asLongText());
        }
    }
}
