package com.trerpc.protocol;

/**
 * RPC请求的POJO
 * @author trecool
 *
 */

public class RpcRequest {
	//为什么需要一个requestId
	//本框架有两种版本，同步版本和异步版本，在异步版本中，可能发出多条RPC请求，并且受到乱序的RPC回应，这样必须在request和response中都包含一个requestID
	private String requestID;
	
	//下面四个成员，标示了一个特定的方法，服务器端可以使用这些参数来调用特定的方法。
	private String className;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;
	
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
}
