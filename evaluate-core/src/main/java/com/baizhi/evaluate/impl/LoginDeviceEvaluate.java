package com.baizhi.evaluate.impl;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateItem;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;
import com.baizhi.evaluate.EvaluateChain;
import com.baizhi.evaluate.EvaluateOperator;

import java.util.Set;

public class LoginDeviceEvaluate implements EvaluateOperator {

    @Override
    public void invoke(EvaluateData evaluateData, HistoryData historyData, EvaluateReport evaluateReport, EvaluateChain chain) {
        Boolean value = evaluateLoginDevice(evaluateData.getUserAgent(), historyData.getHistoryDevices());
        EvaluateItem item = new EvaluateItem();
        item.setComponentName("device");
        item.setValue(value);
        evaluateReport.getItems().add(item);
        chain.doEvaluate(evaluateData, historyData, evaluateReport);
    }

    /**
     * 登录设备发生变化
     */
    public Boolean evaluateLoginDevice(String currentDevice, Set<String> historyDevices) {
        // 新用户 无历史的登录设备信息
        if (historyDevices == null || historyDevices.size() == 0) {
            return false;
        }
        //
        return !historyDevices.contains(currentDevice);
    }
}
