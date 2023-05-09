package io.github.linyimin.startup;

import io.github.linyimin.startup.container.IocContainerHolder;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.Stack;

/**
 * @author yiminlin
 * @date 2023/05/10 00:39
 **/
public class BeanLoadTimeCostBeanFactory extends DefaultListableBeanFactory {

    private final Object DUMMY = new Object();

    private static final ThreadLocal<Stack<BeanCreateResult>> parentStackThreadLocal = ThreadLocal.withInitial(Stack::new);

    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
        // 记录bean初始化开始
        BeanCreateResult beanCreateResult = new BeanCreateResult(beanName);

        if (!parentStackThreadLocal.get().empty()) {
            BeanCreateResult parentCreateResult = parentStackThreadLocal.get().peek();
            parentCreateResult.addChild(beanCreateResult);
            beanCreateResult.setHasParent(true);
        }
        // 入栈
        parentStackThreadLocal.get().push(beanCreateResult);

        Object bean = super.createBean(beanName, mbd, args);

        // bean初始化结束, 出栈
        parentStackThreadLocal.get().pop();

        beanCreateResult.setBeanEndTime(System.currentTimeMillis());
        IocContainerHolder.getBeanCreateResultMap().put(beanCreateResult, DUMMY);

        return bean;
    }
}
