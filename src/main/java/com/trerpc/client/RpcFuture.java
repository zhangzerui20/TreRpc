package com.trerpc.client;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.client.ResponseActions;

import com.trerpc.protocol.RpcRequest;
import com.trerpc.protocol.RpcResponse;


/**
 * treRPC的future类，模仿future的写法，使用AQS实现future的同步。
 * @author trecool
 *
 * @param <T>
 */
public class RpcFuture implements Future{
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcFuture.class);
	private RpcResponse response;
	
	private Sync sync;
	private RpcRequest request;
	
	public RpcFuture(RpcRequest req){
		this.request = req;
		sync = new Sync();
	}
	
	
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException();
	}

	public boolean isCancelled() {
		throw new UnsupportedOperationException();
	}

	public boolean isDone() {
		return sync.isDone();
	}

	public Object get() throws InterruptedException, ExecutionException {
		sync.acquire(-1);
		//这里acquire就相当于已经获得了锁了，所以下面这个直接使用
		if(this.response != null){
			return this.response.getResult();
		}else{
			return null;
		}
	}

	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
		if(success){
			if(response != null){
				return response;
			}else{
				return null;
			}
		}
		return null;
	}

	//读取到rpc服务器的回应信息后，需要调用这个函数设置response到对应的request中
	public void done(RpcResponse response){
		this.response = response;
		sync.release(1);
	}
	
	static class Sync extends AbstractQueuedSynchronizer{
		
		//定义，RpcFuture的整形的状态
		private final int done = 1;
		private final int pending = 0;
		
		@Override
		protected boolean tryAcquire(int arg) {
			if (getState() == 1) {
				return true;
			}
			else 
				return false;
		}
		@Override
		protected boolean tryRelease(int arg) {
			if (getState() == pending) {
				if(compareAndSetState(pending, done))
					return true;
			}
			return false;
		}
		
		public boolean isDone(){
			return getState() == done;
		}
		
		
	}
	
	
}
