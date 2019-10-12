package com.dc.echo.client;

import com.alibaba.fastjson.JSON;

public class EchoCoreUtils {
    public static Message getMessByCode(Integer statusCode) {
        Message msg = new Message();
        Header header = new Header();
        header.setStatusCode(statusCode);
        msg.setHeader(JSON.toJSONString(header));
        return msg;
    }
    public static Message getKeepaliveMess() {
        Message msg = new Message();
        Header header = new Header();
        header.setMsgType(EchoCode.HEARTBEAT_ACTION);//心跳
        msg.setHeader(JSON.toJSONString(header));
        return msg;
    }
    public static Message getErrorMess() {
        Message msg = new Message();
        Header header = new Header();
        header.setMsgType(EchoCode.ERROR);//心跳
        msg.setHeader(JSON.toJSONString(header));
        return msg;
    }
}
