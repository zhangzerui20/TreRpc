# TreRpc
the RPC framework with Netty and Zookeeper.

We use Zookeeper for the Service discovery, and use Netty for the Network transmission.

QUICK START:

you will use the syn RPC call by this:
```
		ServiceDiscovery discovery = new ServiceDiscovery("127.0.0.1:2181");
		RpcProxy proxy = new RpcProxy(discovery);
		
		TestService hello = proxy.create(TestService.class);
		String res = hello.helloService("trecool");
```

or you will use the asyn RPC call by this:

```
		ServiceDiscovery discovery = new ServiceDiscovery("127.0.0.1:2181");
		RpcProxy proxy = new RpcProxy(discovery);
    
		RpcCaller caller = proxy.createAsyn(TestService2.class);
		RpcFuture future = caller.call("add", 5, 7);
		int resa = (Integer) future.get();
		LOGGER.error("res is " + resa);
```
