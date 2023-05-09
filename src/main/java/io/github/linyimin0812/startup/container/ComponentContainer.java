package io.github.linyimin0812.startup.container;

import io.github.linyimin0812.startup.reporter.JaegerReporter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yiminlin
 * @date 2023/05/10 00:41
 **/
public class ComponentContainer {

    private final MutablePicoContainer container = new PicoBuilder().withSetterInjection().withCaching().withLifecycle().build();

    private final AtomicBoolean started = new AtomicBoolean();

    private final AtomicBoolean stopped = new AtomicBoolean();

    public ComponentContainer() {
        // 服务在这里添加，接口和实现

        // TODO: 可配置，使用SPI机制提高扩展能力
        container.addComponent(JaegerReporter.class, JaegerReporter.class);
    }

    public <T> T getComponent(Class<T> clazz) {
        return container.getComponent(clazz);
    }

    /**
     * 启动服务容器
     */
    public final void start() {
        if (started.compareAndSet(false, true)) {
            container.start();
        }
    }

    /**
     * 停止容器
     */
    public final void stop() {
        if (stopped.compareAndSet(false, true)) {
            container.dispose();
        }
    }

    /**
     * 是否启动了
     *
     * @return 服务容器是否启动
     */
    public final boolean isStarted() {
        return started.get();
    }
}
