package com.dc.echo.client;

public class EchoCode {
	public static final int SUCCESS = 200;
	/**
	 * 登录成功
	 */
	public static final int LOGIN_SUCCESS = 1;
	
	
	/**
	 * 登录动作
	 */
	public static final int LOGIN_ACTION = 2;
	/**
	 * 消息转发动作
	 */
	public static final int MESSAGE_TRANS_ACTION = 3;
	/**
	 * 心跳
	 */
	public static final int HEARTBEAT_ACTION = 120;
	
	/**
	 * 服务器错误
	 */
	public static final int ERROR = 500;
	/**
	 * 用户已存在
	 */
	public static final int USER_EXIST = 501;
	/**
	 * 未注册appkey
	 */
	public static final int UNREG_APPKEY = 502;
}
