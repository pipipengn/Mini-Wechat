package com.pipipengn.controller;

import com.pipipengn.enums.OperatorFriendRequestTypeEnum;
import com.pipipengn.enums.SearchFriendsStatusEnum;
import com.pipipengn.pojo.Users;
import com.pipipengn.pojo.bo.UsersBo;
import com.pipipengn.pojo.vo.AddFriendVo;
import com.pipipengn.pojo.vo.MyFriendsVo;
import com.pipipengn.pojo.vo.UsersVo;
import com.pipipengn.service.UserService;
import com.pipipengn.utils.AWS;
import com.pipipengn.utils.JsonResult;
import com.pipipengn.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("u")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/registOrLogin")
    public JsonResult registOrLogin(@RequestBody Users user) throws Exception {

        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return JsonResult.errorMsg("username or password is empty");
        }
        boolean isExist = userService.queryUsernameIsExist(user.getUsername());

        Users userResult;
        if (isExist) {
            userResult = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (userResult == null) {
                return JsonResult.errorMsg("wrong user name or password");
            }
        } else {
            userResult = userService.saveUser(user);
        }

        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(userResult, usersVo);

        return JsonResult.ok(usersVo);
    }

    @PostMapping("/uploadFace")
    public JsonResult uploadFace(@RequestBody UsersBo userBo) {

        Users updateUser = new Users();
        updateUser.setId(userBo.getUserId());
        updateUser.setFaceImage(AWS.S3Image0);
        updateUser.setFaceImageBig(AWS.S3Image0);
        Users updateUserInfo = userService.updateUserInfo(updateUser);

        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(updateUserInfo, usersVo);

        return JsonResult.ok(usersVo);
    }

    @PostMapping("/setNickname")
    public JsonResult setNickname(@RequestBody UsersBo userBo) {

        Users updateUser = new Users();
        updateUser.setId(userBo.getUserId());
        updateUser.setNickname(userBo.getNickname());
        Users updateUserInfo = userService.updateUserInfo(updateUser);

        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(updateUserInfo, usersVo);

        return JsonResult.ok(usersVo);
    }

    @PostMapping("/search")
    public JsonResult searchUser(@RequestBody AddFriendVo addFriendVo) {

        String myUserId = addFriendVo.myUserId;
        String friendUsername = addFriendVo.friendUsername;

        // 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)) {
            return JsonResult.errorMsg("Empty");
        }

        // 1. 搜索的用户如果不存在，返回[无此用户]
        // 2. 搜索账号是你自己，返回[不能添加自己]
        // 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (Objects.equals(status, SearchFriendsStatusEnum.SUCCESS.status)) {
            Users user = userService.queryUserInfoByUsername(friendUsername);
            UsersVo userVo = new UsersVo();
            BeanUtils.copyProperties(user, userVo);
            return JsonResult.ok(userVo);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JsonResult.errorMsg(errorMsg);
        }
    }


    @PostMapping("/addFriendRequest")
    public JsonResult addFriendRequest(@RequestBody AddFriendVo addFriendVo) {

        String myUserId = addFriendVo.myUserId;
        String friendUsername = addFriendVo.friendUsername;

        // 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)) {
            return JsonResult.errorMsg("Empty");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (Objects.equals(status, SearchFriendsStatusEnum.SUCCESS.status)) {
            userService.sendFriendRequest(myUserId, friendUsername);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JsonResult.errorMsg(errorMsg);
        }

        return JsonResult.ok();
    }

    @GetMapping("/queryFriendRequests")
    public JsonResult queryFriendRequests(String userId) {

        // 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return JsonResult.errorMsg("Empty");
        }

        // 查询用户接受到的朋友申请
        return JsonResult.ok(userService.queryFriendRequestList(userId));
    }


    @PostMapping("/operFriendRequest")
    public JsonResult operFriendRequest(String acceptUserId, String sendUserId, Integer operType) {

        // acceptUserId sendUserId operType 判断不能为空
        if (StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId) || operType == null) {
            return JsonResult.errorMsg("Empty");
        }

        // 1. 如果operType 没有对应的枚举值，则直接抛出空错误信息
        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
            return JsonResult.errorMsg("Empty");
        }

        if (operType.equals(OperatorFriendRequestTypeEnum.IGNORE.type)) {
            // 2. 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        } else if (operType.equals(OperatorFriendRequestTypeEnum.PASS.type)) {
            // 3. 判断如果是通过好友请求，则互相增加好友记录到数据库对应的表 然后删除好友请求的数据库表记录
            userService.passFriendRequest(sendUserId, acceptUserId);
        }

        // 4. 数据库查询好友列表
        List<MyFriendsVo> myFirends = userService.queryMyFriends(acceptUserId);

        return JsonResult.ok(myFirends);
    }


    @GetMapping("/myFriends")
    public JsonResult myFriends(String userId) {
        if (StringUtils.isBlank(userId)) {
            return JsonResult.errorMsg("");
        }
        List<MyFriendsVo> myFirends = userService.queryMyFriends(userId);

        return JsonResult.ok(myFirends);
    }


    @GetMapping("/getUnReadMsgList")
    public JsonResult getUnSignedMsgList(String acceptUserId) {
        // userId 判断不能为空
        if (StringUtils.isBlank(acceptUserId)) {
            return JsonResult.errorMsg("");
        }

        // 查询列表
        List<com.pipipengn.pojo.ChatMsg> unSignedMsgList = userService.getUnSignedMsgList(acceptUserId);

        return JsonResult.ok(unSignedMsgList);
    }
}
