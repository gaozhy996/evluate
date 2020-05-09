package com.baizhi.evaluate;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;

import java.util.List;

/**
 * 评估链路对象
 */
public class EvaluateChain {
    private List<EvaluateOperator> operators;
    private int position;

    public EvaluateChain(List<EvaluateOperator> operators) {
        this.operators = operators;
        this.position = 0;
    }

    public void doEvaluate(EvaluateData evaluateData, HistoryData historyData, EvaluateReport evaluateReport) {
        if (operators != null && position < operators.size()) {
            EvaluateOperator operator = operators.get(position);
            position++;
            operator.invoke(evaluateData, historyData, evaluateReport, this);
        } else{
            evaluateReport.setCurrentTime(evaluateData.getCurrentTime());
            evaluateReport.setApplicationId(evaluateData.getApplicationId());
            evaluateReport.setLoginSequence(evaluateData.getLoginSequence());
            evaluateReport.setUserId(evaluateData.getUserId());
            evaluateReport.setRegion(evaluateData.getRegion());
        }
    }
}
