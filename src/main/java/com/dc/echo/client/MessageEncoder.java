package com.dc.echo.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Message>{
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        try {
            out.writeInt(Message.MAGIC_CODE);
            out.writeInt(msg.getHeader().getBytes().length);
            out.writeBytes(msg.getHeader().getBytes());
            if(msg.getBody()!=null) {
                out.writeInt(msg.getBody().length);
                out.writeBytes(msg.getBody());
            }else {
                out.writeInt(0);
            }
        }catch (Exception e) {
            throw e;
        }
    }
}
