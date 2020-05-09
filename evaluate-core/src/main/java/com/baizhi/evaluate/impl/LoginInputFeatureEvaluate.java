package com.baizhi.evaluate.impl;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateItem;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;
import com.baizhi.evaluate.EvaluateChain;
import com.baizhi.evaluate.EvaluateOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LoginInputFeatureEvaluate implements EvaluateOperator {

    @Override
    public void invoke(EvaluateData evaluateData, HistoryData historyData, EvaluateReport evaluateReport, EvaluateChain chain) {
        Boolean value = evaluateInputFeatures(evaluateData.getInputFeature(), historyData.getHistoryVectors());
        EvaluateItem item = new EvaluateItem();
        item.setComponentName("input_feature");
        item.setValue(value);
        evaluateReport.getItems().add(item);
        chain.doEvaluate(evaluateData, historyData, evaluateReport);
    }

    /**
     * 用户输入特征的相似度的判定
     *
     * @currentVector 业务系统提供当前登录的输入特征向量  如：[100,200,300]
     * @historyVectors 用户历史的登录行为输入特征向量(只保留用户最新的10个特征向量)
     */
    public Boolean evaluateInputFeatures(Double[] currentVector, List<Double[]> historyVectors) {
        if (historyVectors == null || historyVectors.size() < 2) {
            return false;
        }
        // 1. 首先计算出历史的输入特征向量的中心点
        // [100,200,300]  [200,300,400]  ==> [150,250,350]
        // 相同纬度的值相加 / 长度 = 中心点纬度值
        Double[] sumVector = new Double[currentVector.length];
        for (Double[] historyVector : historyVectors) {
            for (int i = 0; i < historyVector.length; i++) {
                if (sumVector[i] == null) {
                    sumVector[i] = 0.0;
                }
                sumVector[i] += historyVector[i];
            }
        }
        Double[] centerPoint = new Double[currentVector.length];
        for (int i = 0; i < centerPoint.length; i++) {
            centerPoint[i] = sumVector[i] / historyVectors.size();
        }
        Arrays.stream(centerPoint).forEach(n -> System.out.print(n + ","));

        // 2. 求出历史登录特征两两之间的距离
        // p1: [A,B]  p2:[X,Y]    p1->p2
        // p1: [A,B]  p2:[X,Y] P3:[M,N]  p1->p2  p1->p3 p2->p3
        // p1: [A,B]  p2:[X,Y] P3:[M,N] p4:[J,K]  p1->p2  p1->p3 p1->p4  p2->p3 p2->p4  p3->p4
        // 等差数列 n*(n-1)/2 * 1/3
        List<Double> distances = new ArrayList<>();
        for (int i = 0; i < historyVectors.size(); i++) {
            Double[] p1 = historyVectors.get(i);
            for (int j = i + 1; j < historyVectors.size(); j++) {
                Double[] p2 = historyVectors.get(j);
                // 求两点之间的距离
                Double sum = 0.0;
                for (int k = 0; k < p1.length; k++) {
                    sum += Math.pow(p1[k] - p2[k], 2);
                }
                Double distance = Math.sqrt(sum);
                distances.add(distance);
            }
        }
        System.out.println(distances);

        // 3. 对历史行为特征距离集合的样本数据 升序排列，取1/3位置的结果作为 判定阈值
        distances = distances.stream().sorted().collect(Collectors.toList());

        // 计算安全阈值
        double threshold = distances.get(historyVectors.size() * (historyVectors.size() - 1) / 6);

        // 4. 当前输入点和中心点距离
        Double sum = 0.0;

        for (int i = 0; i < currentVector.length; i++) {
            sum += Math.pow(currentVector[i] - centerPoint[i], 2);
        }

        System.out.println("当前输入点和中心点距离：" + Math.sqrt(sum) + "  安全阈值：" + threshold);

        return Math.sqrt(sum) > threshold;
    }
}
