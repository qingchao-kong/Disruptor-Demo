package com.qingchao.disruptordemo;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * 事件转换类
 */
public class LongEventTranslator implements EventTranslatorOneArg<LongEvent, Long> {
    @Override
    public void translateTo(LongEvent longEvent, long l, Long aLong) {
        longEvent.setNumber(aLong);
    }
}
