package com.dc.echo.simple;

import java.util.Scanner;

import com.dc.echo.client.EchoCode;
import com.dc.echo.client.EchoConnection;
import com.dc.echo.client.EchoCoreUtils;
import com.dc.echo.client.Message;
import com.dc.echo.client.MessageListener;

import io.netty.channel.ChannelHandlerContext;

public class ClientTest {
	public static void main(String[] args) throws Throwable {
		//EchoConnection conn = new EchoConnection("47.104.77.145", 6666);
		EchoConnection conn = new EchoConnection("localhost", 6666);
		conn.setListener(new MessageListener() {
			@Override
			public void callback(ChannelHandlerContext ctx, byte[] dataByteArr) {
			    Message message = EchoCoreUtils.byteToMessage(dataByteArr);
				if (message.getSender() != null) {
					try {
						if (message.getMsgCode() == EchoCode.MESSAGE_TRANS_ACTION) {
							System.out.println();
							System.out.println("         " + message.getSender() + ":" +new String(message.getContent()));
							System.out.print(message.getReceiver()[0] + ":");
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
				Message message = EchoCoreUtils.byteToMessage(login(conn, username));
				if(message.getMsgCode()==EchoCode.SUCCESS) {
					break;
				}
				if (message.getMsgCode() == EchoCode.USER_EXIST) {// 用户已被注册，请重启程序，并使用新的用户名登录
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
			String content = sc.nextLine();
			if(content!=null && content.length()>0) {
				try {
					Message message = new Message();
					message.setVersion("1.0");
					message.setMsgCode(EchoCode.MESSAGE_TRANS_ACTION);
					message.setReceiver(new String[] {receiver});
					message.setSender(username);
					message.setEncoding(encoding);
					message.setSendTime(System.currentTimeMillis());
					message.setContent(content);
					conn.setSync(false);
					conn.sendMessage(EchoCoreUtils.messageToByteArr(message));//发送消息
				}catch (Exception e) {
					e.printStackTrace();
				}
				System.out.print(username+":");
			}
		}
		sc.close();
	}
	public static byte[] login(EchoConnection conn, String username) throws Throwable {
		System.out.println("正在登录...........................");
		Message message = new Message();
		message.setVersion("1.0");
		message.setMsgCode(EchoCode.LOGIN_ACTION);
		message.setSender(username);
		return conn.sendMessage(EchoCoreUtils.messageToByteArr(message));// 开始登录
	}
}
