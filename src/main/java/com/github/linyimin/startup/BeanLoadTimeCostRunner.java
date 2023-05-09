package com.github.linyimin.startup;

import com.github.linyimin.startup.container.IocContainerHolder;
import com.github.linyimin.startup.reporter.JaegerReporter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author yiminlin
 * @date 2023/05/10 00:39
 **/
@Component
public class BeanLoadTimeCostRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {

        JaegerReporter reporter = IocContainerHolder.getContainer().getComponent(JaegerReporter.class);

        reporter.reportBeanCreateResult(IocContainerHolder.getBeanCreateResultMap().keySet());

        IocContainerHolder.getContainer().stop();
    }
}

