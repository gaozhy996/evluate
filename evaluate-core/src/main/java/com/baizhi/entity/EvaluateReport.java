package com.baizhi.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 评估报告的实体类
 */
public class EvaluateReport implements Serializable {
    private Long currentTime;
    private String applicationId;
    private String userId;
    private String loginSequence;
    private String region;
    // region true
    // distance false
    public List<EvaluateItem> items = new ArrayList<>();

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginSequence() {
        return loginSequence;
    }

    public void setLoginSequence(String loginSequence) {
        this.loginSequence = loginSequence;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<EvaluateItem> getItems() {
        return items;
    }

    public void setItems(List<EvaluateItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "EvaluateReport{" +
                "currentTime=" + currentTime +
                ", applicationId='" + applicationId + '\'' +
                ", userId='" + userId + '\'' +
                ", loginSequence='" + loginSequence + '\'' +
                ", region='" + region + '\'' +
                ", items=" + items +
                '}';
    }
}
