package com.trerpc.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trerpc.protocol.RpcDecoder;
import com.trerpc.protocol.RpcEncoder;
import com.trerpc.protocol.RpcRequest;
import com.trerpc.protocol.RpcResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 这个类有两个作用，一方面在这里建立netty服务，connect rpc服务器
 * 另一方面也实现了接口，成为一个InboundHandler
 * @author trecool
 *
 */

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
	private String host;
	private int port;
	
	private RpcResponse response;
	
	//作为收到消息后的通知的信号量
	private final Object obj = new Object();
	
	public RpcClient(String host, int port){
		this.host = host;
		this.port = port;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
		response = msg;
		
		System.out.println("get a response " + response.getResult());
		
		synchronized(obj){
			obj.notifyAll();
		}
	}
	
	public RpcResponse send(RpcRequest request){
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			
			bs.group(group).channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
	
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast(new RpcEncoder(RpcRequest.class))
					.addLast(new RpcDecoder(RpcResponse.class))
					.addLast(RpcClient.this);
				}
			})
			.option(ChannelOption.SO_KEEPALIVE, true);
					
			ChannelFuture future = bs.connect(host, port).sync();
			future.channel().writeAndFlush(request).sync();
			LOGGER.info("send a message " + request);
			
			synchronized(obj){
				obj.wait();
			}
			//if (response != null) {
			//	future.channel().closeFuture().sync();
			//}
			LOGGER.info("get a message " + response);
			return response;
		} catch (InterruptedException e) {
			
		} finally{
			group.shutdownGracefully();
		}
		
		return null;
	}
	
	
}
