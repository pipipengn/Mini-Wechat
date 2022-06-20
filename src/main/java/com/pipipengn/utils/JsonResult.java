package com.pipipengn.utils;

import lombok.Data;

/**
 * @Description: 自定义响应数据结构
 * 				200：表示成功
 * 				500：表示错误，错误信息在msg字段中
 * 				501：bean验证错误，不管多少个错误都以map形式返回
 * 				502：拦截器拦截到用户token出错
 * 				555：异常抛出信息
 */
@Data
public class JsonResult {

    private Integer status;

    private String msg;

    private Object data;

    private String ok;	// 不使用

    public static JsonResult build(Integer status, String msg, Object data) {
        return new JsonResult(status, msg, data);
    }

    public static JsonResult ok(Object data) {
        return new JsonResult(data);
    }

    public static JsonResult ok() {
        return new JsonResult(null);
    }
    
    public static JsonResult errorMsg(String msg) {
        return new JsonResult(500, msg, null);
    }
    
    public static JsonResult errorMap(Object data) {
        return new JsonResult(501, "error", data);
    }
    
    public static JsonResult errorTokenMsg(String msg) {
        return new JsonResult(502, msg, null);
    }
    
    public static JsonResult errorException(String msg) {
        return new JsonResult(555, msg, null);
    }

    public JsonResult() {

    }

    public JsonResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public JsonResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }

}
