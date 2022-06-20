package com.pipipengn.enums;


import java.util.Objects;

public enum SearchFriendsStatusEnum {
	
	SUCCESS(0, "OK"),
	USER_NOT_EXIST(1, "No User..."),
	NOT_YOURSELF(2, "Can't Yourself..."),
	ALREADY_FRIENDS(3, "Already Friend...");
	
	public final Integer status;
	public final String msg;
	
	SearchFriendsStatusEnum(Integer status, String msg){
		this.status = status;
		this.msg = msg;
	}
	
	public Integer getStatus() {
		return status;
	}  
	
	public static String getMsgByKey(Integer status) {
		for (SearchFriendsStatusEnum type : SearchFriendsStatusEnum.values()) {
			if (Objects.equals(type.getStatus(), status)) {
				return type.msg;
			}
		}
		return null;
	}
	
}
