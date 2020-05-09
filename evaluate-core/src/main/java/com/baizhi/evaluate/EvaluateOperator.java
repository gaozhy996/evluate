package com.baizhi.evaluate;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;

/**
 * 评估模型的组件接口
 */
public interface EvaluateOperator {

    public void invoke(EvaluateData evaluateData, HistoryData historyData, EvaluateReport evaluateReport,EvaluateChain chain);
}
