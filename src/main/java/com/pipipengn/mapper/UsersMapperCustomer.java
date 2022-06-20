package com.pipipengn.mapper;

import com.pipipengn.pojo.Users;
import com.pipipengn.pojo.vo.FriendRequestVo;
import com.pipipengn.pojo.vo.MyFriendsVo;
import com.pipipengn.utils.MyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsersMapperCustomer extends MyMapper<Users> {
    List<FriendRequestVo> queryFriendRequestList(String acceptUserId);

    List<MyFriendsVo> queryMyFriends(String userId);

    void batchUpdateMsgSigned(List<String> msgIdList);
}
