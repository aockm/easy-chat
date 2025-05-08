package com.easychat.websorcket;

import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.WsInitData;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.ChatSessionQuery;
import com.easychat.entity.query.ChatSessionUserQuery;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.enums.MessageTypeEnum;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.mappers.UserInfoMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionService;
import com.easychat.service.ChatSessionUserService;
import com.easychat.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ChannelContextUtils {

    public static final ConcurrentHashMap<String, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, ChannelGroup> GROUP_CONTEXT_MAP = new ConcurrentHashMap<>();

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private ChatSessionUserService chatSessionUserService;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;


    public void addContext(String userId, Channel channel){
        String channelId = channel.id().toString();
        AttributeKey attributeKey = null;
        if(!AttributeKey.exists(channelId)) {
            attributeKey = AttributeKey.newInstance(channelId);
        }else {
            attributeKey = AttributeKey.valueOf(channelId);
        }

        channel.attr(attributeKey).set(userId);
        List<String> userContactList = redisComponent.getUserContactList(userId);
        for (String groupId : userContactList) {
            if (groupId.startsWith(UserContactTypeEnum.GROUP.getPrefix())) {
                add2Group(groupId, channel);
            }
        }


        USER_CONTEXT_MAP.put(userId, channel);
        redisComponent.saveUserHeartBeat(userId);

        // 更新用户最后连接时间
        UserInfo updateInfo = new UserInfo();
        // userInfo.setLastLoginTime(new Date());
        //        userInfo.

        // 给用户发送消息
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        //userInfo.getLastOffTime()

        // 1.查询会话信息
        ChatSessionUserQuery chatSessionUserQuery = new ChatSessionUserQuery();
        chatSessionUserQuery.setUserId(userId);
        chatSessionUserQuery.setOrderBy("last_receive_time desc");
        List<ChatSessionUser> chatSessionUserList = chatSessionUserService.findListByParam(chatSessionUserQuery);

        WsInitData wsInitData = new WsInitData();
        wsInitData.setChatSessionUserList(chatSessionUserList);
        // 2.查询聊天信息

        // 3.查询好友申请

        // 发送消息
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.INIT.getType());
        messageSendDto.setContactId(userId);
        messageSendDto.setExtendData(wsInitData);
        sendMsg(messageSendDto,userId);
    }

    // 发送消息
    public static void sendMsg(MessageSendDto messageSendDto,String receiveId){
        if (receiveId == null) {
            return;
        }
        Channel sendChannel = USER_CONTEXT_MAP.get(receiveId);
        if (sendChannel == null) {
            return;
        }
        // 相对于客户端而言，联系人就是发送人，这里转发一下再发送
        messageSendDto.setContactId(messageSendDto.getContactId());
        messageSendDto.setContactName(messageSendDto.getSendUserNickName());
        sendChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));
    }


    private void add2Group(String groupId, Channel channel){
        ChannelGroup group = GROUP_CONTEXT_MAP.get(groupId);
        if(group == null){
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            GROUP_CONTEXT_MAP.put(groupId, group);
        }
        if(channel == null){
           return;
        }
        group.add(channel);
    }

    public void removeContext(Channel channel) {
        Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
        String userId = attribute.get();
        if(StringUtils.isEmpty(userId)){
            USER_CONTEXT_MAP.remove(userId);
        }
        redisComponent.removeUserHeartBeat(userId);

        // 更新用户最后离线时间
    }


}
