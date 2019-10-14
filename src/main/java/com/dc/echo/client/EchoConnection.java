package com.dc.echo.client;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoConnection{

	public static CountDownLatch  resultWait;

	private static Message message = null;

	private int readTimeOut = 10;//秒
	private EventLoopGroup group;
	private Bootstrap bootstrap;
	private String host;
	private int port;
	private Channel channel;
	private MessageListener listener;

	// 是否同步获取响应结果
	private boolean sync = false;

	private int keepaliveTimeout = 5;//心跳间隔秒

	public Thread keepaliveThread;

	public EchoConnection(String host,int port){
		this.host = host;
		this.port = port;
	}
	public EchoConnection connect() throws Throwable{
		try {
			if(group==null) {
				group = new NioEventLoopGroup(1);
			}
			//开启心跳程序
			keepaliveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while(keepaliveThread!=null) {
						try {
							Thread.sleep(keepaliveTimeout*1000);
							sendMessage(EchoCoreUtils.getKeepaliveMess());
						} catch (Throwable e) {
							return;
						}
					}
				}
			});
			keepaliveThread.start();
			if(bootstrap==null) {
				bootstrap = new Bootstrap()
						.group(group)
						.channel(NioSocketChannel.class)
						//.option(ChannelOption.SO_KEEPALIVE, true)
						.option(ChannelOption.SO_REUSEADDR, true)
						//禁用Nagle，Nagle算法就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
						.option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel ch) throws Exception {
								ch.config().setAllowHalfClosure(true);
								ChannelPipeline pipeline = ch.pipeline();
								//                pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
								//                pipeline.addLast("decoder", new StringDecoder());
								//                pipeline.addLast("encoder", new StringEncoder());
								pipeline.addLast("decoder", new MessageDecoder());
								pipeline.addLast("encoder", new MessageEncoder());
								pipeline.addLast("handler", new SimpleChannelInboundHandler<Message>() {
									@Override
									protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
										if(sync) {
											EchoConnection.message = message;
											resultWait.countDown();
										}else {
											if(listener!=null) {
												listener.callback(ctx,message);
											}
										}
									}

									@Override
									public void channelActive(ChannelHandlerContext ctx) throws Exception {
										System.out.println("Client active ");
										super.channelActive(ctx);
									}

									@Override
									public void channelInactive(ChannelHandlerContext ctx) throws Exception {
										System.out.println("Client close ");
										ctx.channel().close();
									}
									@Override
									public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
										System.out.println("链接异常中断");
										close();
									}
									@Override
									public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
										System.err.println("channelRegistered");
									}

									@Override
									public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
										System.err.println("channelUnregistered");
										close();
									}

									@Override
									public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
										System.err.println("userEventTriggered");
										ctx.close();
									}
								});
							}
						});
			}
			channel = bootstrap.connect(host, port).sync().channel();
		}catch (Throwable e) {
			this.close();
			throw e;
		}
		return this;
	}
	public Message sendMessage(Message message,MessageListener listener) throws Throwable{
		if(sync) {
			EchoConnection.message=null;
			EchoConnection.resultWait = new CountDownLatch(1);
		}
		if(listener!=null ) {
			this.listener = listener;
		}
		if(channel==null || !channel.isOpen() || !channel.isActive()) {
			if(channel==null || !channel.isOpen() || !channel.isActive()) {
				throw new Exception("channel break,Unable to connect to server");
			}
		}
		channel.writeAndFlush(message);
		if(sync) {
			EchoConnection.resultWait.await(readTimeOut,TimeUnit.SECONDS);
			if(EchoConnection.resultWait.getCount()!=0) {
				EchoConnection.message = null;
				throw new Exception("send fail");
			}
			return EchoConnection.message;
		}else {
			return null;
		}
	}
	public Message sendMessage(Message message) throws Throwable{
		return sendMessage(message, null);
	}
	public void close() {
		if(channel!=null){
			channel.close();
			channel=null;
		}

		if(group!=null) {
			group.shutdownGracefully();
			group = null;
		}
		if(keepaliveThread!=null) {
			keepaliveThread = null;
		}
		bootstrap= null;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	public int getReadTimeOut() {
		return readTimeOut;
	}
	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}
	public static Message getMessage() {
		return message;
	}
	public static void setMessage(Message message) {
		EchoConnection.message = message;
	}
	public boolean isSync() {
		return sync;
	}
	public EchoConnection setSync(boolean sync) {
		this.sync = sync;
		return this;
	}
	public MessageListener getListener() {
		return listener;
	}
	public EchoConnection setListener(MessageListener listener) {
		this.listener = listener;
		return this;
	}
}
