package com.trerpc.protocol;

import com.trerpc.utils.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 将request/response对象转化为字节数组
 * @author trecool
 * 编码的协议是，先写入一个int型的代表长度的变量，在写入对象
 */


public class RpcEncoder extends MessageToByteEncoder {

	private Class<?> genericClass;
	
	public RpcEncoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		//不仅可以写request/response，也可以写request/reponse的子类
		if(genericClass.isInstance(msg)){
			//这里说明类型是写在object结构中的，因为我传入一个Object类型，也可以正确的编码
			byte[] bytes = SerializationUtil.serialize(msg);
			out.writeInt(bytes.length);
			out.writeBytes(bytes);
		}
		
	}

}
