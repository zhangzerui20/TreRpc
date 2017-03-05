package com.trerpc.server;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.trerpc.protocol.RpcDecoder;
import com.trerpc.protocol.RpcEncoder;
import com.trerpc.protocol.RpcRequest;
import com.trerpc.protocol.RpcResponse;
import com.trerpc.registry.ServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 * 这个类负责在实现类的bean加载后，来启动Netty server，绑定端口，使用Map存下接口和实现类的对应关系，并向Zookeeper注册服务。
 * 这个afterPropertiesSet()方法，会在所有的bean都被加载后被调用。
 * 实现了ApplicationContextAware，是为了，通过ApplicationContext得到所有的bean。
 * @author trecool
 *	
 */
public class RpcServer implements InitializingBean, ApplicationContextAware{

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
	
	private String serverAddress;
	//这里存放的是，接口名和实现类对象的关系
	private Map<String, Object> handlerMap = new HashMap<String, Object>();
	private ServiceRegistry serviceRegistry;
	
	
	public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
		this.serverAddress = serverAddress;
		this.serviceRegistry = serviceRegistry;
	}

	
	public void afterPropertiesSet() throws Exception {
		
		//首先启动Rpc的服务，绑定配置文件中的端口
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
	
				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline()
						.addLast(new RpcEncoder(RpcResponse.class))
						.addLast(new RpcDecoder(RpcRequest.class))
						.addLast(new RpcHandler(handlerMap));
				}
				
			})
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			String[] address = serverAddress.split(":");
			int inetPort = Integer.parseInt(address[1]);
			ChannelFuture future = serverBootstrap.bind(inetPort).sync();
			LOGGER.debug("TreRPC server start on port : " + inetPort);
			
			//启动服务器后，向zk注册服务
			serviceRegistry.register(serverAddress);
			
			//closeFuture()，返回一个channelFutre，当channel被关闭的时候，会通知这个future
			//sync()：等待这个future完成
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}	
	}

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		//这里得到所有带注解的bean
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
		if(!serviceBeanMap.isEmpty()){
			for(Object bean : serviceBeanMap.values()){
				String interfaceName = bean.getClass().getAnnotation(RpcService.class).value().getName();
				handlerMap.put(interfaceName, bean);
			}
		}
	}
	
}
