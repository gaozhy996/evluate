package com.baizhi.entity;

import java.io.Serializable;

public class EvaluateReportItem implements Serializable {
    private String key;
    private Boolean value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "EvaluateReportItem{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
