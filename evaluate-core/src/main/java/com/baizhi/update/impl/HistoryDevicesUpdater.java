package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateOperator;
import com.baizhi.update.UpdaterChain;

import java.util.HashSet;
import java.util.Set;

/**
 * 历史登录设备列表的数据更新
 */
public class HistoryDevicesUpdater implements UpdateOperator {

    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdaterChain chain) {
        Set<String> historyDevices = historyData.getHistoryDevices();
        if (historyDevices == null) {
            historyDevices = new HashSet<>();
        }
        historyDevices.add(loginSuccessData.getUserAgent());
        historyData.setHistoryDevices(historyDevices);
        chain.doUpdate(loginSuccessData, historyData);
    }
}
