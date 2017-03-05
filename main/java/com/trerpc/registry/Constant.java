package com.trerpc.registry;


/**
 * 这个接口中定义各种常量
 * @author trecool
 *
 */
public interface Constant {
    int ZK_SESSION_TIMEOUT = 5000;

    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
