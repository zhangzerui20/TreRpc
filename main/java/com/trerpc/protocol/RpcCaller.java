package com.trerpc.protocol;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

import com.trerpc.client.ConnectManage;
import com.trerpc.client.RpcClientHandler;
import com.trerpc.client.RpcFuture;

/***
 * 
 * @author trecool
 * RpcProxy会返回这个类的对象，通过调用这个类的call方法，实现异步。
 * Caller相当于用户调用和future之间的中间人。
 */
public class RpcCaller {

	private Class<?> interfaceClass;
	private AtomicLong reqID;
	
	public RpcCaller(AtomicLong reqID, Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
		this.reqID = reqID;
	}
	
	/***
	 * 异步的调用方法，返回一个与这个请求相关的Future。
	 * @param methodName : 调用的方法名
	 * @param parameters : 
	 * @return
	 */
	public RpcFuture call(String methodName, Object... parameters){
		try {
			
			//首先需要得到所有的参数的类型
			Class<?>[]	paramTypes = new Class<?>[parameters.length];
			for(int i = 0;i < parameters.length; i++){
				paramTypes[i] = getClassType(parameters[i]);
			}
			
			Method method = interfaceClass.getMethod(methodName, paramTypes);
			RpcRequest req = new RpcRequest();
			req.setClassName(interfaceClass.getName());
			req.setMethodName(methodName);
			req.setParameters(parameters);
			req.setParameterTypes(method.getParameterTypes());
			req.setRequestID(Long.toString(reqID.getAndIncrement()));
			
			//从连接管理中取一个连接出来
			RpcClientHandler handler = ConnectManage.getInstance().chooseHandler();
			RpcFuture future = handler.sendRequest(req);
			return future;
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    private Class<?> getClassType(Object obj){
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName){
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }

        return classType;
    }
	
}
