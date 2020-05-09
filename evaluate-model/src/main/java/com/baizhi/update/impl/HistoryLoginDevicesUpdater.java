package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateChain;
import com.baizhi.update.UpdateOperator;

import java.util.HashSet;
import java.util.Set;

public class HistoryLoginDevicesUpdater implements UpdateOperator {

    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdateChain updateChain) {
        Set<String> historyDevices = historyData.getHistoryDevices();
        if (historyDevices == null) {
            historyDevices = new HashSet<>();
        }
        historyDevices.add(loginSuccessData.getUserAgent());
        historyData.setHistoryDevices(historyDevices);
        updateChain.doChain(loginSuccessData, historyData);
    }
}
