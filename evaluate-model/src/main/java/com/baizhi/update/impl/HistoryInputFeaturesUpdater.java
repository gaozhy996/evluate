package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateChain;
import com.baizhi.update.UpdateOperator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryInputFeaturesUpdater implements UpdateOperator {

    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdateChain updateChain) {
        List<Double[]> historyVectors = historyData.getHistoryVectors();
        if(historyVectors == null){
            historyVectors = new ArrayList<>();
        }

        historyVectors.add(loginSuccessData.getInputFeature());
        if(historyVectors.size() > 10){
            historyVectors.remove(0);
        }
        historyData.setHistoryVectors(historyVectors);
        updateChain.doChain(loginSuccessData,historyData);
    }
}
