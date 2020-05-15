package com.dc.echo.client;

import java.nio.ByteBuffer;



import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<byte[]>{
	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] dataByteArr, ByteBuf out) throws Exception {
		//        try {
		//            out.writeInt(Message.MAGIC_CODE);
		//            out.writeInt(dataByteArr.length);
		//            out.writeBytes(dataByteArr);
		//        }catch (Exception e) {
		//            LOG.error("encoder fail",e);
		//            throw e;
		//        }
		try {
			out.writeInt(dataByteArr.length);
			out.writeBytes(dataByteArr);
		} catch (Exception e) {
			throw e;
		}
	}
}
