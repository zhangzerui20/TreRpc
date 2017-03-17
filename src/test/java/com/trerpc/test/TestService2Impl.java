package com.trerpc.test;

import com.trerpc.server.RpcService;

@RpcService(TestService2.class)
public class TestService2Impl implements TestService2{

	public int add(int a, int b) {
		int c = a + b;
		return c;
	}

}
