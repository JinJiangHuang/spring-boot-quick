package com.quick.util;

/**
 * 消息编号
 * <p>
 * 按照以下规范添加
 */
public enum MsgCode {

    Success(1),// 成功
    Prompt(2),// 提示
    Warning(3),// 警告
    Error(4),// 错误
    Unauth(5),//权限异常
    SessionInvalid(6),//会话失效
    ;

    private Integer value;

    MsgCode(Integer v) {
        this.value = v;
    }

    public Integer get() {
        return value;
    }

}
