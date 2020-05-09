package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateChain;
import com.baizhi.update.UpdateOperator;

import java.util.HashSet;
import java.util.Set;

public class HistoryCitiesUpdater implements UpdateOperator {

    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdateChain updateChain) {
        String region = loginSuccessData.getRegion();
        Set<String> historyCities = historyData.getHistoryCities();
        if(historyCities == null){
            historyCities = new HashSet<>();
        }
        historyCities.add(region);
        historyData.setHistoryCities(historyCities);
        updateChain.doChain(loginSuccessData,historyData);
    }
}
