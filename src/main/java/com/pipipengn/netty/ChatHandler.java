package com.pipipengn.netty;

import com.pipipengn.enums.MsgActionEnum;
import com.pipipengn.service.UserService;
import com.pipipengn.utils.JsonUtils;
import com.pipipengn.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
        Channel currentChannel = ctx.channel();


        // 1. 获取客户端发来的消息
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        assert dataContent != null;
        Integer action = dataContent.getAction();

        // 2. 如果是初始化的时候，把channel id 和 userid关联
        if (Objects.equals(action, MsgActionEnum.CONNECT.type)) {
            System.out.println(dataContent.getChatMsg().getSenderId());
            System.err.println("Init Connection");

            String senderId = dataContent.getChatMsg().getSenderId();
            UserChannelRelation.put(senderId, currentChannel);

            for (Channel user : users) {
                System.out.println(user.id().asLongText());
            }
        }

        // 3. 如果是聊天类型，把聊天记录保存到数据库，标记消息签收状态
        else if (Objects.equals(action, MsgActionEnum.CHAT.type)) {
            ChatMsg chatMsg = dataContent.getChatMsg();
            String receiveid = chatMsg.getReceiverId();

            // 保存数据库
            UserService userService = (UserService) SpringUtil.getBean("userService");
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            // 新建返回对象
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            Channel receiverChannel = UserChannelRelation.get(receiveid);
            if (receiverChannel == null) {
                // TODO channel为空代表用户离线，推送消息
                System.out.println("receiverChannel为空代表用户离线，推送消息");
            } else {
                // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                Channel findChannel = users.find(receiverChannel.id());
                if (findChannel != null) {
                    // 用户在线
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContentMsg)));
                } else {
                    // 用户离线 TODO 推送消息
                    System.out.println("用户离线 推送消息");
                }
            }
        }

        // 4. 签收消息
        else if (Objects.equals(action, MsgActionEnum.SIGNED.type)) {
            UserService userService = (UserService) SpringUtil.getBean("userService");

            // 扩展字段在signed类型的消息中，代表需要去签收的消息id，逗号间隔
            String msgIdsStr = dataContent.getExtand();
            String[] msgIds = msgIdsStr.split(",");

            List<String> msgIdList = new ArrayList<>();
            for (String mid : msgIds) {
                if (StringUtils.isNotBlank(mid)) {
                    msgIdList.add(mid);
                }
            }

            System.out.println(msgIdList);

            // 批量签收
            if (!msgIdList.isEmpty()) {
                userService.updateMsgSigned(msgIdList);
            }
        }

        // 5. 心跳类型
        else if (Objects.equals(action, MsgActionEnum.KEEPALIVE.type)) {
            System.out.println("收到来自channel为[" + currentChannel + "]的心跳...");
        }
    }

    // 当客户端连接服务器后，获取客户端当channel，并放到ChannelGroup中去管理
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当执行时，ChannelGroup会自动移除客户端channel
        users.remove(ctx.channel());
        System.err.println("Remove Channel");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常后关闭channel，随后从group中删除
        ctx.channel().close();
        users.remove(ctx.channel());
        System.err.println("Error Remove Channel");
    }
}

