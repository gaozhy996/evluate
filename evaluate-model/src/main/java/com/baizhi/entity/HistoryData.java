package com.baizhi.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryData implements Serializable {
    private Set<String> historyCities;

    private Integer currentDayCounts;

    private Long lastLoginTime;

    private Double[] bPoint;

    private Set<String> historyDevices;

    private Map<String, Map<String, Long>> historyHabits;

    private Set<String> historyPasswords;

    private List<Double[]> historyVectors;

    public Set<String> getHistoryCities() {
        return historyCities;
    }

    public void setHistoryCities(Set<String> historyCities) {
        this.historyCities = historyCities;
    }

    public Integer getCurrentDayCounts() {
        return currentDayCounts;
    }

    public void setCurrentDayCounts(Integer currentDayCounts) {
        this.currentDayCounts = currentDayCounts;
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

    public Set<String> getHistoryDevices() {
        return historyDevices;
    }

    public void setHistoryDevices(Set<String> historyDevices) {
        this.historyDevices = historyDevices;
    }

    public Map<String, Map<String, Long>> getHistoryHabits() {
        return historyHabits;
    }

    public void setHistoryHabits(Map<String, Map<String, Long>> historyHabits) {
        this.historyHabits = historyHabits;
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
                ", currentDayCounts=" + currentDayCounts +
                ", lastLoginTime=" + lastLoginTime +
                ", bPoint=" + Arrays.toString(bPoint) +
                ", historyDevices=" + historyDevices +
                ", historyHabits=" + historyHabits +
                ", historyPasswords=" + historyPasswords +
                ", historyVectors=" + historyVectors +
                '}';
    }
}
