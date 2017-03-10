package com.trerpc.protocol;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.trerpc.client.ConnectManage;
import com.trerpc.client.RpcClient;
import com.trerpc.client.RpcClientHandler;
import com.trerpc.client.RpcFuture;
import com.trerpc.registry.ServiceDiscovery;
import com.trerpc.server.RpcHandler;

/**
 * 客户端使用的代理类。控制接口的访问方式。
 * 使用户的调用更像一个本地调用。
 * 动态代理最大的缺点在于只能对接口进行动态代理，不能对类进行。
 * 
 * @author trecool
 *
 */

public class RpcProxy {
	//rpc服务的地址
	private String serverAddress;
	private ServiceDiscovery serviceDiscovery;
	
	//用来产生requestID
	private static AtomicLong reqID;
	static{
		reqID = new AtomicLong();
		reqID.set(0);
	}
	
	//使用spring装配不同的serviceDiscovery
	public RpcProxy(ServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T create(Class<?> interfaceClass){
		T t = (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
			
			//这个方法是反射出来的，当被代理的接口的方法被调用的时候调用的方法
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				//在这个方法中，connect RPC服务器，并且发送请求，期待回应
				RpcRequest req = new RpcRequest();
				long id = reqID.getAndIncrement();
				
				req.setRequestID(Long.toString(id));
				req.setClassName(method.getDeclaringClass().getName());
				req.setMethodName(method.getName());
				req.setParameterTypes(method.getParameterTypes());
				req.setParameters(args);
				
				RpcClientHandler handler = ConnectManage.getInstance().chooseHandler();
				RpcFuture future = handler.sendRequest(req);
				return future.get();
			}
		});
		return t;
	}
	

	public RpcCaller createAsyn(Class<?> interfaceClass){
		RpcCaller call = new RpcCaller(reqID ,interfaceClass);
		return call;
	}
	
	
	

}

class RpcProxyException extends Exception{
	public RpcProxyException() {
		super("no registry node in zk!");
	}
}
