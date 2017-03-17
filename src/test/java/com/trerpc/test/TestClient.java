package com.trerpc.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.trerpc.client.RpcFuture;
import com.trerpc.protocol.RpcCaller;
import com.trerpc.protocol.RpcProxy;
import com.trerpc.registry.ServiceDiscovery;
import com.trerpc.utils.SerializationUtil;

public class TestClient {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(TestClient.class);
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		
		/*
		ServiceDiscovery discovery = new ServiceDiscovery("127.0.0.1:2181");
		RpcProxy proxy = new RpcProxy(discovery);
		
		TestService hello = proxy.create(TestService.class);
		String res = hello.helloService("trecool");
		System.out.println(res);
		
		RpcCaller caller = proxy.createAsyn(TestService2.class);
		RpcFuture future = caller.call("add", 5, 7);
		int resa = (Integer) future.get();
		LOGGER.error("res is " + resa);
		
		TimeUnit.SECONDS.sleep(30);
		*/

		long startTime = System.currentTimeMillis();
		for(int i = 0;i < 1000; i++)
		{
			Test test = new Test(1, 2.0, "zcr", 3, 4.0, 5, "test", 6, 7.0, "dd");
			byte[] bytes = SerializationUtil.serialize(test);
	
			FileOutputStream out = null;
			try {
				out = new FileOutputStream("test_file");
				out.write(bytes);
				out.close();
			} catch (FileNotFoundException e) {
				System.out.println("no get the filesdf");
				e.printStackTrace();
			}
				
			Test t1;
			byte[] bb = new byte[bytes.length];
			FileInputStream in = new FileInputStream(new File("test_file"));
			int len = in.read(bb);
			//System.out.println(len);
			in.close();
			t1 = SerializationUtil.deserialize(bb, Test.class);
			//t1.fun();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("get time " + (endTime - startTime));

	/*	
		long startTime = System.currentTimeMillis();
		for(int i = 0;i < 1000 ;i++)
		{
			Test test = new Test(1, 2.0, "zcr", 3, 4.0, 5, "test", 6, 7.0, "dd");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("test_file")));
			oos.writeObject(test);
			oos.flush();
			oos.close();
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("test_file")));
			Test t = (Test) ois.readObject();
			ois.close();
			//t.fun();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("get time " + (endTime - startTime));
	*/	
	}
	
	
}


class Test implements Serializable{
	private int a;
	private double b;
	private String c;
	private int d;
	private double e;
	private int f;
	private String g;
	private int h;
	private double i;
	private String j;
	
	
	
	public Test(int a, double b, String c, int d, double e, int f, String g, int h, double i, String j) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.e = e;
		this.f = f;
		this.g = g;
		this.h = h;
		this.i = i;
		this.j = j;
	}
	public void fun(){
		System.out.println("get a " + a + b + c + d + e + f + g + h + i + j);
	}
}

