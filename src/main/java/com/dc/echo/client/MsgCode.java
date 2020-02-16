package com.dc.echo.client;

public class MsgCode {
	public static final int SUCCESS = 200;
	public static final int LOGIN_SUCCESS = 1;
	
	
	
	public static final int LOGIN_ACTION = 2;
	public static final int MESSAGE_TRANS_ACTION = 3;
	/**
	 * 心跳
	 */
	public static final int HEARTBEAT_ACTION = 120;
	
	
	public static final int ERROR = 500;
	
	public static final int USER_EXIST = 501;
	/**
	 * 未注册appkey
	 */
	public static final int UNREG_APPKEY = 502;
	
	
}
