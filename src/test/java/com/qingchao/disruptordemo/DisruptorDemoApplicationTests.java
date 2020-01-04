package com.qingchao.disruptordemo;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;

@SpringBootTest
class DisruptorDemoApplicationTests {
    private static String parallel = "parallel";
    private static String serial = "serial";
    private static String diamond = "diamond";
    private static String chain = "chain";
    private static String parallelWithPool = "parallelWithPool";
    private static String serialWithPool = "serialWithPool";

    @Test
    void contextLoads() throws Exception {
        baseTest(parallel);
        baseTest(serial);
        baseTest(diamond);
        baseTest(chain);
        baseTest(parallelWithPool);
        baseTest(serialWithPool);
    }

    private static void baseTest(String methodName) throws Exception {
        int bufferSize = 1024;//环形队列长度，必须是2的N次方
        EventFactory<LongEvent> eventFactory = new LongEventFactory();
        //定义Disruptor，基于单生产者，阻塞策略
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(eventFactory,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy());
        Class<DisruptorDemoApplicationTests> clazz = DisruptorDemoApplicationTests.class;
        Method method = clazz.getMethod(methodName, Disruptor.class);
        method.invoke(null, disruptor);//这里是调用各种不同方法的地方.
        disruptor.start();
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        /**
         * 输入10
         */
        ringBuffer.publishEvent(new LongEventTranslator(), 10L);
        ringBuffer.publishEvent(new LongEventTranslator(), 100L);
    }

    /**
     * 并行计算实现
     * .........................|-> consumer1
     * producer -> ringbuffer -|-> consumer2
     * .......................|-> consumer3
     *
     * @param disruptor
     */
    public static void parallel(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWith(new C11EventHandler(), new C21EventHandler());
    }

    /**
     * 串行计算
     * producer -> ringbuffer -> consumer1 -> consumer2 -> consumer3
     *
     * @param disruptor
     */
    public static void serial(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWith(new C11EventHandler()).then(new C21EventHandler());
    }

    /**
     * 菱形方式执行
     * .........................|-> consumer1 -|
     * producer -> ringbuffer -|..............|-> consumer3
     * .......................|-> consumer2 -|
     *
     * @param disruptor
     */
    public static void diamond(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWith(new C11EventHandler(), new C12EventHandler()).then(new C21EventHandler());
    }

    /**
     * 链式并行计算
     * .........................|-> consumer1-1 -> consumer1-2
     * producer -> ringbuffer -|
     * .......................|-> consumer2-1 -> consumer2-2
     *
     * @param disruptor
     */
    public static void chain(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWith(new C11EventHandler()).then(new C12EventHandler());
        disruptor.handleEventsWith(new C21EventHandler()).then(new C22EventHandler());
    }

    /**
     * 见resource.uml.并行计算实现,c1,c2互相不依赖,同时C1，C2分别有2个实例.png
     *
     * @param disruptor
     */
    public static void parallelWithPool(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWithWorkerPool(new C11EventHandler(), new C11EventHandler());
        disruptor.handleEventsWithWorkerPool(new C21EventHandler(), new C21EventHandler());
    }

    /**
     * 见resource.uml.串行依次执行,同时C11，C21分别有2个实例.png
     *
     * @param disruptor
     */
    public static void serialWithPool(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWithWorkerPool(new C11EventHandler(), new C11EventHandler()).thenHandleEventsWithWorkerPool(new C21EventHandler(), new C21EventHandler());
    }
}
