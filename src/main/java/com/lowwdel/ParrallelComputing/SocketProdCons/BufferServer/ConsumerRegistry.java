package com.lowwdel.ParrallelComputing.SocketProdCons.BufferServer;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ConsumerRegistry {
    private final Map<String, Set<ConsumerHandler>> consumerRegistry = new ConcurrentHashMap<>();
    private boolean isConsuming = false;//有至少一个消费者running
    public void registerConsumer(String tag, ConsumerHandler consumerHandler){
        consumerRegistry.computeIfAbsent(tag,k -> ConcurrentHashMap.newKeySet()).add(consumerHandler);
        updateIsConsuming();
    }

    public void unregisterConsumer(String tag,ConsumerHandler consumerHandler){
        //先获取某个tag的消费者集合
        Set<ConsumerHandler> consumerHandlers = consumerRegistry.get(tag);

        if(consumerHandlers!= null){
            //某个消费者取消订阅，从集合中移除该消费者
            consumerHandlers.remove(consumerHandler);
            if (consumerHandlers.isEmpty()){
                //如果该消费者删除后，集合没有其他同tag消费者，则移除该tag
                consumerRegistry.remove(tag);
            }
        }
        updateIsConsuming();//删除后，检查是否还有任意消费者running
    }

    public Set<ConsumerHandler> getConsumersByTag(String tag){
        return consumerRegistry.getOrDefault(tag, Collections.emptySet());
    }

    public void updateIsConsuming(){
        isConsuming = consumerRegistry.values().stream().anyMatch(consumers ->!consumers.isEmpty());
    }

    public boolean isConsuming() {
        return isConsuming;
    }
}
