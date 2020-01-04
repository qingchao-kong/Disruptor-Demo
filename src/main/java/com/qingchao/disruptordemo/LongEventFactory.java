package com.qingchao.disruptordemo;

import com.lmax.disruptor.EventFactory;

/**
 * 事件工厂类
 */
public class LongEventFactory implements EventFactory<LongEvent> {
    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
