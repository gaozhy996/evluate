package com.baizhi.evaluate.impl;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateItem;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;
import com.baizhi.evaluate.EvaluateChain;
import com.baizhi.evaluate.EvaluateOperator;

import java.util.*;
import java.util.stream.Collectors;

public class LoginPasswordEvaluate implements EvaluateOperator {
    private Double threshold;

    public LoginPasswordEvaluate(Double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void invoke(EvaluateData evaluateData, HistoryData historyData, EvaluateReport evaluateReport, EvaluateChain chain) {
        Boolean value = evaluateLoginPasswordSimilarity(evaluateData.getOrderlessPassword(), historyData.getHistoryPasswords(), threshold);
        EvaluateItem item = new EvaluateItem();
        item.setComponentName("password");
        item.setValue(value);
        evaluateReport.getItems().add(item);
        chain.doEvaluate(evaluateData, historyData, evaluateReport);
    }

    /**
     * 密码相似特征评估
     *
     * @currentPassword 当前登录密码
     * @historyPasswords 用户的历史密码的集合
     * @threshold 安全阈值
     */
    public Boolean evaluateLoginPasswordSimilarity(String currentPassword, Set<String> historyPasswords, Double threshold) {
        if (historyPasswords == null || historyPasswords.size() == 0) {
            return false;
        }

        // 建立词袋子模型（历史密码 + 当前密码）  bag of words model
        Set<Character> bagOfWords = new HashSet<>();
        char[] currentPasswordCharArray = currentPassword.toCharArray();
        for (int i = 0; i < currentPasswordCharArray.length; i++) {
            bagOfWords.add(currentPasswordCharArray[i]);
        }

        for (String historyPassword : historyPasswords) {
            char[] historyPasswordCharArray = historyPassword.toCharArray();
            for (int i = 0; i < historyPasswordCharArray.length; i++) {
                bagOfWords.add(historyPasswordCharArray[i]);
            }
        }
        // 建立最终的词袋子模型
        List<Character> bagOfWordsModel = bagOfWords.stream().sorted().collect(Collectors.toList());
        bagOfWordsModel.stream().forEach(n -> System.out.print(n + "\t"));
        System.out.println();
        // 建立当前登录密码向量模型
        // map记录 当前密码中字符的出现次数
        // aba123
        // a 2
        // b 1
        // 1 1
        // 2 1
        // 3 1
        HashMap<Character, Integer> charMap = new HashMap<>();
        for (int i = 0; i < currentPasswordCharArray.length; i++) {
            char c = currentPasswordCharArray[i];
            if (charMap.containsKey(c)) {
                charMap.put(c, charMap.get(c) + 1);
            } else {
                charMap.put(c, 1);
            }
        }
        // 向量的纬度等价于词袋子长度
        Integer[] currentPasswordVector = new Integer[bagOfWordsModel.size()];
        for (int i = 0; i < bagOfWordsModel.size(); i++) {
            if (charMap.containsKey(bagOfWordsModel.get(i))) {
                currentPasswordVector[i] = charMap.get(bagOfWordsModel.get(i));
            } else {
                currentPasswordVector[i] = 0;
            }
        }
        Arrays.stream(currentPasswordVector).forEach(n -> System.out.print(n + "\t"));

        // 建立历史登录密码的向量
        List<Integer[]> historyPasswordVectors = new ArrayList<>();
        for (String historyPassword : historyPasswords) {
            HashMap<Character, Integer> tempCharMap = new HashMap<>();
            char[] historyPasswordCharArray = historyPassword.toCharArray();
            for (int i = 0; i < historyPasswordCharArray.length; i++) {
                char c = historyPasswordCharArray[i];
                if (tempCharMap.containsKey(c)) {
                    tempCharMap.put(c, tempCharMap.get(c) + 1);
                } else {
                    tempCharMap.put(c, 1);
                }
            }
            // 向量的纬度等价于词袋子长度
            Integer[] historyPasswordVector = new Integer[bagOfWordsModel.size()];
            for (int i = 0; i < bagOfWordsModel.size(); i++) {
                if (tempCharMap.containsKey(bagOfWordsModel.get(i))) {
                    historyPasswordVector[i] = tempCharMap.get(bagOfWordsModel.get(i));
                } else {
                    historyPasswordVector[i] = 0;
                }
            }
            historyPasswordVectors.add(historyPasswordVector);
        }
        // 求向量夹角
        for (Integer[] historyPasswordVector : historyPasswordVectors) {
            Double fz = 0.0;
            for (int i = 0; i < historyPasswordVector.length; i++) {
                fz += historyPasswordVector[i] * currentPasswordVector[i];
            }

            Double fm = Math.sqrt(Arrays.stream(historyPasswordVector).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get())
                    * Math.sqrt(Arrays.stream(currentPasswordVector).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get());
            System.out.println("相似度：" + fz / fm);
            if ((fz / fm) > threshold) {
                return false;
            }
        }
        return true;
    }
}
