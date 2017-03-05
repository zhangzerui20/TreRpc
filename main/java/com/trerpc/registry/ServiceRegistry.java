package com.trerpc.registry;

import java.io.IOException;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 向Zookeeper注册
 * @author trecool
 *
 */

public class ServiceRegistry {
	private String registryAddress;
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
	private CountDownLatch latch = new CountDownLatch(1);
	
	public ServiceRegistry(String registryAddress) {
		this.registryAddress = registryAddress;
	}
	
	
	/**
	 * 注册的方法
	 * @param data
	 * data表示要注册的内容，这里指的是RPC服务的地址:端口
	 * @return
	 */
	public boolean register(String data){
		if(data != null){
			ZooKeeper zk = connectServer();
			if (zk != null) {
				createNode(zk, data);
			}
			return true;
		}
		return false;
	} 
	
	//zk中所有的操作都是异步操作，所以一般会注册回调
	private ZooKeeper connectServer(){
		ZooKeeper zk = null;
		
		try {
			//这里是书中的写法
			//watch是zookeeper中的回调机制
			zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher(){
				//下面这个函数是，有事件发生时，执行的函数。
				public void process(WatchedEvent event) {
					if(event.getState() == Event.KeeperState.SyncConnected){
						latch.countDown();
					}
				}	
			});
			latch.await();
			
		} catch (IOException e) {
			LOGGER.error("error ", e);
		} catch (InterruptedException e) {
			LOGGER.error("error ", e);
		}	
		return zk;
	}
	
	private void createNode(ZooKeeper zk, String data){
		try {
			byte[] bytes = data.getBytes();
			//第一个参数是zk服务器中的路径，因为最后一个参数指定的是创建以个临时的顺序节点，每次创建一个节点会在名字后面自动添加一个数字，ACL属性指定了访问权限
			//返回值是完整的zk中的路径，打印在日志中。
			String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			LOGGER.debug("create zookeeper node ({} => {})", path, data);
		} catch (KeeperException e) {
			LOGGER.error("error ", e);
		} catch (InterruptedException e) {
			LOGGER.error("error ", e);
		}
	}
	
	
}
