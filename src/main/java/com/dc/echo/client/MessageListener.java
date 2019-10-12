package com.dc.echo.client;

import io.netty.channel.ChannelHandlerContext;

public interface MessageListener {
    public void callback(ChannelHandlerContext ctx, Message message);
}
