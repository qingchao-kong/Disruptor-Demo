package com.qingchao.disruptordemo;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * 该消费者执行将数值+10的操作。可以看到该消费者同时实现了EventHandler和WorkHandler两个接口。如果不需要池化，
 * 只需要实现EventHandler类即可。如果需要池化，只需要实现WorkHandler类即可。
 */
public class C11EventHandler implements EventHandler<LongEvent>, WorkHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        long number = longEvent.getNumber();
        number += 10;
        System.out.println(System.currentTimeMillis() + ":c1-1 consumer finished.number=" + number);
    }

    /**
     * 池化
     *
     * @param longEvent
     * @throws Exception
     */
    @Override
    public void onEvent(LongEvent longEvent) throws Exception {
        long number = longEvent.getNumber();
        number += 10;
        System.out.println(System.currentTimeMillis() + ":c1-1 consumer finished.number=" + number);
    }
}
