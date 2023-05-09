package io.github.linyimin.startup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiminlin
 * @date 2023/05/10 00:36
 **/
public class BeanCreateResult {

    private String beanClassName;
    private long beanStartTime;
    private long beanEndTime;
    private String loadThreadName;

    private boolean hasParent;

    private List<BeanCreateResult> children;

    public BeanCreateResult(String beanClassName) {
        this.beanClassName = beanClassName;
        this.beanStartTime = System.currentTimeMillis();
        this.loadThreadName = Thread.currentThread().getName();
        this.children = new ArrayList<>();
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public long getBeanStartTime() {
        return beanStartTime;
    }

    public void setBeanStartTime(long beanStartTime) {
        this.beanStartTime = beanStartTime;
    }

    public List<BeanCreateResult> getChildren() {
        return children;
    }

    public void setChildren(List<BeanCreateResult> children) {
        this.children = children;
    }

    public String getLoadThreadName() {
        return loadThreadName;
    }

    public void setLoadThreadName(String loadThreadName) {
        this.loadThreadName = loadThreadName;
    }

    public long getBeanEndTime() {
        return beanEndTime;
    }

    public void setBeanEndTime(long beanEndTime) {
        this.beanEndTime = beanEndTime;
    }

    public boolean isHasParent() {
        return hasParent;
    }

    public void setHasParent(boolean hasParent) {
        this.hasParent = hasParent;
    }

    public void addChild(BeanCreateResult child) {
        this.children.add(child);
    }
}

