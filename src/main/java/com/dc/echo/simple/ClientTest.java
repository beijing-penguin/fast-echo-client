package com.dc.echo.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.alibaba.fastjson.JSON;
import com.dc.echo.client.EchoCode;
import com.dc.echo.client.EchoConnection;
import com.dc.echo.client.Header;
import com.dc.echo.client.Message;
import com.dc.echo.client.MessageListener;

import io.netty.channel.ChannelHandlerContext;

public class ClientTest {
	public static void main(String[] args) throws Throwable {
		EchoConnection conn = new EchoConnection("47.104.77.145", 3333);
		conn.setListener(new MessageListener() {
			@Override
			public void callback(ChannelHandlerContext ctx, Message message) {
				Header header = JSON.parseObject(message.getHeader(), Header.class);
				if (header.getSender() != null) {
					try {
						if (header.getMsgType() == EchoCode.MESSAGE_TRANS_ACTION) {
							System.out.println();
							System.out.println("         " + header.getSender() + ":" +new String(message.getBody()));
							System.out.print(header.getReceiver()[0] + ":");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		conn.setReadTimeOut(10);//10s超时
		conn.setSync(true);//开启同步请求
		conn.connect();
		
		String encoding = System.getProperty("file.encoding");
		Scanner sc = new Scanner(System.in,System.getProperty("file.encoding"));
		System.out.println(System.getProperty("file.encoding"));
		String username = null;
		while(true) {
			System.out.print(new String("输入自己的用户名："));
			username = sc.nextLine();
			try {
				Message message = login(conn, username);
				if(JSON.parseObject(message.getHeader(), Header.class).getStatusCode()==EchoCode.SUCCESS) {
					break;
				}
				if (JSON.parseObject(message.getHeader(), Header.class).getStatusCode() == EchoCode.USER_EXIST) {// 用户已被注册，请重启程序，并使用新的用户名登录
					System.out.println("用户已被注册，请使用新的用户名登录");
				}
			}catch (Throwable e) {
				System.out.print("登录失败");
				e.printStackTrace();
			}
			System.out.print(new String("输入自己的用户名："));
		}

		System.out.print("输入对方的用户名：");
		String receiver = sc.nextLine();
		System.out.print(username+":");
		while(sc.hasNextLine()) {
			String context = sc.nextLine();
			if(context!=null && context.length()>0) {
				try {
					Message message = new Message();
					Map<String, Object> headerMap = new HashMap<String, Object>();
					headerMap.put("version", "1.0");
					headerMap.put("msgType", EchoCode.MESSAGE_TRANS_ACTION);//发送消息
					headerMap.put("receiver", new String[] {receiver});
					headerMap.put("sender", username);
					headerMap.put("encoding", encoding);
					headerMap.put("sendTime", System.currentTimeMillis());
					message.setHeader(JSON.toJSONString(headerMap));

					message.setBody(context.getBytes());
					conn.setSync(false);
					conn.sendMessage(message);//发送消息
				}catch (Exception e) {
					e.printStackTrace();
				}
				System.out.print(username+":");
			}
		}
		sc.close();
	}
	public static Message login(EchoConnection conn, String username) throws Throwable {
		System.out.println("正在登录...........................");
		Message message = new Message();
		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("version", "1.0");
		headerMap.put("msgType", EchoCode.LOGIN_ACTION);// 登录
		headerMap.put("sender", username);
		message.setHeader(JSON.toJSONString(headerMap));
		return conn.sendMessage(message);// 开始登录
	}
}
