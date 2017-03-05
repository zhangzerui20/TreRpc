package com.trerpc.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * TreRpc为序列化编写的工具类。
 * 使用Protostuff完成序列化和反序列化，比起protobuf的优点在于，它不用编写.proto文件。
 * 使用Objenesis来反射实例化对象。
 * @author trecool
 *
 */

public class SerializationUtil {
	
	//构建schema是一个非常耗时的行为，所以我们为他建立缓存
	//为protostuff的schema(模式)建立缓存
	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();
	private static Objenesis objenesis = new ObjenesisStd(true);
	
	
	//得到一个类型的schema模式的方法
	private static <T> Schema<T> getSchema(Class<T> cls){
		Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
		if (schema == null) {
			//如果缓存中没有
			schema = RuntimeSchema.createFrom(cls);
			if (schema != null) {
				cachedSchema.put(cls, schema);
			}
		}
		return schema;
	}
	
	//序列化的方法，将一个对象转为字节流
	public static <T> byte[] serialize(T obj){
		Class<T> cls = (Class<T>) obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		
		try{
			Schema<T> schema = getSchema(cls);
			return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		}finally {
			buffer.clear();
		}
		
		
	}
	
	//反序列化方法，将一个字节序列转化为对象
	public static <T> T deserialize(byte[] data, Class<T> cls){
		T message = (T)objenesis.newInstance(cls);
		Schema<T> schema = getSchema(cls);
		ProtostuffIOUtil.mergeFrom(data, message, schema);
		return message;
	}
	

	
	
	
}
