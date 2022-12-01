package com.quick.util;


public class DataReturn {

    private int statusCode;// 状态代码

    private String message;// 消息

    private Object data;// 数据体

    public DataReturn() {
        super();
    }

    public DataReturn(int statusCode, String message, Object data) {
        super();
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static DataReturn success(String msg) {
        DataReturn result = new DataReturn();
        result.setStatusCode(MsgCode.Success.get());
        result.setMessage(msg);
        return result;
    }

    public static DataReturn success(Object data) {
        DataReturn result = new DataReturn();
        result.setStatusCode(MsgCode.Success.get());
        result.setData(data);
        return result;
    }

    public static DataReturn success(String msg, Object data) {
        DataReturn result = new DataReturn();
        result.setStatusCode(MsgCode.Success.get());
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

    public static DataReturn error(String msg) {
        DataReturn result = new DataReturn();
        result.setStatusCode(MsgCode.Error.get());
        result.setMessage(msg);
        return result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
