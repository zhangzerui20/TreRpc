package com.trerpc.test;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;

import org.junit.Ignore;
import org.junit.Test;

import com.trerpc.client.RpcFuture;
import com.trerpc.protocol.RpcCaller;
import com.trerpc.protocol.RpcProxy;
import com.trerpc.registry.ServiceDiscovery;

import junit.framework.TestCase;

public class TreRpcTest {

	@Test
	public void testSyn() {
		ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
		RpcProxy proxy = new RpcProxy(serviceDiscovery);
		
		TestService hello = proxy.create(TestService.class);
		String res = hello.helloService("trecool");
		
		assertTrue(res.equals("hello world! my name is trecool"));
	}
	
	@Test
	public void testAsyn() throws InterruptedException, ExecutionException {
		ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
		RpcProxy proxy = new RpcProxy(serviceDiscovery);		
		RpcCaller caller = proxy.createAsyn(TestService2.class);
		RpcFuture future = caller.call("add", 5, 7);
		int resa = (Integer) future.get();
		
		TestCase.assertEquals(resa, 12);
	}

}
