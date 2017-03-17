package com.trerpc.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trerpc.protocol.RpcRequest;
import com.trerpc.protocol.RpcResponse;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 实现了读和写的方法
 * 代表和一个rpc服务器的连接
 * @author trecool
 *
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientHandler.class);
	
	//这个map应该可以全局只有一个，现在这样是一个连接对应一个.
	private ConcurrentHashMap<String, RpcFuture> pendingRPC = new ConcurrentHashMap<String, RpcFuture>();
	private volatile Channel channel;
	private SocketAddress remotePeer;
	
	/**
	 * 读取到response
	 * 1.从map中找到对应的future
	 * 2.设置future对应域的值
	 * 3.调用future的done，表明该操作已经完成
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
		String requestId = msg.getRequestID();
		RpcFuture future = pendingRPC.get(requestId);
		if(future != null){
			pendingRPC.remove(requestId);
			future.done(msg);
		}
		
		
	}

	//channel的状态，先open，open‘注册’后是registered，然后连接完成之后是avtived
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		remotePeer = ctx.channel().remoteAddress();
	}


	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		channel = ctx.channel();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("client caught exception!");
		ctx.close();
	}
	
	public Channel getChannel(){
		return channel;
	}
	
	public void close(){
		channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	public InetSocketAddress getRemoteAddr(){
		return (InetSocketAddress) this.remotePeer;
	}
	
	public RpcFuture sendRequest(RpcRequest req){
		RpcFuture future = new RpcFuture(req);
		pendingRPC.put(req.getRequestID(), future);
		channel.writeAndFlush(req);
		
		return future;
	}
	
}
