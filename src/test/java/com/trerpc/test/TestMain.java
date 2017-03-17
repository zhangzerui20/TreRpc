package com.trerpc.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 启动服务器端的代码
 * 让spring框架跑起来即可
 * @author trecool
 *
 */
public class TestMain{
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
	}
	
}	


