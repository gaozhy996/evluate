package com.baizhi.entity;

import java.io.Serializable;

/**
 * 评估项
 */
public class EvaluateItem implements Serializable {
    private String componentName;
    private Boolean value;

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "EvaluateItem{" +
                "componentName='" + componentName + '\'' +
                ", value=" + value +
                '}';
    }
}
