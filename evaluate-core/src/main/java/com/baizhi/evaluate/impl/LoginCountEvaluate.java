package com.baizhi.evaluate.impl;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateItem;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;
import com.baizhi.evaluate.EvaluateChain;
import com.baizhi.evaluate.EvaluateOperator;

public class LoginCountEvaluate implements EvaluateOperator {
    private Integer threshold;

    public LoginCountEvaluate(Integer threshold) {
        this.threshold = threshold;
    }

    @Override
    public void invoke(EvaluateData evaluateData, HistoryData historyData, EvaluateReport evaluateReport, EvaluateChain chain) {
        // 无论用户是否登录成功 都需要做登录次数的风险评估
        Integer historyCount = historyData.getCurrentDayCounts();
        if (historyCount == null) {
            historyCount = 0;
        }
        Boolean value = evaluateLoginCount(historyCount + 1, threshold);
        EvaluateItem item = new EvaluateItem();
        item.setComponentName("count");
        item.setValue(value);
        evaluateReport.getItems().add(item);
        chain.doEvaluate(evaluateData, historyData, evaluateReport);
    }

    /**
     * 频繁登录（登录次数限定）
     * map(day,count)
     */
    public Boolean evaluateLoginCount(Integer currentDayCounts, Integer threshold) {
        if (currentDayCounts == null || currentDayCounts == 0) {
            return false;
        }
        // 实际登录次数大于要求阈值  风险
        return currentDayCounts + 1 > threshold;
    }
}
