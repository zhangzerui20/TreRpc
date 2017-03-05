package com.trerpc.protocol;

/**
 * RPC回应的POJO
 * @author trecool
 *
 */

public class RpcResponse {
	private String requestID;
	
	//RPC框架需要处理错误
	private Throwable error;
	//处理的返回值
	private Object result;
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	public Throwable getError() {
		return error;
	}
	public void setError(Throwable error) {
		this.error = error;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
}
