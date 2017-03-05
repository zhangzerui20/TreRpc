package com.trerpc.server;

import static org.hamcrest.CoreMatchers.nullValue;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trerpc.protocol.RpcRequest;
import com.trerpc.protocol.RpcResponse;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;

/**
 * rpc请求的处理类，通过请求中的classname找到对应的实现类。
 * 
 * @author trecool
 *
 */

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);
	
	private final Map<String, Object> handlerMap;
	public RpcHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest req) throws Exception {
		RpcResponse response = new RpcResponse();
		
		LOGGER.info("get a meesage ********" + req.getMethodName());
		System.out.println(req.getClassName());
		System.out.println(req.getParameterTypes()[0].getName());
		System.out.println(req.getParameters());
		
		response.setRequestID(req.getRequestID());
		try {
			response.setResult(handle(req));
		} catch (Throwable e) {
			response.setError(e);
		}
		ctx.writeAndFlush(response);
	}
	
	//这里，我们实现的RPC处理是不带状态的，每次都通过反射产生一个新的处理类的对象
	private Object handle(RpcRequest request) throws Throwable{
		String className = request.getClassName();
		Object serviceBean = handlerMap.get(className);
		
		//接下来做反射
		Class<?> serviceClass = serviceBean.getClass();
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();
		
		Method method = serviceClass.getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		Object res = method.invoke(serviceBean, parameters);
		return res;
	}

	
	
}




