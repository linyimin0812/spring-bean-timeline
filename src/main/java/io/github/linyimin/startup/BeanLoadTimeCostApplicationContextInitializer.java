package io.github.linyimin.startup;

import io.github.linyimin.startup.container.IocContainerHolder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yiminlin
 * @date 2023/05/10 00:37
 **/
public class BeanLoadTimeCostApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private static final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        if (started.compareAndSet(false, true)) {
            IocContainerHolder.start();
        }

        if (applicationContext instanceof GenericApplicationContext) {
            BeanLoadTimeCostBeanFactory beanFactory = new BeanLoadTimeCostBeanFactory();
            Field field = null;
            try {
                field = GenericApplicationContext.class.getDeclaredField("beanFactory");
                field.setAccessible(true);
                field.set(applicationContext, beanFactory);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

