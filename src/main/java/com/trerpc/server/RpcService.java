package com.trerpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * rpc服务接口的实现类，需要使用这个注解来表明，将这个实现类作为远程接口的实现。
 * 
 * @author trecool
 * 第一个参数表示，这个注解修饰的类型，可以修饰class
 * 第二个参数表示，注解的声明周期，这个表示整个程序运行时都存在
 * 第三个参数表示，被注解的类会被spring发现，并实例化
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
	Class<?> value();
}
