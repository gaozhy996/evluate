package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateOperator;
import com.baizhi.update.UpdaterChain;

import java.util.ArrayList;
import java.util.List;

public class HistoryInputFeatureUpdater implements UpdateOperator {
    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdaterChain chain) {
        // 保留10条最新的输入特征
        List<Double[]> historyVectors = historyData.getHistoryVectors();
        if (historyVectors == null) {
            historyVectors = new ArrayList<>();
        }
        if (historyVectors.size() > 10) {
            historyVectors.remove(0);
        }
        historyVectors.add(loginSuccessData.getInputFeature());
        historyData.setHistoryVectors(historyVectors);
        chain.doUpdate(loginSuccessData, historyData);
    }
}
