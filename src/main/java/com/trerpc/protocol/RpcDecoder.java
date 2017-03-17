package com.trerpc.protocol;

import java.util.List;

import com.trerpc.utils.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * RPC的解码函数，将传来的序列化后的字节流转换成
 * @author trecool
 *
 */

public class RpcDecoder extends ByteToMessageDecoder{
	
	private Class<?> geneicClass;
	public RpcDecoder(Class<?> geneicClass) {
		this.geneicClass = geneicClass;
	}
	
	//将解码后产生的对象加入到out中，就表示解码成功
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//通信的协议是前四个字节的int表示这个结构的长度
		if (in.readableBytes() < 4) {
			return ;
		}
		
		//因为是按一个字节流来接收，所以不一定能接收全一个对象
		//保留标识知道读的字节数超过了dataLength
		in.markReaderIndex();
		
		int dataLength = in.readInt();
		if(dataLength < 0){
			ctx.close();
		}
		if(in.readableBytes() < dataLength){
			in.resetReaderIndex();
			return ;
		}
		
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		
		//反序列化需要一个类型
		Object obj = SerializationUtil.deserialize(data, geneicClass);
		out.add(obj);
	}

}
