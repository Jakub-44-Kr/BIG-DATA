package com.matrix;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;

public class DistributedSystemConfig {
    public static Config initialize() {
        Config configuration = new Config();
        JoinConfig networkJoin = configuration.getNetworkConfig().getJoin();
        networkJoin.getMulticastConfig().setEnabled(false);
        networkJoin.getTcpIpConfig().setEnabled(true)

                .addMember("192.168.195.22");
        return configuration;
    }
}
