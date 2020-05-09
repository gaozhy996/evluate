package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateChain;
import com.baizhi.update.UpdateOperator;

import java.util.HashSet;
import java.util.Set;

public class HistoryPasswordUpdater implements UpdateOperator {
    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdateChain updateChain) {
        Set<String> historyPasswords = historyData.getHistoryPasswords();
        if(historyPasswords == null){
            historyPasswords = new HashSet<>();
        }
        historyPasswords.add(loginSuccessData.getOrderlessPassword());
        historyData.setHistoryPasswords(historyPasswords);
        updateChain.doChain(loginSuccessData,historyData);
    }
}
