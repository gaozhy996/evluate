package com.baizhi.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryData implements Serializable {
    // 历史登录城市列表
    private Set<String> historyCities;
    // 上一次的登录时间
    private Long lastLoginTime;
    // 上一次登录坐标
    private Double[] bPoint;
    // 用户登录次数
    private Integer currentDayCounts;
    // 用户历史的登录设备列表
    private Set<String> historyDevices;
    // 用户历史的登录习惯
    private Map<String, Map<String, Long>> historyHabit;
    // 用户历史的登录明文乱序密码列表
    private Set<String> historyPasswords;
    // 用户历史的输入特征的向量列表
    private List<Double[]> historyVectors;

    public Set<String> getHistoryCities() {
        return historyCities;
    }

    public void setHistoryCities(Set<String> historyCities) {
        this.historyCities = historyCities;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Double[] getbPoint() {
        return bPoint;
    }

    public void setbPoint(Double[] bPoint) {
        this.bPoint = bPoint;
    }

    public Integer getCurrentDayCounts() {
        return currentDayCounts;
    }

    public void setCurrentDayCounts(Integer currentDayCounts) {
        this.currentDayCounts = currentDayCounts;
    }

    public Set<String> getHistoryDevices() {
        return historyDevices;
    }

    public void setHistoryDevices(Set<String> historyDevices) {
        this.historyDevices = historyDevices;
    }

    public Map<String, Map<String, Long>> getHistoryHabit() {
        return historyHabit;
    }

    public void setHistoryHabit(Map<String, Map<String, Long>> historyHabit) {
        this.historyHabit = historyHabit;
    }

    public Set<String> getHistoryPasswords() {
        return historyPasswords;
    }

    public void setHistoryPasswords(Set<String> historyPasswords) {
        this.historyPasswords = historyPasswords;
    }

    public List<Double[]> getHistoryVectors() {
        return historyVectors;
    }

    public void setHistoryVectors(List<Double[]> historyVectors) {
        this.historyVectors = historyVectors;
    }

    @Override
    public String toString() {
        return "HistoryData{" +
                "historyCities=" + historyCities +
                ", lastLoginTime=" + lastLoginTime +
                ", bPoint=" + Arrays.toString(bPoint) +
                ", currentDayCounts=" + currentDayCounts +
                ", historyDevices=" + historyDevices +
                ", historyHabit=" + historyHabit +
                ", historyPasswords=" + historyPasswords +
                ", historyVectors=" + historyVectors +
                '}';
    }
}
