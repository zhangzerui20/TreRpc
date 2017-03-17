package com.trerpc.client;

import com.trerpc.protocol.RpcDecoder;
import com.trerpc.protocol.RpcEncoder;import com.trerpc.protocol.RpcRequest;
import com.trerpc.protocol.RpcResponse;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class RpcAsynInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new RpcEncoder(RpcRequest.class));
		ch.pipeline().addLast(new RpcDecoder(RpcResponse.class));
		ch.pipeline().addLast(new RpcClientHandler());
	}

}
