package com.dc.echo.client;

import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandlerContext;

public class ClientTest {
	public static void main(String[] args) {
		try {
			EchoConnection conn = new EchoConnection("localhost", 1314).connect();
			conn.setReadTimeOut(10);//10s超时
			
			//设置消息接收监听
			conn.setListener(new MessageListener() {
				@Override
				public void callback(ChannelHandlerContext ctx, Message message) {
					System.out.println(JSON.toJSONString(message));
				}
			});
			Message message = new Message();
			message.setBody("你好".getBytes());
			
			Header header = new Header();
			header.setSender("dc");//唯一发送者标识，IM场景下通常使用用户ID。
			//header.setAppKey("94ecf658-bee6-48bb-9e27-07e72d032f65");//通信秘钥
			header.setMsgType(EchoCode.MESSAGE_TRANS_ACTION);//消息转发 动作
			header.setReceiver(new String[] {"ly"});//唯一接受者标识，IM场景下通常为用户ID
			
			message.setHeader(JSON.toJSONString(header));
			
			conn.sendMessage(message);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
