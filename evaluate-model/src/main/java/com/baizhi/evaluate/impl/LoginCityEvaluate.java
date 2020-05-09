package com.baizhi.evaluate.impl;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.EvaluateReportItem;
import com.baizhi.entity.HistoryData;
import com.baizhi.evaluate.EvaluateChain;
import com.baizhi.evaluate.EvaluateOperator;

import java.util.Set;

public class LoginCityEvaluate implements EvaluateOperator {

    @Override
    public void invoke(EvaluateData evaluateData, HistoryData historyData, EvaluateReport evaluateReport, EvaluateChain chain) {
        Boolean result = evaluateLoginCity(evaluateData.getRegion(), historyData.getHistoryCities());
        EvaluateReportItem item = new EvaluateReportItem();
        item.setKey("region");
        item.setValue(result);
        evaluateReport.getItems().add(item);
        chain.doEvaluate(evaluateData, historyData, evaluateReport);
    }

    /**
     * 基于用户登录城市信息
     *
     * @return true 有风险 false 无风险
     * @currentCity 当前城市
     * @historyCities 历史登录城市
     */
    public Boolean evaluateLoginCity(String currentCity, Set<String> historyCities) {
        // 首次登录
        if (historyCities == null || historyCities.size() == 0) {
            return false;
        }
        // true 常用城市登录
        // false 头一次出现
        return !historyCities.contains(currentCity);
    }
}
