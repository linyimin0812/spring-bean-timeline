package io.github.linyimin0812.startup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yiminlin
 * @date 2023/05/10 02:59
 **/
@Configuration
public class StartupConfiguration {
    @Bean
    public BeanLoadTimeCostRunner beanLoadTimeCostRunner() {
        return new BeanLoadTimeCostRunner();
    }
}
