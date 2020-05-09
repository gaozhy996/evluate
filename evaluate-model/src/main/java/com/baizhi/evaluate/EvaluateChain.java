package com.baizhi.evaluate;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;

import java.util.List;

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
        }
    }

    public void reset(){
        position = 0;
    }
}
