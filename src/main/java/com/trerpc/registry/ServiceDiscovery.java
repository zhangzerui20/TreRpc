package com.trerpc.registry;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trerpc.client.ConnectManage;

import io.netty.util.internal.ThreadLocalRandom;

/**
 * 在客户端使用的服务发现类
 * @author trecool
 *
 */

public class ServiceDiscovery {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);
	private CountDownLatch latch = new CountDownLatch(1);
	private String discoveryAddress;
	
	private volatile List<String> dataList = new ArrayList<String>();
	
	public ServiceDiscovery(String discoveryAddress) {
		this.discoveryAddress = discoveryAddress;
		ZooKeeper zk = connectServer();
		if (zk != null) {
			watchNode(zk);
		}
		
	}
	//返回zk节点中的地址
	//这里使用策略，到底返回哪一个rpc服务器的地址(均衡负载)
	
	public String discovery(){
		String data = null;
		int size = dataList.size();
		if(size == 1){
			data = dataList.get(0);
			LOGGER.error("only one node :" + data);
		}
		else if(size > 1){
			data = dataList.get(ThreadLocalRandom.current().nextInt(size));
			LOGGER.error("get the random data :" + data);
		}
		return data;
	}
	
	
	private ZooKeeper connectServer(){
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(discoveryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher(){
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected) {
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
	
	//得到zk中的节点列表，并注册一个watcher，每当有节点变化的时候，更新这个列表
	//这里的zk用作final，是因为匿名内部类中的参数变量必须为final类型
	private void watchNode(final ZooKeeper zk){
		try {
			//这里得到所有node的全路径
			List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher(){
				public void process(WatchedEvent event) {
					if(event.getType() == Event.EventType.NodeChildrenChanged){
						watchNode(zk);
					}
				}	
			});	
			//调用api从路径名得到node的内容
			List<String> dataList = new ArrayList<String>();
			for(String node : nodeList){
				byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
				dataList.add(new String(bytes));
			}
			
			ConnectManage.getInstance().updateConnectedServer(dataList);
			
			LOGGER.debug("node data:{}", dataList);
			this.dataList = dataList;
		} catch (KeeperException e) {
			LOGGER.error("error", e);
		} catch (InterruptedException e) {
			LOGGER.error("error", e);
		}
	}
	
	
}
