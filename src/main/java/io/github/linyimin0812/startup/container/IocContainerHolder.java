package io.github.linyimin0812.startup.container;

import io.github.linyimin0812.startup.BeanCreateResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yiminlin
 * @date 2023/05/10 00:42
 **/
public class IocContainerHolder {

    private static ComponentContainer container;

    private static final Map<BeanCreateResult, Object> beanCreateResultMap = new ConcurrentHashMap<>();

    public static void start() {
        container = new ComponentContainer();
        container.start();

    }

    public static ComponentContainer getContainer() {
        return container;
    }

    public static Map<BeanCreateResult, Object> getBeanCreateResultMap() {
        return beanCreateResultMap;
    }
}
