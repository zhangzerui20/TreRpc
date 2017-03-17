package com.trerpc.test;

import com.trerpc.server.RpcService;

@RpcService(TestService.class)
public class TestServiceImpl implements TestService{

	public String helloService(String name) {
		System.out.println("hello world! " + ",my name is " + name);
		return "hello world! my name is " + name;
	}

}
