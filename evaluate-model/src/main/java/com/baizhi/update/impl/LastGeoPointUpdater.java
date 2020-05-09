package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateChain;
import com.baizhi.update.UpdateOperator;

public class LastGeoPointUpdater implements UpdateOperator {

    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdateChain updateChain) {
        historyData.setbPoint(loginSuccessData.getGeoPoint());
        historyData.setLastLoginTime(loginSuccessData.getCurrentTime());
    }
}
