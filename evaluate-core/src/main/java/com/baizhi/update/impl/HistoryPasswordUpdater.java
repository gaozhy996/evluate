package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateOperator;
import com.baizhi.update.UpdaterChain;

import java.util.HashSet;
import java.util.Set;

/**
 * 更新历史数据的乱序明文密码列表
 */
public class HistoryPasswordUpdater implements UpdateOperator {

    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdaterChain chain) {
        Set<String> historyPasswords = historyData.getHistoryPasswords();
        if (historyPasswords == null) {
            historyPasswords = new HashSet<>();
        }
        historyPasswords.add(loginSuccessData.getOrderlessPassword());
        historyData.setHistoryPasswords(historyPasswords);
        chain.doUpdate(loginSuccessData, historyData);
    }
}
